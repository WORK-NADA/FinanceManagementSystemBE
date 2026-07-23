package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Model.Purchase;
import FinanceManangementSystem.demo.Model.Supplier;
import FinanceManangementSystem.demo.Repository.PurchaseRepository;
import FinanceManangementSystem.demo.Repository.SupplierRepository;
import FinanceManangementSystem.demo.RequestDTO.RequestPurchaseDTO;
import FinanceManangementSystem.demo.RequestDTO.RequestSupplierDTO;
import FinanceManangementSystem.demo.ResponseDTO.ResponsePurchaseDTO;
import FinanceManangementSystem.demo.ResponseDTO.ResponseSufficientSupplierDTO;
import FinanceManangementSystem.demo.ResponseDTO.ResponseSupplierDTO;
import FinanceManangementSystem.demo.Service.ClientServiceInterface;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class ClientService implements ClientServiceInterface{

    private final ModelMapper modelMapper;

    private final SupplierRepository suppRepo;

    private final PurchaseRepository purRepo;

    public ClientService(PurchaseRepository purRepo,SupplierRepository suppRepo,ModelMapper modelMapper){
        this.purRepo = purRepo;
        this.suppRepo = suppRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public ResponseSupplierDTO addSupplier(RequestSupplierDTO dto) {
        Optional<String> name = suppRepo.findByEmailorContactorPANno(dto.getContact(),dto.getEmail(),dto.getPanNo());
        System.out.println(!name.isEmpty());
        if(!name.isEmpty()){
            throw new RuntimeException("Supplier already exists...");
        }

        Supplier supplier = modelMapper.map(dto,Supplier.class);
        System.out.println(supplier.getAddress());
        if(supplier.getAddress() != null) {
            supplier.getAddress().setSupplier(supplier);
        }
        supplier = suppRepo.save(supplier);

        ResponseSupplierDTO response = modelMapper.map(supplier,ResponseSupplierDTO.class);
        return response;
    }

    @Override
    public ResponsePurchaseDTO addPurchase(RequestPurchaseDTO dto) {
        System.out.println("Initiated");
        Supplier supplier = suppRepo.findById(dto.getSupplierId()).orElseThrow(() -> new RuntimeException("Supplier does not exists..."));

        Purchase purchase = modelMapper.map(dto,Purchase.class);
        purchase.setSupplier(supplier);

        double amount = dto.getPrice() * dto.getWeight();
        double withGstAmount = amount + (amount * dto.getGst() / 100);

        purchase.setAmount(amount);
        purchase.setWithGstAmount(withGstAmount);

        System.out.println("REMAINING");
        purchase = purRepo.save(purchase);
        System.out.println("PURCHASED");

        ResponsePurchaseDTO response = modelMapper.map(purchase,ResponsePurchaseDTO.class);
        ResponseSufficientSupplierDTO supp = new ResponseSufficientSupplierDTO(purchase.getSupplier().getSupplierId(),purchase.getSupplier().getName(),purchase.getSupplier().getContact());
        response.setSupplier(supp);
        System.out.println("DONE");
        return response;
    }


}
