package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.RequestDTO.RequestSupplierDTO;
import FinanceManangementSystem.demo.ResponseDTO.ResponseSupplierDTO;

public interface ClientServiceInterface {
    ResponseSupplierDTO addSupplier(RequestSupplierDTO dto);
}
