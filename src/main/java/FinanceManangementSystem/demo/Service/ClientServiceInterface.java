package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestPurchaseDTO;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestSupplierDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponsePurchaseDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseSupplierDTO;

public interface ClientServiceInterface {
    ResponseSupplierDTO addSupplier(RequestSupplierDTO dto);

    ResponsePurchaseDTO addPurchase(RequestPurchaseDTO dto);
}
