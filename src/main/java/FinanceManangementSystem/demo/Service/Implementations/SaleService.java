package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Enums.DocumentType;
import FinanceManangementSystem.demo.Enums.SalePaymentStatus;
import FinanceManangementSystem.demo.Enums.SaleStatus;
import FinanceManangementSystem.demo.Model.Customer;
import FinanceManangementSystem.demo.Model.Sale;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestSaleDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseSaleDTO;
import FinanceManangementSystem.demo.Repository.CustomerRepository;
import FinanceManangementSystem.demo.Repository.SalePaymentRepository;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaleService
        implements SaleServiceInterface {

    private final SaleRepository saleRepo;

    private final SalePaymentRepository salePaymentRepo;

    private final CustomerRepository customerRepo;

    private final DocumentSequenceService documentSequenceService;

    private final ModelMapper modelMapper;

    private final StockTransactionServiceInterface
            stockTransactionService;


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

        Customer customer =
                customerRepo
                        .findByPublicIdAndIsActiveTrue(
                                dto.getCustomerPublicId()
                        )
                        .orElseThrow(() -> {

                            log.info(
                                    "SERVICE - active customer not found..."
                            );

                            return new RuntimeException(
                                    "Active customer not found"
                            );
                        });


        // -----------------------------------------------------
        // NORMALIZE CUSTOMER INVOICE NUMBER
        // -----------------------------------------------------

        String customerInvoiceNumber =
                normalizeString(
                        dto.getCustomerInvoiceNumber()
                );


        // -----------------------------------------------------
        // CHECK CUSTOMER INVOICE NUMBER
        // -----------------------------------------------------

        if (customerInvoiceNumber != null
                && saleRepo
                .existsByCustomerInvoiceNumberAndCustomer(
                        customerInvoiceNumber,
                        customer
                )) {

            throw new RuntimeException(
                    "Sale with this customer invoice number already exists"
            );
        }


        // -----------------------------------------------------
        // CREATE SALE
        // -----------------------------------------------------

        Sale sale =
                new Sale();


        sale.setCustomer(
                customer
        );


        sale.setProduct(
                dto.getProduct().trim()
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
        // SERVICE CONTROLLED STATUS
        // -----------------------------------------------------

        sale.setSaleStatus(
                SaleStatus.ACTIVE
        );


        sale.setPaymentStatus(
                SalePaymentStatus.PENDING
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

        sale =
                saleRepo.save(
                        sale
                );


        // -----------------------------------------------------
        // DEDUCT STOCK
        // -----------------------------------------------------

        log.info(
                "SERVICE - deducting sold quantity from stock..."
        );


        stockTransactionService.saleStockOut(
                sale.getProduct(),
                sale.getUnit(),
                sale.getWeight(),
                sale.getSaleNumber()
        );


        log.info(
                "SERVICE - sale and stock deduction completed..."
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

        Sale sale =
                saleRepo
                        .findByPublicId(
                                publicId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Sale not found"
                                )
                        );


        return mapToResponse(
                sale
        );
    }


    // =========================================================
    // GET ALL SALES
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseSaleDTO> getAllSales() {

        return saleRepo
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // GET SALES BY CUSTOMER
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseSaleDTO> getSalesByCustomer(
            UUID customerPublicId
    ) {

        Customer customer =
                customerRepo
                        .findByPublicId(
                                customerPublicId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customer not found"
                                )
                        );


        return saleRepo
                .findByCustomer(
                        customer
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // GET SALES BY DATE RANGE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseSaleDTO> getSalesByDateRange(
            LocalDate fromDate,
            LocalDate toDate
    ) {

        if (fromDate == null ||
                toDate == null) {

            throw new RuntimeException(
                    "From date and to date are required"
            );
        }


        if (fromDate.isAfter(toDate)) {

            throw new RuntimeException(
                    "From date cannot be after to date"
            );
        }


        return saleRepo
                .findBySaleDateBetween(
                        fromDate,
                        toDate
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // GET SALES BY STATUS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseSaleDTO> getSalesByStatus(
            String status
    ) {

        if (status == null ||
                status.isBlank()) {

            throw new RuntimeException(
                    "Sale status is required"
            );
        }


        SaleStatus saleStatus;

        try {

            saleStatus =
                    SaleStatus.valueOf(
                            status.trim().toUpperCase()
                    );

        } catch (IllegalArgumentException exception) {

            throw new RuntimeException(
                    "Invalid sale status"
            );
        }


        return saleRepo
                .findBySaleStatus(
                        saleStatus
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
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

        Sale sale =
                saleRepo
                        .findByPublicId(
                                publicId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Sale not found"
                                )
                        );


        // -----------------------------------------------------
        // CHECK SALE STATUS
        // -----------------------------------------------------

        if (sale.getSaleStatus()
                == SaleStatus.CANCELLED) {

            throw new RuntimeException(
                    "Cancelled sale cannot be updated"
            );
        }


        // -----------------------------------------------------
        // STOCK-AFFECTING DATA CANNOT CHANGE
        // -----------------------------------------------------

        if (!sale.getProduct()
                .equalsIgnoreCase(
                        dto.getProduct().trim()
                )) {

            throw new RuntimeException(
                    "Product cannot be changed after sale creation"
            );
        }


        if (sale.getWeight()
                .compareTo(
                        dto.getWeight()
                ) != 0) {

            throw new RuntimeException(
                    "Sale quantity cannot be changed after sale creation"
            );
        }


        if (sale.getUnit()
                != dto.getUnit()) {

            throw new RuntimeException(
                    "Sale unit cannot be changed after sale creation"
            );
        }


        // -----------------------------------------------------
        // FIND ACTIVE CUSTOMER
        // -----------------------------------------------------

        Customer customer =
                customerRepo
                        .findByPublicIdAndIsActiveTrue(
                                dto.getCustomerPublicId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Active customer not found"
                                )
                        );


        // -----------------------------------------------------
        // CUSTOMER INVOICE
        // -----------------------------------------------------

        String newInvoiceNumber =
                normalizeString(
                        dto.getCustomerInvoiceNumber()
                );


        String oldInvoiceNumber =
                sale.getCustomerInvoiceNumber();


        boolean invoiceChanged =
                !Objects.equals(
                        oldInvoiceNumber,
                        newInvoiceNumber
                );


        boolean customerChanged =
                !sale.getCustomer()
                        .getPublicId()
                        .equals(
                                customer.getPublicId()
                        );


        if ((invoiceChanged || customerChanged)
                && newInvoiceNumber != null
                && saleRepo
                .existsByCustomerInvoiceNumberAndCustomerAndPublicIdNot(
                        newInvoiceNumber,
                        customer,
                        publicId
                )) {

            throw new RuntimeException(
                    "Sale with this customer invoice number already exists"
            );
        }


        // -----------------------------------------------------
        // UPDATE CUSTOMER
        // -----------------------------------------------------

        sale.setCustomer(
                customer
        );


        // -----------------------------------------------------
        // UPDATE NON-STOCK DETAILS
        // -----------------------------------------------------

        sale.setRatePerUnit(
                dto.getRatePerUnit()
        );


        sale.setGstPercentage(
                dto.getGstPercentage()
        );


        sale.setCustomerInvoiceNumber(
                newInvoiceNumber
        );


        sale.setSaleDate(
                dto.getSaleDate()
        );


        // -----------------------------------------------------
        // RECALCULATE AMOUNT
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
        // RECALCULATE GST
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
        // RECALCULATE TOTAL
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
        // RECALCULATE PAYMENT STATUS
        // -----------------------------------------------------

        updatePaymentStatus(
                sale
        );


        // -----------------------------------------------------
        // SAVE SALE
        // -----------------------------------------------------

        sale =
                saleRepo.save(
                        sale
                );


        return mapToResponse(
                sale
        );
    }


    // =========================================================
    // CANCEL SALE
    // =========================================================

    @Override
    @Transactional
    public void cancelSale(
            UUID publicId
    ) {

        Sale sale =
                saleRepo
                        .findByPublicId(
                                publicId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Sale not found"
                                )
                        );


        // -----------------------------------------------------
        // CHECK STATUS
        // -----------------------------------------------------

        if (sale.getSaleStatus()
                == SaleStatus.CANCELLED) {

            throw new RuntimeException(
                    "Sale is already cancelled"
            );
        }


        // -----------------------------------------------------
        // REVERSE STOCK
        // -----------------------------------------------------

        stockTransactionService.saleCancelStockIn(
                sale.getProduct(),
                sale.getUnit(),
                sale.getWeight(),
                sale.getSaleNumber()
        );


        // -----------------------------------------------------
        // CANCEL SALE
        // -----------------------------------------------------

        sale.setSaleStatus(
                SaleStatus.CANCELLED
        );


        // -----------------------------------------------------
        // CANCEL PAYMENT STATUS
        // -----------------------------------------------------

        sale.setPaymentStatus(
                SalePaymentStatus.CANCELLED
        );


        // -----------------------------------------------------
        // SAVE
        // -----------------------------------------------------

        saleRepo.save(
                sale
        );


        log.info(
                "SERVICE - sale cancelled and stock reversed successfully..."
        );
    }


    // =========================================================
    // UPDATE PAYMENT STATUS
    // =========================================================

    private void updatePaymentStatus(
            Sale sale
    ) {

        BigDecimal totalPaid =
                salePaymentRepo
                        .getTotalPaidAmount(
                                sale
                        );


        if (totalPaid == null) {

            totalPaid =
                    BigDecimal.ZERO;
        }


        totalPaid =
                totalPaid.setScale(
                        2,
                        RoundingMode.HALF_UP
                );


        BigDecimal totalAmount =
                sale.getTotalAmount()
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );


        if (totalPaid.compareTo(
                BigDecimal.ZERO
        ) == 0) {

            sale.setPaymentStatus(
                    SalePaymentStatus.PENDING
            );

        } else if (totalPaid.compareTo(
                totalAmount
        ) >= 0) {

            sale.setPaymentStatus(
                    SalePaymentStatus.PAID
            );

        } else {

            sale.setPaymentStatus(
                    SalePaymentStatus.PARTIAL
            );
        }
    }


    // =========================================================
    // ENTITY → RESPONSE DTO
    // =========================================================

    private ResponseSaleDTO mapToResponse(
            Sale sale
    ) {

        ResponseSaleDTO response =
                modelMapper.map(
                        sale,
                        ResponseSaleDTO.class
                );


        Customer customer =
                sale.getCustomer();


        if (customer != null) {

            ResponseSaleDTO.CustomerDetails
                    customerDetails =
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
}