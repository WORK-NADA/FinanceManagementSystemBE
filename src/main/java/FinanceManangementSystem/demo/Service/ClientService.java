package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.Model.Supplier;
import FinanceManangementSystem.demo.Repository.SupplierRepository;
import FinanceManangementSystem.demo.RequestDTO.RequestSupplierDTO;
import FinanceManangementSystem.demo.ResponseDTO.ResponseSupplierDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientService implements ClientServiceInterface{

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    SupplierRepository suppRepo;

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
}
