package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Enums.DocumentType;
import FinanceManangementSystem.demo.Enums.PaymentStatus;
import FinanceManangementSystem.demo.Model.Customer;
import FinanceManangementSystem.demo.Model.Sale;
import FinanceManangementSystem.demo.Model.SalePayment;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestSalePaymentDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseSalePaymentDTO;
import FinanceManangementSystem.demo.Repository.CustomerRepository;
import FinanceManangementSystem.demo.Repository.SalePaymentRepository;
import FinanceManangementSystem.demo.Repository.SaleRepository;
import FinanceManangementSystem.demo.Service.SalePaymentServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalePaymentService
        implements SalePaymentServiceInterface {

    private final SalePaymentRepository salePaymentRepo;

    private final SaleRepository saleRepo;

    private final CustomerRepository customerRepo;

    private final DocumentSequenceService documentSequenceService;

    private final ModelMapper modelMapper;


    // =========================================================
    // ADD PAYMENT
    // =========================================================

    @Override
    @Transactional
    public ResponseSalePaymentDTO addPayment(
            RequestSalePaymentDTO dto
    ) {

        log.info(
                "SERVICE - request came in addPayment for sale..."
        );


        // -----------------------------------------------------
        // FIND SALE
        // -----------------------------------------------------

        Sale sale =
                saleRepo
                        .findByPublicId(
                                dto.getSalePublicId()
                        )
                        .orElseThrow(() -> {

                            log.info(
                                    "SERVICE - sale not found..."
                            );

                            return new RuntimeException(
                                    "Sale not found"
                            );
                        });


        // -----------------------------------------------------
        // VALIDATE AMOUNT
        // -----------------------------------------------------

        BigDecimal currentReceivedAmount =
                salePaymentRepo
                        .sumReceivedAmountBySale(
                                sale
                        );

        BigDecimal newTotalReceived =
                currentReceivedAmount
                        .add(
                                dto.getAmountReceived()
                        );

        if (newTotalReceived.compareTo(sale.getTotalAmount()) > 0) {

            log.info(
                    "SERVICE - payment amount exceeds pending balance..."
            );

            BigDecimal currentPending =
                    sale
                            .getTotalAmount()
                            .subtract(
                                    currentReceivedAmount
                            )
                            .max(
                                    BigDecimal.ZERO
                            );

            throw new RuntimeException(
                    "Payment amount exceeds remaining pending amount of " + currentPending
            );
        }


        // -----------------------------------------------------
        // GENERATE REFERENCE NUMBER
        // -----------------------------------------------------

        int year =
                dto.getPaymentDate()
                        .getYear();

        String referenceNumber =
                documentSequenceService
                        .generateDocumentNumber(
                                DocumentType.CUSTOMER_RECEIPT,
                                year
                        );


        // -----------------------------------------------------
        // CREATE PAYMENT ENTITY
        // -----------------------------------------------------

        SalePayment payment =
                new SalePayment();

        payment.setSale(
                sale
        );

        payment.setAmountReceived(
                dto.getAmountReceived()
        );

        payment.setPaymentDate(
                dto.getPaymentDate()
        );

        payment.setPaymentMode(
                dto.getPaymentMode()
        );

        payment.setReferenceNumber(
                referenceNumber
        );

        payment.setRemarks(
                dto.getRemarks() != null ? dto.getRemarks().trim() : null
        );

        payment =
                salePaymentRepo.save(
                        payment
                );


        // -----------------------------------------------------
        // UPDATE SALE PAYMENT STATUS
        // -----------------------------------------------------

        if (newTotalReceived.compareTo(sale.getTotalAmount()) >= 0) {

            sale.setPaymentStatus(
                    PaymentStatus.COMPLETED
            );

        } else if (newTotalReceived.compareTo(BigDecimal.ZERO) > 0) {

            sale.setPaymentStatus(
                    PaymentStatus.PARTIALLY_PAID
            );

        } else {

            sale.setPaymentStatus(
                    PaymentStatus.PENDING
            );
        }

        saleRepo.save(
                sale
        );


        log.info(
                "SERVICE - sale payment added successfully..."
        );


        return mapToResponse(
                payment,
                sale,
                newTotalReceived
        );
    }


    // =========================================================
    // GET PAYMENT BY PUBLIC ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public ResponseSalePaymentDTO getPaymentByPublicId(
            UUID publicId
    ) {

        log.info(
                "SERVICE - request came in getPaymentByPublicId for sale..."
        );

        SalePayment payment =
                salePaymentRepo
                        .findByPublicId(
                                publicId
                        )
                        .orElseThrow(() -> new RuntimeException(
                                "Sale payment not found"
                        ));

        Sale sale =
                payment.getSale();

        BigDecimal receivedAmount =
                salePaymentRepo
                        .sumReceivedAmountBySale(
                                sale
                        );

        return mapToResponse(
                payment,
                sale,
                receivedAmount
        );
    }


    // =========================================================
    // GET PAYMENTS BY SALE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseSalePaymentDTO> getPaymentsBySale(
            UUID salePublicId
    ) {

        log.info(
                "SERVICE - request came in getPaymentsBySale..."
        );

        Sale sale =
                saleRepo
                        .findByPublicId(
                                salePublicId
                        )
                        .orElseThrow(() -> new RuntimeException(
                                "Sale not found"
                        ));

        BigDecimal receivedAmount =
                salePaymentRepo
                        .sumReceivedAmountBySale(
                                sale
                        );

        return salePaymentRepo
                .findBySaleOrderByPaymentDateDesc(
                        sale
                )
                .stream()
                .map(payment -> mapToResponse(payment, sale, receivedAmount))
                .toList();
    }


    // =========================================================
    // GET PAYMENTS BY CUSTOMER
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseSalePaymentDTO> getPaymentsByCustomer(
            UUID customerPublicId
    ) {

        log.info(
                "SERVICE - request came in getPaymentsByCustomer..."
        );

        customerRepo
                .findByPublicIdAndIsActiveTrue(
                        customerPublicId
                )
                .orElseThrow(() -> new RuntimeException(
                        "Active customer not found"
                ));

        return salePaymentRepo
                .findBySale_Customer_PublicIdOrderByPaymentDateDesc(
                        customerPublicId
                )
                .stream()
                .map(payment -> {
                    Sale sale = payment.getSale();
                    BigDecimal receivedAmount = salePaymentRepo.sumReceivedAmountBySale(sale);
                    return mapToResponse(payment, sale, receivedAmount);
                })
                .toList();
    }


    // =========================================================
    // GET SALE PAYMENT SUMMARY
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public ResponseSalePaymentDTO.SaleDetails getSalePaymentSummary(
            UUID salePublicId
    ) {

        log.info(
                "SERVICE - request came in getSalePaymentSummary..."
        );

        Sale sale =
                saleRepo
                        .findByPublicId(
                                salePublicId
                        )
                        .orElseThrow(() -> new RuntimeException(
                                "Sale not found"
                        ));

        return buildSaleDetails(
                sale
        );
    }


    // =========================================================
    // GET ALL PENDING PAYMENTS (DASHBOARD)
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseSalePaymentDTO.SaleDetails> getAllPendingPayments() {

        log.info(
                "SERVICE - request came in getAllPendingPayments for sales..."
        );

        List<Sale> pendingSales =
                saleRepo
                        .findByPaymentStatusIn(
                                List.of(
                                        PaymentStatus.PENDING,
                                        PaymentStatus.PARTIALLY_PAID
                                )
                        );

        return pendingSales
                .stream()
                .map(this::buildSaleDetails)
                .toList();
    }


    // =========================================================
    // GET PENDING PAYMENTS BY CUSTOMER (DASHBOARD)
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseSalePaymentDTO.SaleDetails> getPendingPaymentsByCustomer(
            UUID customerPublicId
    ) {

        log.info(
                "SERVICE - request came in getPendingPaymentsByCustomer..."
        );

        Customer customer =
                customerRepo
                        .findByPublicIdAndIsActiveTrue(
                                customerPublicId
                        )
                        .orElseThrow(() -> new RuntimeException(
                                "Active customer not found"
                        ));

        List<Sale> pendingSales =
                saleRepo
                        .findByCustomerAndPaymentStatusIn(
                                customer,
                                List.of(
                                        PaymentStatus.PENDING,
                                        PaymentStatus.PARTIALLY_PAID
                                )
                        );

        return pendingSales
                .stream()
                .map(this::buildSaleDetails)
                .toList();
    }

        @Override
        @Transactional(readOnly = true)
        public org.springframework.data.domain.Page<ResponseSalePaymentDTO> getAllPayments(org.springframework.data.domain.Pageable pageable) {

                log.info("SERVICE - request came in getAllPayments for sales...");

                return salePaymentRepo.findAll(pageable)
                                .map(payment -> {
                                        Sale sale = payment.getSale();
                                        java.math.BigDecimal receivedAmount = salePaymentRepo.sumReceivedAmountBySale(sale);
                                        return mapToResponse(payment, sale, receivedAmount);
                                });
        }


    // =========================================================
    // GET TOTAL RECEIVABLE AMOUNT (DASHBOARD SUMMARY)
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalReceivableAmount() {

        log.info(
                "SERVICE - request came in getTotalReceivableAmount..."
        );

        List<Sale> pendingSales =
                saleRepo
                        .findByPaymentStatusIn(
                                List.of(
                                        PaymentStatus.PENDING,
                                        PaymentStatus.PARTIALLY_PAID
                                )
                        );

        return pendingSales
                .stream()
                .map(sale -> {
                    BigDecimal received = salePaymentRepo.sumReceivedAmountBySale(sale);
                    return sale.getTotalAmount().subtract(received).max(BigDecimal.ZERO);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    // =========================================================
    // HELPER: BUILD SALE DETAILS
    // =========================================================

    private ResponseSalePaymentDTO.SaleDetails buildSaleDetails(
            Sale sale
    ) {

        BigDecimal receivedAmount =
                salePaymentRepo
                        .sumReceivedAmountBySale(
                                sale
                        );

        BigDecimal pendingAmount =
                sale
                        .getTotalAmount()
                        .subtract(
                                receivedAmount
                        )
                        .max(
                                BigDecimal.ZERO
                        );

        ResponseSalePaymentDTO.SaleDetails details =
                new ResponseSalePaymentDTO.SaleDetails();

        details.setPublicId(
                sale.getPublicId()
        );

        details.setSaleNumber(
                sale.getSaleNumber()
        );

        details.setTotalAmount(
                sale.getTotalAmount()
        );

        details.setReceivedAmount(
                receivedAmount
        );

        details.setPendingAmount(
                pendingAmount
        );

        details.setPaymentStatus(
                sale.getPaymentStatus()
        );

        return details;
    }


    // =========================================================
    // HELPER: MAP TO RESPONSE DTO
    // =========================================================

    private ResponseSalePaymentDTO mapToResponse(
            SalePayment payment,
            Sale sale,
            BigDecimal receivedAmount
    ) {

        ResponseSalePaymentDTO response =
                modelMapper.map(
                        payment,
                        ResponseSalePaymentDTO.class
                );

        response.setSale(
                buildSaleDetails(
                        sale
                )
        );

        return response;
    }
}
