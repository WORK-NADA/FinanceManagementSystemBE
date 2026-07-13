package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.RequestDTO.RequestUserDTO;
import FinanceManangementSystem.demo.ResponseDTO.ResponseUserDTO;

public interface AdminServiceInterface {
    ResponseUserDTO registration(RequestUserDTO dto);
}
