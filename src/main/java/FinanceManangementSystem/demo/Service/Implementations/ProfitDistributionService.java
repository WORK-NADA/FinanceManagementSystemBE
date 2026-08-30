package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Model.Partner;
import FinanceManangementSystem.demo.Model.PartnerProfitShare;
import FinanceManangementSystem.demo.Model.ProfitDistribution;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestProfitDistributionDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseProfitDistributionDTO;
import FinanceManangementSystem.demo.Repository.PartnerProfitShareRepository;
import FinanceManangementSystem.demo.Repository.PartnerRepository;
import FinanceManangementSystem.demo.Repository.ProfitDistributionRepository;
import FinanceManangementSystem.demo.Repository.PurchaseRepository;
import FinanceManangementSystem.demo.Repository.SaleRepository;
import FinanceManangementSystem.demo.Service.ExpenseServiceInterface;
import FinanceManangementSystem.demo.Service.ProfitDistributionServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfitDistributionService implements ProfitDistributionServiceInterface {

    private final SaleRepository saleRepo;

    private final PurchaseRepository purchaseRepo;

    private final ExpenseServiceInterface expenseService;

    private final PartnerRepository partnerRepo;

    private final ProfitDistributionRepository distributionRepo;

    private final PartnerProfitShareRepository shareRepo;

    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public ResponseProfitDistributionDTO calculateAndDistribute(RequestProfitDistributionDTO dto) {
        log.info("SERVICE - request came in calculateAndDistribute...");

        LocalDate from = dto.getFromDate();
        LocalDate to = dto.getToDate();

        if (from.isAfter(to)) {
            throw new RuntimeException("fromDate must be before or equal to toDate");
        }

        if (distributionRepo.existsByFromDateAndToDate(from, to)) {
            throw new RuntimeException("Profit already distributed for this period");
        }

        List<Partner> activePartners = partnerRepo.findByIsActiveTrue();

        if (activePartners.isEmpty()) {
            throw new RuntimeException("No active partners to distribute profit");
        }

        BigDecimal activeSum = partnerRepo.sumActiveSharePercentage();
        if (activeSum == null) activeSum = BigDecimal.ZERO;

        if (activeSum.compareTo(new BigDecimal("100.00")) != 0) {
            throw new RuntimeException("Active partner shares must total exactly 100% before distribution");
        }

        // Compute totals
        BigDecimal totalRevenue = saleRepo.findBySaleDateBetween(from, to)
                .stream()
                .map(s -> s.getTotalAmount() == null ? BigDecimal.ZERO : s.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPurchaseCost = purchaseRepo.findByPurchaseDateBetween(from, to)
                .stream()
                .map(p -> p.getTotalAmount() == null ? BigDecimal.ZERO : p.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = expenseService.getTotalExpenses(from, to);
        if (totalExpenses == null) totalExpenses = BigDecimal.ZERO;

        BigDecimal netProfit = totalRevenue.subtract(totalPurchaseCost).subtract(totalExpenses);

        if (netProfit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("No profit to distribute for this period");
        }

        // Persist distribution record
        ProfitDistribution dist = new ProfitDistribution();
        dist.setFromDate(from);
        dist.setToDate(to);
        dist.setTotalRevenue(totalRevenue);
        dist.setTotalPurchaseCost(totalPurchaseCost);
        dist.setTotalExpenses(totalExpenses);
        dist.setNetProfit(netProfit);

        dist = distributionRepo.save(dist);

        // Sort partners deterministically by publicId string
        List<Partner> sortedPartners = new ArrayList<>(activePartners);
        sortedPartners.sort(Comparator.comparing(p -> p.getPublicId().toString()));

        List<ResponseProfitDistributionDTO.PartnerShareDetails> shareDetails = new ArrayList<>();

        BigDecimal sumRounded = BigDecimal.ZERO;

        List<PartnerProfitShare> toSave = new ArrayList<>();

        for (Partner partner : sortedPartners) {

            BigDecimal rawShare = netProfit.multiply(partner.getSharePercentage())
                    .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

            BigDecimal rounded = rawShare.setScale(2, RoundingMode.HALF_UP);

            PartnerProfitShare s = new PartnerProfitShare();
            s.setDistribution(dist);
            s.setPartner(partner);
            s.setSharePercentageAtDistribution(partner.getSharePercentage());
            s.setShareAmount(rounded);

            toSave.add(s);

            sumRounded = sumRounded.add(rounded);

            ResponseProfitDistributionDTO.PartnerShareDetails pd = new ResponseProfitDistributionDTO.PartnerShareDetails();
            pd.setPartnerPublicId(partner.getPublicId());
            pd.setPartnerName(partner.getPartnerName());
            pd.setSharePercentageAtDistribution(partner.getSharePercentage());
            pd.setShareAmount(rounded);

            shareDetails.add(pd);
        }

        BigDecimal remainder = netProfit.subtract(sumRounded);

        if (remainder.compareTo(BigDecimal.ZERO) != 0) {
            // Add remainder to last partner
            PartnerProfitShare last = toSave.get(toSave.size() - 1);
            last.setShareAmount(last.getShareAmount().add(remainder));

            // Update response shareDetails last element
            ResponseProfitDistributionDTO.PartnerShareDetails lastPd = shareDetails.get(shareDetails.size() - 1);
            lastPd.setShareAmount(lastPd.getShareAmount().add(remainder));
        }

        // Save shares
        shareRepo.saveAll(toSave);

        ResponseProfitDistributionDTO resp = new ResponseProfitDistributionDTO();
        resp.setPublicId(dist.getPublicId());
        resp.setFromDate(dist.getFromDate());
        resp.setToDate(dist.getToDate());
        resp.setTotalRevenue(dist.getTotalRevenue());
        resp.setTotalPurchaseCost(dist.getTotalPurchaseCost());
        resp.setTotalExpenses(dist.getTotalExpenses());
        resp.setNetProfit(dist.getNetProfit());
        resp.setCreatedAt(dist.getCreatedAt());
        resp.setShares(shareDetails);

        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseProfitDistributionDTO getDistributionByPublicId(UUID publicId) {
        ProfitDistribution dist = distributionRepo.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("Distribution not found"));

        ResponseProfitDistributionDTO resp = modelMapper.map(dist, ResponseProfitDistributionDTO.class);

        List<PartnerProfitShare> shares = shareRepo.findByDistribution(dist);

        List<ResponseProfitDistributionDTO.PartnerShareDetails> details = shares.stream().map(s -> {
            ResponseProfitDistributionDTO.PartnerShareDetails pd = new ResponseProfitDistributionDTO.PartnerShareDetails();
            pd.setPartnerPublicId(s.getPartner().getPublicId());
            pd.setPartnerName(s.getPartner().getPartnerName());
            pd.setSharePercentageAtDistribution(s.getSharePercentageAtDistribution());
            pd.setShareAmount(s.getShareAmount());
            return pd;
        }).collect(Collectors.toList());

        resp.setShares(details);

        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseProfitDistributionDTO> getAllDistributions() {
        List<ProfitDistribution> dists = distributionRepo.findAllByOrderByToDateDesc();

        return dists.stream().map(dist -> {
            ResponseProfitDistributionDTO resp = modelMapper.map(dist, ResponseProfitDistributionDTO.class);
            List<PartnerProfitShare> shares = shareRepo.findByDistribution(dist);
            List<ResponseProfitDistributionDTO.PartnerShareDetails> details = shares.stream().map(s -> {
                ResponseProfitDistributionDTO.PartnerShareDetails pd = new ResponseProfitDistributionDTO.PartnerShareDetails();
                pd.setPartnerPublicId(s.getPartner().getPublicId());
                pd.setPartnerName(s.getPartner().getPartnerName());
                pd.setSharePercentageAtDistribution(s.getSharePercentageAtDistribution());
                pd.setShareAmount(s.getShareAmount());
                return pd;
            }).collect(Collectors.toList());
            resp.setShares(details);
            return resp;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseProfitDistributionDTO.PartnerShareDetails> getShareHistoryByPartner(UUID partnerPublicId) {
        return shareRepo.findByPartner_PublicIdOrderByCreatedAtDesc(partnerPublicId)
                .stream()
                .map(s -> {
                    ResponseProfitDistributionDTO.PartnerShareDetails pd = new ResponseProfitDistributionDTO.PartnerShareDetails();
                    pd.setPartnerPublicId(s.getPartner().getPublicId());
                    pd.setPartnerName(s.getPartner().getPartnerName());
                    pd.setSharePercentageAtDistribution(s.getSharePercentageAtDistribution());
                    pd.setShareAmount(s.getShareAmount());
                    return pd;
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseProfitDistributionDTO getLatestDistribution() {
        ProfitDistribution dist = distributionRepo.findFirstByOrderByToDateDesc()
                .orElseThrow(() -> new RuntimeException("No distributions found"));

        return getDistributionByPublicId(dist.getPublicId());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getLifetimeEarningsByPartner(UUID partnerPublicId) {
        BigDecimal sum = shareRepo.sumLifetimeEarningsByPartner(partnerPublicId);
        return sum == null ? BigDecimal.ZERO : sum;
    }
}
