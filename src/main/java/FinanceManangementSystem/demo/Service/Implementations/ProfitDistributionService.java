package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Exceptions.InvalidRequestException;
import FinanceManangementSystem.demo.Exceptions.ResourceNotFoundException;

import FinanceManangementSystem.demo.Model.Partner;
import FinanceManangementSystem.demo.Model.PartnerProfitShare;
import FinanceManangementSystem.demo.Model.ProfitDistribution;
import FinanceManangementSystem.demo.Model.User;
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

    private final CurrentUserService currentUserService;

    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public ResponseProfitDistributionDTO calculateAndDistribute(RequestProfitDistributionDTO dto) {
        log.info("SERVICE - request came in calculateAndDistribute...");

        LocalDate from = dto.getFromDate();
        LocalDate to = dto.getToDate();

        if (from.isAfter(to)) {
            throw new InvalidRequestException("fromDate must be before or equal to toDate");
        }

        User currentUser = currentUserService.getCurrentUser();

        if (distributionRepo.existsByUserAndFromDateAndToDate(currentUser, from, to)) {
            throw new InvalidRequestException("Profit already distributed for this period");
        }

        List<Partner> activePartners = partnerRepo.findByUserAndIsActiveTrue(currentUser);

        if (activePartners.isEmpty()) {
            throw new InvalidRequestException("No active partners to distribute profit");
        }

        BigDecimal activeSum = partnerRepo.sumActiveSharePercentage(currentUser);
        if (activeSum == null) activeSum = BigDecimal.ZERO;

        if (activeSum.compareTo(new BigDecimal("100.00")) != 0) {
            throw new InvalidRequestException("Active partner shares must total exactly 100% before distribution");
        }

        // Compute totals
        BigDecimal totalRevenue = saleRepo.findByUserAndSaleDateBetween(currentUser, from, to)
                .stream()
                .map(s -> s.getTotalAmount() == null ? BigDecimal.ZERO : s.getTotalAmount())
                .reduce(BigDecimal.ZERO, (acc, value) -> acc.add(value == null ? BigDecimal.ZERO : value));

        BigDecimal totalPurchaseCost = purchaseRepo.findByUserAndPurchaseDateBetween(currentUser, from, to)
                .stream()
                .map(p -> p.getTotalAmount() == null ? BigDecimal.ZERO : p.getTotalAmount())
                .reduce(BigDecimal.ZERO, (acc, value) -> acc.add(value == null ? BigDecimal.ZERO : value));

        BigDecimal totalExpenses = expenseService.getTotalExpenses(currentUser, from, to);
        if (totalExpenses == null) totalExpenses = BigDecimal.ZERO;

        BigDecimal netProfit = totalRevenue.subtract(totalPurchaseCost).subtract(totalExpenses);

        if (netProfit.compareTo(BigDecimal.ZERO) < 0) {
            netProfit = BigDecimal.ZERO;
        }

        // Persist distribution record
        ProfitDistribution dist = new ProfitDistribution();
        dist.setUser(currentUser);
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
        User currentUser = currentUserService.getCurrentUser();

        ProfitDistribution dist;
        if (currentUser.getRole() == FinanceManangementSystem.demo.Enums.UserRole.ADMIN) {
            dist = distributionRepo.findByPublicId(publicId)
                    .orElseThrow(() -> new ResourceNotFoundException("Distribution not found"));
        } else {
            dist = distributionRepo.findByUserAndPublicId(currentUser, publicId)
                    .orElseThrow(() -> new ResourceNotFoundException("Distribution not found"));
        }

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
        User currentUser = currentUserService.getCurrentUser();

        List<ProfitDistribution> dists;
        if (currentUser.getRole() == FinanceManangementSystem.demo.Enums.UserRole.ADMIN) {
            dists = distributionRepo.findAllByOrderByToDateDesc();
        } else {
            dists = distributionRepo.findByUserOrderByToDateDesc(currentUser);
        }

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
        User currentUser = currentUserService.getCurrentUser();
        List<PartnerProfitShare> shares;

        if (currentUser.getRole() == FinanceManangementSystem.demo.Enums.UserRole.ADMIN) {
            shares = shareRepo.findByPartner_PublicIdOrderByCreatedAtDesc(partnerPublicId);
        } else {
            shares = shareRepo.findByPartner_PublicIdAndDistribution_UserOrderByCreatedAtDesc(partnerPublicId, currentUser);
        }

        return shares.stream()
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
        User currentUser = currentUserService.getCurrentUser();
        ProfitDistribution dist;

        if (currentUser.getRole() == FinanceManangementSystem.demo.Enums.UserRole.ADMIN) {
            dist = distributionRepo.findFirstByOrderByToDateDesc()
                    .orElse(null);
        } else {
            dist = distributionRepo.findFirstByUserOrderByToDateDesc(currentUser)
                    .orElse(null);
        }

        if (dist == null) {
            return null;
        }

        return getDistributionByPublicId(dist.getPublicId());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getLifetimeEarningsByPartner(UUID partnerPublicId) {
        User currentUser = currentUserService.getCurrentUser();
        BigDecimal sum;

        if (currentUser.getRole() == FinanceManangementSystem.demo.Enums.UserRole.ADMIN) {
            sum = shareRepo.sumLifetimeEarningsByPartner(partnerPublicId);
        } else {
            sum = shareRepo.sumLifetimeEarningsByPartnerAndUser(partnerPublicId, currentUser);
        }
        return sum == null ? BigDecimal.ZERO : sum;
    }
}
