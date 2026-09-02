package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Exceptions.InvalidRequestException;
import FinanceManangementSystem.demo.Exceptions.ResourceNotFoundException;

import FinanceManangementSystem.demo.Enums.DocumentType;
import FinanceManangementSystem.demo.Enums.PaymentStatus;
import FinanceManangementSystem.demo.Enums.UserRole;
import FinanceManangementSystem.demo.Model.Customer;
import FinanceManangementSystem.demo.Model.Sale;
import FinanceManangementSystem.demo.Model.User;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestSaleDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseSaleDTO;
import FinanceManangementSystem.demo.Repository.CustomerRepository;
import FinanceManangementSystem.demo.Repository.SaleRepository;
import FinanceManangementSystem.demo.Service.SaleServiceInterface;
import FinanceManangementSystem.demo.Service.StockTransactionServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaleService
        implements SaleServiceInterface {

    private final SaleRepository saleRepo;

    private final CustomerRepository customerRepo;

    private final CurrentUserService currentUserService;

    private final DocumentSequenceService documentSequenceService;

    private final ModelMapper modelMapper;

    private final StockTransactionServiceInterface stockTransactionService;


    // =========================================================
    // ADD SALE
    // =========================================================

    @Override
    @Transactional
    public ResponseSaleDTO addSale(
            RequestSaleDTO dto
    ) {

        log.info(
                "SERVICE - request came in addSale..."
        );


        // -----------------------------------------------------
        // FIND ACTIVE CUSTOMER
        // -----------------------------------------------------

        log.info(
                "SERVICE - finding active customer..."
        );

        Customer customer =
                customerRepo
                        .findByPublicIdAndIsActiveTrue(
                                dto.getCustomerPublicId()
                        )
                        .orElseThrow(() -> {
                            log.info("SERVICE - active customer not found...");
                            return new ResourceNotFoundException("Active customer not found");
                        });

        // -----------------------------------------------------
        // CHECK CUSTOMER INVOICE NUMBER

        String customerInvoiceNumber =
                dto.getCustomerInvoiceNumber();

        if (customerInvoiceNumber != null) {

            customerInvoiceNumber =
                    customerInvoiceNumber.trim();

            if (!customerInvoiceNumber.isBlank()
                    && saleRepo
                    .existsByCustomerInvoiceNumberAndCustomer(
                            customerInvoiceNumber,
                            customer
                    )) {

                log.info(
                        "SERVICE - customer invoice number already exists..."
                );

                throw new InvalidRequestException(
                        "Sale with this customer invoice number already exists"
                );
            }
        }


        // -----------------------------------------------------
        // CREATE SALE
        // -----------------------------------------------------

        User currentUser = currentUserService.getCurrentUser();

        Sale sale = new Sale();

        sale.setUser(currentUser);

        sale.setCustomer(
                customer
        );

        sale.setRawMaterial(
                dto.getRawMaterial().trim()
        );

        sale.setWeight(
                dto.getWeight()
        );

        sale.setUnit(
                dto.getUnit()
        );

        sale.setRatePerUnit(
                dto.getRatePerUnit()
        );

        sale.setGstPercentage(
                dto.getGstPercentage()
        );

        sale.setCustomerInvoiceNumber(
                customerInvoiceNumber
        );

        sale.setSaleDate(
                dto.getSaleDate()
        );


        // -----------------------------------------------------
        // INITIAL STATUS
        // -----------------------------------------------------

        sale.setPaymentStatus(
                PaymentStatus.PENDING
        );


        // -----------------------------------------------------
        // CALCULATE AMOUNT
        // -----------------------------------------------------

        BigDecimal amount =
                dto.getWeight()
                        .multiply(
                                dto.getRatePerUnit()
                        )
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        sale.setAmount(
                amount
        );


        // -----------------------------------------------------
        // CALCULATE GST
        // -----------------------------------------------------

        BigDecimal gstAmount =
                amount
                        .multiply(
                                dto.getGstPercentage()
                        )
                        .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP
                        );

        sale.setGstAmount(
                gstAmount
        );


        // -----------------------------------------------------
        // CALCULATE TOTAL
        // -----------------------------------------------------

        BigDecimal totalAmount =
                amount
                        .add(gstAmount)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        sale.setTotalAmount(
                totalAmount
        );


        // -----------------------------------------------------
        // GENERATE SALE NUMBER
        // -----------------------------------------------------

        log.info(
                "SERVICE - generating sale number..."
        );

        int year =
                dto.getSaleDate()
                        .getYear();

        String saleNumber =
                documentSequenceService
                        .generateDocumentNumber(
                                DocumentType.SALE,
                                year
                        );

        sale.setSaleNumber(
                saleNumber
        );


        // -----------------------------------------------------
        // SAVE SALE
        // -----------------------------------------------------

        log.info(
                "SERVICE - saving sale..."
        );

        sale =
                saleRepo.save(
                        sale
                );


        // -----------------------------------------------------
        // DEDUCT STOCK
        // -----------------------------------------------------

        log.info(
                "SERVICE - deducting sold quantity from stock pool..."
        );

        stockTransactionService.saleStockOut(
                sale.getRawMaterial(),
                sale.getUnit(),
                sale.getWeight(),
                sale.getSaleNumber()
        );


        log.info(
                "SERVICE - sale created and stock deducted successfully..."
        );


        return mapToResponse(
                sale
        );
    }


    // =========================================================
    // GET SALE BY PUBLIC ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public ResponseSaleDTO getSaleByPublicId(
            UUID publicId
    ) {

        log.info(
                "SERVICE - request came in getSaleByPublicId..."
        );

        User currentUser = currentUserService.getCurrentUser();

        Sale sale;

        if (currentUser.getRole() == UserRole.ADMIN) {
            sale = saleRepo.findByPublicId(publicId)
                    .orElseThrow(() -> {
                        log.info("SERVICE - sale not found...");
                        return new ResourceNotFoundException("Sale not found");
                    });
        } else {
            sale = saleRepo.findByUserAndPublicId(currentUser, publicId)
                    .orElseThrow(() -> {
                        log.info("SERVICE - sale not found for current user...");
                        return new ResourceNotFoundException("Sale not found");
                    });
        }

        return mapToResponse(sale);
    }


    // =========================================================
    // GET ALL SALES
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<ResponseSaleDTO> getAllSales(org.springframework.data.domain.Pageable pageable) {

        log.info(
                "SERVICE - request came in getAllSales..."
        );

        User currentUser = currentUserService.getCurrentUser();

        return saleRepo.findByUser(currentUser, pageable).map(this::mapToResponse);
    }


    // =========================================================
    // UPDATE SALE
    // =========================================================

    @Override
    @Transactional
    public ResponseSaleDTO updateSale(
            UUID publicId,
            RequestSaleDTO dto
    ) {

        log.info(
                "SERVICE - request came in updateSale..."
        );


        // -----------------------------------------------------
        // FIND SALE
        // -----------------------------------------------------

        User currentUser = currentUserService.getCurrentUser();

        Sale sale;

        if (currentUser.getRole() == UserRole.ADMIN) {
            sale = saleRepo.findByPublicId(publicId)
                    .orElseThrow(() -> {
                        log.info("SERVICE - sale not found...");
                        return new ResourceNotFoundException("Sale not found");
                    });
        } else {
            sale = saleRepo.findByUserAndPublicId(currentUser, publicId)
                    .orElseThrow(() -> {
                        log.info("SERVICE - sale not found for current user...");
                        return new ResourceNotFoundException("Sale not found");
                    });
        }


        // -----------------------------------------------------
        // PREVENT STOCK-AFFECTING CHANGES
        // -----------------------------------------------------

        if (!sale.getRawMaterial()
                .equalsIgnoreCase(
                        dto.getRawMaterial().trim()
                )) {

            throw new InvalidRequestException(
                    "Raw material cannot be updated after sale creation. Stock ledger already updated."
            );
        }

        if (sale.getUnit() != dto.getUnit()) {

            throw new InvalidRequestException(
                    "Weight unit cannot be updated after sale creation. Stock ledger already updated."
            );
        }

        if (sale.getWeight().compareTo(dto.getWeight()) != 0) {

            throw new InvalidRequestException(
                    "Weight cannot be updated after sale creation. Stock ledger already updated."
            );
        }


        // -----------------------------------------------------
        // UPDATE CUSTOMER IF CHANGED
        // -----------------------------------------------------

        Customer currentCustomer =
                sale.getCustomer();

        if (!currentCustomer
                .getPublicId()
                .equals(dto.getCustomerPublicId())) {

            log.info(
                    "SERVICE - customer changed, fetching new active customer..."
            );

            Customer newCustomer =
                    customerRepo
                            .findByUserAndPublicIdAndIsActiveTrue(
                                    currentUser,
                                    dto.getCustomerPublicId()
                            )
                            .orElseThrow(() -> new InvalidRequestException(
                                    "Active customer not found"
                            ));

            sale.setCustomer(newCustomer);
        }


        // -----------------------------------------------------
        // CHECK CUSTOMER INVOICE NUMBER
        // -----------------------------------------------------

        String newCustomerInvoiceNumber =
                dto.getCustomerInvoiceNumber();

        if (newCustomerInvoiceNumber != null) {

            newCustomerInvoiceNumber =
                    newCustomerInvoiceNumber.trim();
        }

        String existingCustomerInvoiceNumber =
                sale.getCustomerInvoiceNumber();

        if (!Objects.equals(
                existingCustomerInvoiceNumber,
                newCustomerInvoiceNumber
        )) {

            if (newCustomerInvoiceNumber != null
                    && !newCustomerInvoiceNumber.isBlank()) {

                boolean exists =
                        saleRepo
                                .existsByCustomerInvoiceNumberAndCustomer(
                                        newCustomerInvoiceNumber,
                                        sale.getCustomer()
                                );

                if (exists) {

                    throw new InvalidRequestException(
                            "Sale with this customer invoice number already exists"
                    );
                }
            }

            sale.setCustomerInvoiceNumber(
                    newCustomerInvoiceNumber
            );
        }


        // -----------------------------------------------------
        // UPDATE NON-STOCK FIELDS
        // -----------------------------------------------------

        sale.setRatePerUnit(
                dto.getRatePerUnit()
        );

        sale.setGstPercentage(
                dto.getGstPercentage()
        );

        sale.setSaleDate(
                dto.getSaleDate()
        );


        // -----------------------------------------------------
        // RE-CALCULATE AMOUNT
        // -----------------------------------------------------

        BigDecimal amount =
                sale.getWeight()
                        .multiply(
                                dto.getRatePerUnit()
                        )
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        sale.setAmount(
                amount
        );


        // -----------------------------------------------------
        // RE-CALCULATE GST
        // -----------------------------------------------------

        BigDecimal gstAmount =
                amount
                        .multiply(
                                dto.getGstPercentage()
                        )
                        .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP
                        );

        sale.setGstAmount(
                gstAmount
        );


        // -----------------------------------------------------
        // RE-CALCULATE TOTAL
        // -----------------------------------------------------

        BigDecimal totalAmount =
                amount
                        .add(gstAmount)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        sale.setTotalAmount(
                totalAmount
        );


        // -----------------------------------------------------
        // SAVE SALE
        // -----------------------------------------------------

        sale =
                saleRepo.save(
                        sale
                );

        log.info(
                "SERVICE - sale updated successfully..."
        );


        return mapToResponse(
                sale
        );
    }


    // =========================================================
    // ENTITY → RESPONSE DTO
    // =========================================================

    private ResponseSaleDTO mapToResponse(
            Sale sale
    ) {

        log.info(
                "SERVICE - mapping sale to response DTO..."
        );

        ResponseSaleDTO response =
                modelMapper.map(
                        sale,
                        ResponseSaleDTO.class
                );

        Customer customer =
                sale.getCustomer();

        if (customer != null) {

            ResponseSaleDTO.CustomerDetails customerDetails =
                    new ResponseSaleDTO.CustomerDetails();

            customerDetails.setPublicId(
                    customer.getPublicId()
            );

            customerDetails.setCustomerName(
                    customer.getCustomerName()
            );

            customerDetails.setMobileNumber(
                    customer.getMobileNumber()
            );

            customerDetails.setEmail(
                    customer.getEmail()
            );

            customerDetails.setGstNumber(
                    customer.getGstNumber()
            );

            response.setCustomer(
                    customerDetails
            );
        }

        return response;
    }
}
