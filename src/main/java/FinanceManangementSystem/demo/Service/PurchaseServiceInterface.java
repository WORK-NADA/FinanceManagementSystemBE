package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestPurchaseDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponsePurchaseDTO;

import java.util.UUID;

public interface PurchaseServiceInterface {

    ResponsePurchaseDTO addPurchase(RequestPurchaseDTO dto);

    ResponsePurchaseDTO getPurchaseByPublicId(UUID publicId);
}