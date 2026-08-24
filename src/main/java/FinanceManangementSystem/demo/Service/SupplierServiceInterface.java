package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestSupplierDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseSupplierDTO;

import java.util.UUID;

public interface SupplierServiceInterface {

    ResponseSupplierDTO addSupplier(RequestSupplierDTO dto);

    ResponseSupplierDTO getSupplierByPublicId(UUID publicId);
}