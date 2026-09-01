package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestSupplierDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseSupplierDTO;

import java.util.List;
import java.util.UUID;

public interface SupplierServiceInterface {

    ResponseSupplierDTO addSupplier(
            RequestSupplierDTO dto
    );

    ResponseSupplierDTO getSupplierByPublicId(
            UUID publicId
    );

    List<ResponseSupplierDTO> getAllSuppliers();

    ResponseSupplierDTO updateSupplier(
            UUID publicId,
            RequestSupplierDTO dto
    );

    void deactivateSupplier(
            UUID publicId
    );

    void reactivateSupplier(
            UUID publicId
    );
}