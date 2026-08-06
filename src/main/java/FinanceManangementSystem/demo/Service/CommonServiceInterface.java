package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestLoginDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseLoginDTO;

public interface CommonServiceInterface {
    ResponseLoginDTO login(RequestLoginDTO dto);
}
