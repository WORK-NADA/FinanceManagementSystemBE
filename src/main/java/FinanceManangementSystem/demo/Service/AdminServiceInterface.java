package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestUserDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseUserDTO;

public interface AdminServiceInterface {
    ResponseUserDTO registration(RequestUserDTO dto);
}
