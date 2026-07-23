package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.RequestDTO.RequestPurchaseDTO;
import FinanceManangementSystem.demo.RequestDTO.RequestSupplierDTO;
import FinanceManangementSystem.demo.ResponseDTO.ResponsePurchaseDTO;
import FinanceManangementSystem.demo.ResponseDTO.ResponseSupplierDTO;

public interface ClientServiceInterface {
    ResponseSupplierDTO addSupplier(RequestSupplierDTO dto);

    ResponsePurchaseDTO addPurchase(RequestPurchaseDTO dto);
}
