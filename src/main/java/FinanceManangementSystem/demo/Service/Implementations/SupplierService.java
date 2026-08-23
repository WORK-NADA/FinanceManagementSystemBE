package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Model.Supplier;
import FinanceManangementSystem.demo.Model.SupplierAddress;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestSupplierDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseSupplierDTO;
import FinanceManangementSystem.demo.Repository.SupplierRepository;
import FinanceManangementSystem.demo.Service.SupplierServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierService implements SupplierServiceInterface {

    private final SupplierRepository supplierRepo;

    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public ResponseSupplierDTO addSupplier(RequestSupplierDTO dto) {

        log.info("SERVICE - request came in addSupplier...");


        // Check mobile number
        log.info("SERVICE - checking supplier mobile number...");

        if (supplierRepo.existsByMobileNumber(dto.getMobileNumber())) {

            log.warn(
                    "SERVICE - supplier mobile number already exists..."
            );

            throw new RuntimeException(
                    "Supplier with this mobile number already exists"
            );
        }


        // Check GST number
        if (dto.getGstNumber() != null
                && !dto.getGstNumber().isBlank()) {

            log.info("SERVICE - checking supplier GST number...");

            if (supplierRepo.existsByGstNumber(dto.getGstNumber())) {

                log.warn(
                        "SERVICE - supplier GST number already exists..."
                );

                throw new RuntimeException(
                        "Supplier with this GST number already exists"
                );
            }
        }


        // DTO → Entity
        log.info(
                "SERVICE - mapping RequestSupplierDTO to Supplier entity..."
        );

        Supplier supplier =
                modelMapper.map(
                        dto,
                        Supplier.class
                );


        // Set supplier relationship
        if (dto.getAddress() != null) {

            SupplierAddress address =
                    modelMapper.map(
                            dto.getAddress(),
                            SupplierAddress.class
                    );

            address.setSupplier(supplier);
            supplier.setAddress(address);
        }


        // Save supplier
        log.info("SERVICE - saving supplier...");

        supplier = supplierRepo.save(supplier);


        log.info(
                "SERVICE - supplier saved successfully..."
        );


        // Entity → Response DTO
        log.info(
                "SERVICE - mapping Supplier entity to ResponseSupplierDTO..."
        );

        ResponseSupplierDTO response =
                modelMapper.map(
                        supplier,
                        ResponseSupplierDTO.class
                );


        log.info(
                "SERVICE - addSupplier completed successfully..."
        );

        return response;
    }


    @Override
    @Transactional(readOnly = true)
    public ResponseSupplierDTO getSupplierByPublicId(UUID publicId) {

        log.info(
                "SERVICE - request came in getSupplierByPublicId..."
        );


        // Find supplier
        log.info(
                "SERVICE - searching supplier by publicId..."
        );

        Supplier supplier =
                supplierRepo
                        .findByPublicId(publicId)
                        .orElseThrow(() -> {

                            log.warn(
                                    "SERVICE - supplier not found..."
                            );

                            return new RuntimeException(
                                    "Supplier not found"
                            );
                        });


        log.info(
                "SERVICE - supplier found successfully..."
        );


        // Entity → Response DTO
        log.info(
                "SERVICE - mapping Supplier entity to ResponseSupplierDTO..."
        );

        ResponseSupplierDTO response =
                modelMapper.map(
                        supplier,
                        ResponseSupplierDTO.class
                );


        log.info(
                "SERVICE - getSupplierByPublicId completed successfully..."
        );

        return response;
    }
}