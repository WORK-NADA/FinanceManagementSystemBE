package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestProfitDistributionDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseProfitDistributionDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProfitDistributionServiceInterface {

    ResponseProfitDistributionDTO calculateAndDistribute(
            RequestProfitDistributionDTO dto
    );

    ResponseProfitDistributionDTO getDistributionByPublicId(
            UUID publicId
    );

    List<ResponseProfitDistributionDTO> getAllDistributions();

    List<ResponseProfitDistributionDTO.PartnerShareDetails> getShareHistoryByPartner(
            UUID partnerPublicId
    );

    ResponseProfitDistributionDTO getLatestDistribution();

    BigDecimal getLifetimeEarningsByPartner(UUID partnerPublicId);
}
