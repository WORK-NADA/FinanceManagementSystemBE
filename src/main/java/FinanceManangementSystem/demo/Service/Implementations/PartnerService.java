package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Model.Partner;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestPartnerDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponsePartnerDTO;
import FinanceManangementSystem.demo.Repository.PartnerProfitShareRepository;
import FinanceManangementSystem.demo.Repository.PartnerRepository;
import FinanceManangementSystem.demo.Service.PartnerServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartnerService implements PartnerServiceInterface {

    private final PartnerRepository partnerRepo;

    private final PartnerProfitShareRepository partnerProfitShareRepo;

    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public ResponsePartnerDTO addPartner(RequestPartnerDTO dto) {

        log.info("SERVICE - request came in addPartner...");

        if (partnerRepo.existsByMobileNumber(dto.getMobileNumber())) {
            log.info("SERVICE - partner mobile number already exists...");
            throw new RuntimeException("Partner with this mobile number already exists");
        }

        BigDecimal currentSum = partnerRepo.sumActiveSharePercentage();

        if (currentSum == null) currentSum = BigDecimal.ZERO;

        if (currentSum.add(dto.getSharePercentage()).compareTo(new BigDecimal("100.00")) > 0) {
            log.info("SERVICE - total partner share would exceed 100%...");
            throw new RuntimeException("Total partner share cannot exceed 100%");
        }

        Partner partner = modelMapper.map(dto, Partner.class);

        partner = partnerRepo.save(partner);

        return mapToResponse(partner);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponsePartnerDTO getPartnerByPublicId(UUID publicId) {
        log.info("SERVICE - request came in getPartnerByPublicId...");

        Partner partner = partnerRepo.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("Partner not found"));

        return mapToResponse(partner);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponsePartnerDTO> getAllPartners() {
        log.info("SERVICE - request came in getAllPartners...");

        List<Partner> partners = partnerRepo.findAll();

        return partners.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponsePartnerDTO> getAllActivePartners() {
        log.info("SERVICE - request came in getAllActivePartners...");

        List<Partner> partners = partnerRepo.findByIsActiveTrue();

        return partners.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponsePartnerDTO updatePartner(UUID publicId, RequestPartnerDTO dto) {
        log.info("SERVICE - request came in updatePartner...");

        Partner partner = partnerRepo.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("Partner not found"));

        if (!partner.getMobileNumber().equals(dto.getMobileNumber())
                && partnerRepo.existsByMobileNumberAndPublicIdNot(dto.getMobileNumber(), publicId)) {
            log.info("SERVICE - partner mobile number already exists...");
            throw new RuntimeException("Partner with this mobile number already exists");
        }

        BigDecimal otherSum = partnerRepo.sumActiveSharePercentageExcluding(publicId);
        if (otherSum == null) otherSum = BigDecimal.ZERO;

        if (otherSum.add(dto.getSharePercentage()).compareTo(new BigDecimal("100.00")) > 0) {
            log.info("SERVICE - total partner share would exceed 100% on update...");
            throw new RuntimeException("Total partner share cannot exceed 100%");
        }

        partner.setPartnerName(dto.getPartnerName());
        partner.setMobileNumber(dto.getMobileNumber());
        partner.setEmail(dto.getEmail());
        partner.setSharePercentage(dto.getSharePercentage());
        partner.setJoiningDate(dto.getJoiningDate());

        partner = partnerRepo.save(partner);

        return mapToResponse(partner);
    }

    @Override
    @Transactional
    public void deactivatePartner(UUID publicId) {
        log.info("SERVICE - request came in deactivatePartner...");

        Partner partner = partnerRepo.findByPublicIdAndIsActiveTrue(publicId)
                .orElseThrow(() -> new RuntimeException("Partner not found or already inactive"));

        partner.setIsActive(false);
        partnerRepo.save(partner);
    }

    @Override
    @Transactional
    public void reactivatePartner(UUID publicId) {
        log.info("SERVICE - request came in reactivatePartner...");

        Partner partner = partnerRepo.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("Partner not found"));

        partner.setIsActive(true);
        partnerRepo.save(partner);
    }

    private ResponsePartnerDTO mapToResponse(Partner partner) {

        BigDecimal lifetime = partnerProfitShareRepo.sumLifetimeEarningsByPartner(partner.getPublicId());

        if (lifetime == null) lifetime = BigDecimal.ZERO;

        ResponsePartnerDTO resp = modelMapper.map(partner, ResponsePartnerDTO.class);
        resp.setLifetimeEarnings(lifetime);

        return resp;
    }
}
