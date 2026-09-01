package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Exceptions.ResourceNotFoundException;


import FinanceManangementSystem.demo.Enums.UserRole;
import FinanceManangementSystem.demo.Exceptions.DuplicateResourceException;
import FinanceManangementSystem.demo.Exceptions.InvalidStateException;
import FinanceManangementSystem.demo.Model.Customer;
import FinanceManangementSystem.demo.Model.CustomerAddress;
import FinanceManangementSystem.demo.Model.User;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestCustomerDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseCustomerDTO;
import FinanceManangementSystem.demo.Repository.CustomerRepository;
import FinanceManangementSystem.demo.Service.CustomerServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService
        implements CustomerServiceInterface {

    private final CustomerRepository customerRepo;

    private final CurrentUserService currentUserService;

    private final ModelMapper modelMapper;


    // =========================================================
    // ADD CUSTOMER
    // =========================================================

    @Override
    @Transactional
    public ResponseCustomerDTO addCustomer(
            RequestCustomerDTO dto
    ) {

        log.info(
                "SERVICE - request came in addCustomer..."
        );


        // -----------------------------------------------------
        // NORMALIZE VALUES
        // -----------------------------------------------------

        String mobileNumber =
                dto.getMobileNumber().trim();

        String email =
                normalizeEmail(
                        dto.getEmail()
                );

        String gstNumber =
                normalizeGst(
                        dto.getGstNumber()
                );


        // -----------------------------------------------------
        // RESOLVE CURRENT USER (needed for per-user checks)
        // -----------------------------------------------------

        User currentUser = currentUserService.getCurrentUser();


        // -----------------------------------------------------
        // CHECK DUPLICATE MOBILE
        // -----------------------------------------------------

        if (customerRepo.existsByUserAndMobileNumber(
                currentUser,
                mobileNumber
        )) {

            log.warn(
                    "SERVICE - mobile number already exists..."
            );

            throw new DuplicateResourceException(
                    "Customer with this mobile number already exists"
            );
        }


        // -----------------------------------------------------
        // CHECK DUPLICATE EMAIL
        // -----------------------------------------------------

        if (email != null
                && customerRepo.existsByUserAndEmail(currentUser, email)) {

            log.warn(
                    "SERVICE - email already exists..."
            );

            throw new DuplicateResourceException(
                    "Customer with this email already exists"
            );
        }


        // -----------------------------------------------------
        // CHECK DUPLICATE GST
        // -----------------------------------------------------

        if (gstNumber != null
                && customerRepo.existsByUserAndGstNumber(currentUser, gstNumber)) {

            log.warn(
                    "SERVICE - GST number already exists..."
            );

            throw new DuplicateResourceException(
                    "Customer with this GST number already exists"
            );
        }


        // -----------------------------------------------------
        // CREATE CUSTOMER
        // -----------------------------------------------------

        Customer customer =
                new Customer();

        customer.setUser(currentUser);

        customer.setCustomerName(
                dto.getCustomerName().trim()
        );


        customer.setMobileNumber(
                mobileNumber
        );


        customer.setContactPerson(
                normalizeString(
                        dto.getContactPerson()
                )
        );


        customer.setAlternateMobileNumber(
                normalizeString(
                        dto.getAlternateMobileNumber()
                )
        );


        customer.setEmail(
                email
        );


        customer.setGstNumber(
                gstNumber
        );


        // -----------------------------------------------------
        // OPENING BALANCE
        // -----------------------------------------------------

        customer.setOpeningBalance(
                dto.getOpeningBalance()
        );


        // -----------------------------------------------------
        // PAYMENT TERMS
        // -----------------------------------------------------

        customer.setPaymentTerms(
                dto.getPaymentTerms()
        );


        // -----------------------------------------------------
        // SERVICE CONTROLLED STATUS
        // -----------------------------------------------------

        customer.setIsActive(
                true
        );


        // -----------------------------------------------------
        // CUSTOMER ADDRESS
        // -----------------------------------------------------

        setCustomerAddress(
                customer,
                dto.getAddress()
        );


        // -----------------------------------------------------
        // SAVE CUSTOMER
        // -----------------------------------------------------

        log.info(
                "SERVICE - saving customer..."
        );


        customer =
                customerRepo.save(
                        customer
                );


        log.info(
                "SERVICE - customer added successfully..."
        );


        return mapToResponse(
                customer
        );
    }


    // =========================================================
    // GET CUSTOMER BY PUBLIC ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public ResponseCustomerDTO getCustomerByPublicId(
            UUID publicId
    ) {

        log.info(
                "SERVICE - request came in getCustomerByPublicId..."
        );

        User currentUser = currentUserService.getCurrentUser();

        Customer customer;

        if (currentUser.getRole() == UserRole.ADMIN) {
            customer = customerRepo.findByPublicId(publicId)
                    .orElseThrow(() -> {
                        log.info("SERVICE - customer not found...");
                        return new ResourceNotFoundException("Customer not found");
                    });
        } else {
            customer = customerRepo.findByUserAndPublicId(currentUser, publicId)
                    .orElseThrow(() -> {
                        log.info("SERVICE - customer not found for current user...");
                        return new ResourceNotFoundException("Customer not found");
                    });
        }

        return mapToResponse(customer);
    }


    // =========================================================
    // GET ALL CUSTOMERS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseCustomerDTO> getAllCustomers() {

        log.info(
                "SERVICE - request came in getAllCustomers..."
        );


        User currentUser = currentUserService.getCurrentUser();

        if (currentUser.getRole() == UserRole.ADMIN) {
            return customerRepo.findAll().stream()
                    .map(this::mapToResponse)
                    .toList();
        }

        return customerRepo
                .findByUser(currentUser)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // GET ALL ACTIVE CUSTOMERS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseCustomerDTO> getAllActiveCustomers() {

        log.info(
                "SERVICE - request came in getAllActiveCustomers..."
        );


        User currentUser = currentUserService.getCurrentUser();

        if (currentUser.getRole() == UserRole.ADMIN) {
            return customerRepo.findByIsActiveTrue().stream()
                    .map(this::mapToResponse)
                    .toList();
        }

        return customerRepo
                .findByUserAndIsActiveTrue(currentUser)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // UPDATE CUSTOMER
    // =========================================================

    @Override
    @Transactional
    public ResponseCustomerDTO updateCustomer(
            UUID publicId,
            RequestCustomerDTO dto
    ) {

        log.info(
                "SERVICE - request came in updateCustomer..."
        );


        // -----------------------------------------------------
        // FIND CUSTOMER
        // -----------------------------------------------------

        User currentUser = currentUserService.getCurrentUser();

        Customer customer;

        if (currentUser.getRole() == UserRole.ADMIN) {
            customer = customerRepo.findByPublicId(publicId)
                    .orElseThrow(() -> {
                        log.info("SERVICE - customer not found...");
                        return new ResourceNotFoundException("Customer not found");
                    });
        } else {
            customer = customerRepo.findByUserAndPublicId(currentUser, publicId)
                    .orElseThrow(() -> {
                        log.info("SERVICE - customer not found for current user...");
                        return new ResourceNotFoundException("Customer not found");
                    });
        }


        // -----------------------------------------------------
        // CHECK CUSTOMER STATUS
        // -----------------------------------------------------

        if (!Boolean.TRUE.equals(
                customer.getIsActive()
        )) {

            log.info(
                    "SERVICE - inactive customer cannot be updated..."
            );

            throw new InvalidStateException(
                    "Inactive customer cannot be updated"
            );
        }


        // -----------------------------------------------------
        // NORMALIZE VALUES
        // -----------------------------------------------------

        String mobileNumber =
                dto.getMobileNumber().trim();

        String email =
                normalizeEmail(
                        dto.getEmail()
                );

        String gstNumber =
                normalizeGst(
                        dto.getGstNumber()
                );


        // -----------------------------------------------------
        // CHECK MOBILE DUPLICATE
        // -----------------------------------------------------

        if (customerRepo
                .existsByUserAndMobileNumberAndPublicIdNot(
                        currentUser,
                        mobileNumber,
                        publicId
                )) {

            log.info(
                    "SERVICE - mobile number already exists for another customer..."
            );

            throw new DuplicateResourceException(
                    "Customer with this mobile number already exists"
            );
        }


        // -----------------------------------------------------
        // CHECK EMAIL DUPLICATE
        // -----------------------------------------------------

        if (email != null
                && customerRepo
                .existsByUserAndEmailAndPublicIdNot(
                        currentUser,
                        email,
                        publicId
                )) {

            log.info(
                    "SERVICE - email already exists for another customer..."
            );

            throw new DuplicateResourceException(
                    "Customer with this email already exists"
            );
        }


        // -----------------------------------------------------
        // CHECK GST DUPLICATE
        // -----------------------------------------------------

        if (gstNumber != null
                && customerRepo
                .existsByUserAndGstNumberAndPublicIdNot(
                        currentUser,
                        gstNumber,
                        publicId
                )) {

            log.info(
                    "SERVICE - GST number already exists for another customer..."
            );

            throw new DuplicateResourceException(
                    "Customer with this GST number already exists"
            );
        }


        // -----------------------------------------------------
        // UPDATE CUSTOMER DETAILS
        // -----------------------------------------------------

        customer.setCustomerName(
                dto.getCustomerName().trim()
        );


        customer.setMobileNumber(
                mobileNumber
        );


        customer.setContactPerson(
                normalizeString(
                        dto.getContactPerson()
                )
        );


        customer.setAlternateMobileNumber(
                normalizeString(
                        dto.getAlternateMobileNumber()
                )
        );


        customer.setEmail(
                email
        );


        customer.setGstNumber(
                gstNumber
        );


        // -----------------------------------------------------
        // UPDATE OPENING BALANCE
        // -----------------------------------------------------

        customer.setOpeningBalance(
                dto.getOpeningBalance()
        );


        // -----------------------------------------------------
        // UPDATE PAYMENT TERMS
        // -----------------------------------------------------

        customer.setPaymentTerms(
                dto.getPaymentTerms()
        );


        // -----------------------------------------------------
        // UPDATE ADDRESS
        // -----------------------------------------------------

        updateCustomerAddress(
                customer,
                dto.getAddress()
        );


        // -----------------------------------------------------
        // SAVE CUSTOMER
        // -----------------------------------------------------

        customer =
                customerRepo.save(
                        customer
                );


        log.info(
                "SERVICE - customer updated successfully..."
        );


        return mapToResponse(
                customer
        );
    }


    // =========================================================
    // DEACTIVATE CUSTOMER
    // =========================================================

    @Override
    @Transactional
    public void deactivateCustomer(
            UUID publicId
    ) {

        log.info(
                "SERVICE - request came in deactivateCustomer..."
        );


        // -----------------------------------------------------
        // FIND CUSTOMER
        // -----------------------------------------------------

        User currentUser = currentUserService.getCurrentUser();

        Customer customer;

        if (currentUser.getRole() == UserRole.ADMIN) {
            customer = customerRepo.findByPublicId(publicId)
                    .orElseThrow(() -> {
                        log.info("SERVICE - customer not found...");
                        return new ResourceNotFoundException("Customer not found");
                    });
        } else {
            customer = customerRepo.findByUserAndPublicId(currentUser, publicId)
                    .orElseThrow(() -> {
                        log.info("SERVICE - customer not found for current user...");
                        return new ResourceNotFoundException("Customer not found");
                    });
        }


        // -----------------------------------------------------
        // CHECK STATUS
        // -----------------------------------------------------

        if (!Boolean.TRUE.equals(
                customer.getIsActive()
        )) {

            log.info(
                    "SERVICE - customer is already inactive..."
            );

            throw new InvalidStateException(
                    "Customer is already inactive"
            );
        }


        // -----------------------------------------------------
        // DEACTIVATE
        // -----------------------------------------------------

        customer.setIsActive(
                false
        );


        customerRepo.save(
                customer
        );


        log.info(
                "SERVICE - customer deactivated successfully..."
        );
    }


    // =========================================================
    // REACTIVATE CUSTOMER
    // =========================================================

    @Override
    @Transactional
    public void reactivateCustomer(
            UUID publicId
    ) {

        log.info(
                "SERVICE - request came in reactivateCustomer..."
        );


        // -----------------------------------------------------
        // FIND CUSTOMER
        // -----------------------------------------------------

        User currentUser = currentUserService.getCurrentUser();

        Customer customer;

        if (currentUser.getRole() == UserRole.ADMIN) {
            customer = customerRepo.findByPublicId(publicId)
                    .orElseThrow(() -> {
                        log.info("SERVICE - customer not found...");
                        return new ResourceNotFoundException("Customer not found");
                    });
        } else {
            customer = customerRepo.findByUserAndPublicId(currentUser, publicId)
                    .orElseThrow(() -> {
                        log.info("SERVICE - customer not found for current user...");
                        return new ResourceNotFoundException("Customer not found");
                    });
        }


        // -----------------------------------------------------
        // CHECK STATUS
        // -----------------------------------------------------

        if (Boolean.TRUE.equals(
                customer.getIsActive()
        )) {

            log.info(
                    "SERVICE - customer is already active..."
            );

            throw new InvalidStateException(
                    "Customer is already active"
            );
        }


        // -----------------------------------------------------
        // REACTIVATE
        // -----------------------------------------------------

        customer.setIsActive(
                true
        );


        customerRepo.save(
                customer
        );


        log.info(
                "SERVICE - customer reactivated successfully..."
        );
    }


    // =========================================================
    // SET CUSTOMER ADDRESS
    // =========================================================

    private void setCustomerAddress(
            Customer customer,
            RequestCustomerDTO.CustomerAddressDTO dto
    ) {

        if (dto == null) {
            return;
        }


        CustomerAddress address =
                new CustomerAddress();


        address.setCustomer(
                customer
        );


        address.setAddressLine1(
                dto.getAddressLine1().trim()
        );


        address.setAddressLine2(
                normalizeString(
                        dto.getAddressLine2()
                )
        );


        address.setCity(
                dto.getCity().trim()
        );


        address.setState(
                dto.getState().trim()
        );


        address.setCountry(
                dto.getCountry() != null
                        ? dto.getCountry().trim()
                        : "India"
        );


        address.setPincode(
                dto.getPincode().trim()
        );


        customer.setAddress(
                address
        );
    }


    // =========================================================
    // UPDATE CUSTOMER ADDRESS
    // =========================================================

    private void updateCustomerAddress(
            Customer customer,
            RequestCustomerDTO.CustomerAddressDTO dto
    ) {

        if (dto == null) {
            return;
        }


        CustomerAddress address =
                customer.getAddress();


        // -----------------------------------------------------
        // CREATE ADDRESS IF IT DOES NOT EXIST
        // -----------------------------------------------------

        if (address == null) {

            address =
                    new CustomerAddress();


            address.setCustomer(
                    customer
            );


            customer.setAddress(
                    address
            );
        }


        // -----------------------------------------------------
        // UPDATE ADDRESS
        // -----------------------------------------------------

        address.setAddressLine1(
                dto.getAddressLine1().trim()
        );


        address.setAddressLine2(
                normalizeString(
                        dto.getAddressLine2()
                )
        );


        address.setCity(
                dto.getCity().trim()
        );


        address.setState(
                dto.getState().trim()
        );


        address.setCountry(
                dto.getCountry() != null
                        ? dto.getCountry().trim()
                        : "India"
        );


        address.setPincode(
                dto.getPincode().trim()
        );
    }


    // =========================================================
    // ENTITY → RESPONSE DTO
    // =========================================================

    private ResponseCustomerDTO mapToResponse(
            Customer customer
    ) {

        log.info(
                "SERVICE - mapping customer to response DTO..."
        );


        // -----------------------------------------------------
        // MAP CUSTOMER
        // -----------------------------------------------------

        ResponseCustomerDTO response =
                modelMapper.map(
                        customer,
                        ResponseCustomerDTO.class
                );


        // -----------------------------------------------------
        // MAP ADDRESS
        // -----------------------------------------------------

        CustomerAddress address =
                customer.getAddress();


        if (address != null) {

            ResponseCustomerDTO.CustomerAddressDetails
                    addressDetails =
                    new ResponseCustomerDTO.CustomerAddressDetails();


            addressDetails.setAddressLine1(
                    address.getAddressLine1()
            );


            addressDetails.setAddressLine2(
                    address.getAddressLine2()
            );


            addressDetails.setCity(
                    address.getCity()
            );


            addressDetails.setState(
                    address.getState()
            );


            addressDetails.setCountry(
                    address.getCountry()
            );


            addressDetails.setPincode(
                    address.getPincode()
            );


            response.setAddress(
                    addressDetails
            );
        }


        return response;
    }


    // =========================================================
    // NORMALIZE STRING
    // =========================================================

    private String normalizeString(
            String value
    ) {

        if (value == null) {
            return null;
        }


        String trimmed =
                value.trim();


        return trimmed.isBlank()
                ? null
                : trimmed;
    }


    // =========================================================
    // NORMALIZE EMAIL
    // =========================================================

    private String normalizeEmail(
            String email
    ) {

        String normalized =
                normalizeString(
                        email
                );


        if (normalized == null) {
            return null;
        }


        return normalized.toLowerCase();
    }


    // =========================================================
    // NORMALIZE GST
    // =========================================================

    private String normalizeGst(
            String gstNumber
    ) {

        String normalized =
                normalizeString(
                        gstNumber
                );


        if (normalized == null) {
            return null;
        }


        return normalized.toUpperCase();
    }
}