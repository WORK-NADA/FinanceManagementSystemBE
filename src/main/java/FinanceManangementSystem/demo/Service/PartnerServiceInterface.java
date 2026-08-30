package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestPartnerDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponsePartnerDTO;

import java.util.List;
import java.util.UUID;

public interface PartnerServiceInterface {

    ResponsePartnerDTO addPartner(
            RequestPartnerDTO dto
    );

    ResponsePartnerDTO getPartnerByPublicId(
            UUID publicId
    );

    List<ResponsePartnerDTO> getAllPartners();

    List<ResponsePartnerDTO> getAllActivePartners();

    ResponsePartnerDTO updatePartner(
            UUID publicId,
            RequestPartnerDTO dto
    );

    void deactivatePartner(
            UUID publicId
    );

    void reactivatePartner(
            UUID publicId
    );
}
