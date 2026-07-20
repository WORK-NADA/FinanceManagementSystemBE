package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.RequestDTO.RequestLoginDTO;
import FinanceManangementSystem.demo.ResponseDTO.ResponseLoginDTO;

public interface CommonServiceInterface {
    ResponseLoginDTO login(RequestLoginDTO dto);
}
