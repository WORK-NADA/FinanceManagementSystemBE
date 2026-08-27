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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierService implements SupplierServiceInterface {

    private final SupplierRepository supplierRepo;

    private final ModelMapper modelMapper;


    // ==================================================
    // ADD SUPPLIER
    // ==================================================

    @Override
    @Transactional
    public ResponseSupplierDTO addSupplier(
            RequestSupplierDTO dto
    ) {

        log.info(
                "SERVICE - request came in addSupplier..."
        );


        // ----------------------------------------------
        // Check Mobile Number
        // ----------------------------------------------

        log.info(
                "SERVICE - checking supplier mobile number..."
        );

        if (supplierRepo.existsByMobileNumber(
                dto.getMobileNumber()
        )) {

            log.info(
                    "SERVICE - supplier mobile number already exists..."
            );

            throw new RuntimeException(
                    "Supplier with this mobile number already exists"
            );
        }


        // ----------------------------------------------
        // Check GST Number
        // ----------------------------------------------

        if (dto.getGstNumber() != null
                && !dto.getGstNumber().isBlank()
                && supplierRepo.existsByGstNumber(
                dto.getGstNumber()
        )) {

            log.info(
                    "SERVICE - supplier GST number already exists..."
            );

            throw new RuntimeException(
                    "Supplier with this GST number already exists"
            );
        }


        // ----------------------------------------------
        // Map DTO → Entity
        // ----------------------------------------------

        log.info(
                "SERVICE - mapping supplier DTO to entity..."
        );

        Supplier supplier =
                modelMapper.map(
                        dto,
                        Supplier.class
                );


        // ----------------------------------------------
        // Explicitly Handle Supplier Address
        // ----------------------------------------------

        if (dto.getAddress() != null) {

            log.info(
                    "SERVICE - supplier address found..."
            );

            SupplierAddress address =
                    modelMapper.map(
                            dto.getAddress(),
                            SupplierAddress.class
                    );

            // Explicit relationship
            address.setSupplier(supplier);

            supplier.setAddress(address);
        }


        // ----------------------------------------------
        // Set Default Values
        // ----------------------------------------------

        if (supplier.getOpeningBalance() == null) {

            supplier.setOpeningBalance(
                    BigDecimal.ZERO
            );
        }

        if (supplier.getPaymentTerms() == null) {

            supplier.setPaymentTerms(30);
        }

        if (supplier.getIsActive() == null) {

            supplier.setIsActive(true);
        }


        // ----------------------------------------------
        // Save Supplier
        // ----------------------------------------------

        log.info(
                "SERVICE - saving supplier..."
        );

        supplier =
                supplierRepo.save(supplier);


        log.info(
                "SERVICE - supplier added successfully..."
        );


        // ----------------------------------------------
        // Map Entity → Response
        // ----------------------------------------------

        return mapToResponse(supplier);
    }


    // ==================================================
    // GET SUPPLIER BY PUBLIC ID
    // ==================================================

    @Override
    @Transactional(readOnly = true)
    public ResponseSupplierDTO getSupplierByPublicId(
            UUID publicId
    ) {

        log.info(
                "SERVICE - request came in getSupplierByPublicId..."
        );


        Supplier supplier =
                supplierRepo.findByPublicId(publicId)
                        .orElseThrow(() -> {

                            log.info(
                                    "SERVICE - supplier not found..."
                            );

                            return new RuntimeException(
                                    "Supplier not found"
                            );
                        });


        log.info(
                "SERVICE - supplier fetched successfully..."
        );


        return mapToResponse(supplier);
    }


    // ==================================================
    // GET ALL SUPPLIERS
    // ==================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseSupplierDTO> getAllSuppliers() {

        log.info(
                "SERVICE - request came in getAllSuppliers..."
        );


        List<Supplier> suppliers =
                supplierRepo.findAll();


        log.info(
                "SERVICE - suppliers fetched successfully..."
        );


        return suppliers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    // ==================================================
    // UPDATE SUPPLIER
    // ==================================================

    @Override
    @Transactional
    public ResponseSupplierDTO updateSupplier(
            UUID publicId,
            RequestSupplierDTO dto
    ) {

        log.info(
                "SERVICE - request came in updateSupplier..."
        );


        // ----------------------------------------------
        // Find Supplier
        // ----------------------------------------------

        Supplier supplier =
                supplierRepo.findByPublicId(publicId)
                        .orElseThrow(() -> {

                            log.info(
                                    "SERVICE - supplier not found..."
                            );

                            return new RuntimeException(
                                    "Supplier not found"
                            );
                        });


        // ----------------------------------------------
        // Check Mobile Number
        // ----------------------------------------------

        if (!supplier.getMobileNumber()
                .equals(dto.getMobileNumber())
                && supplierRepo.existsByMobileNumber(
                dto.getMobileNumber()
        )) {

            log.info(
                    "SERVICE - supplier mobile number already exists..."
            );

            throw new RuntimeException(
                    "Supplier with this mobile number already exists"
            );
        }


        // ----------------------------------------------
        // Check Email
        // ----------------------------------------------

        if (dto.getEmail() != null
                && !dto.getEmail().isBlank()
                && !dto.getEmail().equals(
                supplier.getEmail()
        )
                && supplierRepo.existsByEmail(
                dto.getEmail()
        )) {

            log.info(
                    "SERVICE - supplier email already exists..."
            );

            throw new RuntimeException(
                    "Supplier with this email already exists"
            );
        }


        // ----------------------------------------------
        // Check GST Number
        // ----------------------------------------------

        if (dto.getGstNumber() != null
                && !dto.getGstNumber().isBlank()
                && !dto.getGstNumber().equals(
                supplier.getGstNumber()
        )
                && supplierRepo.existsByGstNumber(
                dto.getGstNumber()
        )) {

            log.info(
                    "SERVICE - supplier GST number already exists..."
            );

            throw new RuntimeException(
                    "Supplier with this GST number already exists"
            );
        }


        // ----------------------------------------------
        // Update Supplier Fields
        // ----------------------------------------------

        supplier.setSupplierName(
                dto.getSupplierName()
        );

        supplier.setMobileNumber(
                dto.getMobileNumber()
        );

        supplier.setContactPerson(
                dto.getContactPerson()
        );

        supplier.setAlternateMobileNumber(
                dto.getAlternateMobileNumber()
        );

        supplier.setEmail(
                dto.getEmail()
        );

        supplier.setGstNumber(
                dto.getGstNumber()
        );

        supplier.setOpeningBalance(
                dto.getOpeningBalance()
        );

        supplier.setPaymentTerms(
                dto.getPaymentTerms()
        );


        // ----------------------------------------------
        // Explicitly Handle Address
        // ----------------------------------------------

        if (dto.getAddress() != null) {

            log.info(
                    "SERVICE - supplier address found..."
            );


            SupplierAddress address =
                    supplier.getAddress();


            if (address == null) {

                address =
                        modelMapper.map(
                                dto.getAddress(),
                                SupplierAddress.class
                        );

                address.setSupplier(supplier);

                supplier.setAddress(address);

            } else {

                modelMapper.map(
                        dto.getAddress(),
                        address
                );

                address.setSupplier(supplier);
            }
        }


        // ----------------------------------------------
        // Save
        // ----------------------------------------------

        supplier =
                supplierRepo.save(supplier);


        log.info(
                "SERVICE - supplier updated successfully..."
        );


        return mapToResponse(supplier);
    }


    // ==================================================
    // DEACTIVATE SUPPLIER
    // ==================================================

    @Override
    @Transactional
    public void deactivateSupplier(
            UUID publicId
    ) {

        log.info(
                "SERVICE - request came in deactivateSupplier..."
        );


        Supplier supplier =
                supplierRepo.findByPublicId(publicId)
                        .orElseThrow(() -> {

                            log.info(
                                    "SERVICE - supplier not found..."
                            );

                            return new RuntimeException(
                                    "Supplier not found"
                            );
                        });


        if (!supplier.getIsActive()) {

            log.info(
                    "SERVICE - supplier is already inactive..."
            );

            throw new RuntimeException(
                    "Supplier is already inactive"
            );
        }


        supplier.setIsActive(false);

        supplierRepo.save(supplier);


        log.info(
                "SERVICE - supplier deactivated successfully..."
        );
    }


    // ==================================================
    // ACTIVATE SUPPLIER
    // ==================================================

    @Override
    @Transactional
    public void activateSupplier(
            UUID publicId
    ) {

        log.info(
                "SERVICE - request came in activateSupplier..."
        );


        Supplier supplier =
                supplierRepo.findByPublicId(publicId)
                        .orElseThrow(() -> {

                            log.info(
                                    "SERVICE - supplier not found..."
                            );

                            return new RuntimeException(
                                    "Supplier not found"
                            );
                        });


        if (supplier.getIsActive()) {

            log.info(
                    "SERVICE - supplier is already active..."
            );

            throw new RuntimeException(
                    "Supplier is already active"
            );
        }


        supplier.setIsActive(true);

        supplierRepo.save(supplier);


        log.info(
                "SERVICE - supplier activated successfully..."
        );
    }


    // ==================================================
    // ENTITY → RESPONSE DTO
    // ==================================================

    private ResponseSupplierDTO mapToResponse(
            Supplier supplier
    ) {

        log.info(
                "SERVICE - mapping supplier to response DTO..."
        );


        ResponseSupplierDTO response =
                modelMapper.map(
                        supplier,
                        ResponseSupplierDTO.class
                );


        // ----------------------------------------------
        // Explicitly Handle Address
        // ----------------------------------------------

        if (supplier.getAddress() != null) {

            response.setAddress(
                    modelMapper.map(
                            supplier.getAddress(),
                            FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseSupplierAddressDTO.class
                    )
            );
        }


        return response;
    }
}