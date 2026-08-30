package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Enums.DocumentType;
import FinanceManangementSystem.demo.Enums.ExpenseCategory;
import FinanceManangementSystem.demo.Model.Expense;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestExpenseDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseExpenseDTO;
import FinanceManangementSystem.demo.Repository.ExpenseRepository;
import FinanceManangementSystem.demo.Service.ExpenseServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseService
        implements ExpenseServiceInterface {

    private final ExpenseRepository expenseRepo;

    private final DocumentSequenceService documentSequenceService;

    private final ModelMapper modelMapper;


    // =========================================================
    // ADD EXPENSE
    // =========================================================

    @Override
    @Transactional
    public ResponseExpenseDTO addExpense(
            RequestExpenseDTO dto
    ) {

        log.info(
                "SERVICE - request came in addExpense..."
        );

        int year =
                dto.getExpenseDate()
                        .getYear();

        String expenseNumber =
                documentSequenceService
                        .generateDocumentNumber(
                                DocumentType.EXPENSE,
                                year
                        );

        Expense expense = new Expense();

        expense.setCategory(
                dto.getCategory()
        );

        expense.setAmount(
                dto.getAmount()
        );

        expense.setExpenseDate(
                dto.getExpenseDate()
        );

        expense.setPaymentMode(
                dto.getPaymentMode()
        );

        expense.setExpenseNumber(
                expenseNumber
        );

        expense.setDescription(
                dto.getDescription().trim()
        );

        expense.setRemarks(
                dto.getRemarks() != null ? dto.getRemarks().trim() : null
        );

        expense.setIsActive(
                true
        );

        expense =
                expenseRepo.save(
                        expense
                );

        log.info(
                "SERVICE - expense added successfully..."
        );

        return mapToResponse(
                expense
        );
    }


    // =========================================================
    // GET EXPENSE BY PUBLIC ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public ResponseExpenseDTO getExpenseByPublicId(
            UUID publicId
    ) {

        log.info(
                "SERVICE - request came in getExpenseByPublicId..."
        );

        Expense expense =
                expenseRepo
                        .findByPublicId(
                                publicId
                        )
                        .orElseThrow(() -> {

                            log.info(
                                    "SERVICE - expense not found..."
                            );

                            return new RuntimeException(
                                    "Expense not found"
                            );
                        });

        return mapToResponse(
                expense
        );
    }


    // =========================================================
    // GET ALL EXPENSES
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<ResponseExpenseDTO> getAllExpenses(org.springframework.data.domain.Pageable pageable) {

        log.info(
                "SERVICE - request came in getAllExpenses..."
        );

        return expenseRepo
                .findByIsActiveTrueOrderByExpenseDateDesc(pageable)
                .map(this::mapToResponse);
    }


    // =========================================================
    // UPDATE EXPENSE
    // =========================================================

    @Override
    @Transactional
    public ResponseExpenseDTO updateExpense(
            UUID publicId,
            RequestExpenseDTO dto
    ) {

        log.info(
                "SERVICE - request came in updateExpense..."
        );

        Expense expense =
                expenseRepo
                        .findByPublicIdAndIsActiveTrue(
                                publicId
                        )
                        .orElseThrow(() -> {

                            log.info(
                                    "SERVICE - inactive or non-existent expense cannot be updated..."
                            );

                            return new RuntimeException(
                                    "Inactive expense cannot be updated"
                            );
                        });

        expense.setCategory(
                dto.getCategory()
        );

        expense.setAmount(
                dto.getAmount()
        );

        expense.setExpenseDate(
                dto.getExpenseDate()
        );

        expense.setPaymentMode(
                dto.getPaymentMode()
        );

        expense.setDescription(
                dto.getDescription().trim()
        );

        expense.setRemarks(
                dto.getRemarks() != null ? dto.getRemarks().trim() : null
        );

        expense =
                expenseRepo.save(
                        expense
                );

        log.info(
                "SERVICE - expense updated successfully..."
        );

        return mapToResponse(
                expense
        );
    }


    // =========================================================
    // DELETE (SOFT DELETE / DEACTIVATE) EXPENSE
    // =========================================================

    @Override
    @Transactional
    public void deleteExpense(
            UUID publicId
    ) {

        log.info(
                "SERVICE - request came in deleteExpense..."
        );

        Expense expense =
                expenseRepo
                        .findByPublicId(
                                publicId
                        )
                        .orElseThrow(() -> new RuntimeException(
                                "Expense not found"
                        ));

        if (!expense.getIsActive()) {

            log.info(
                    "SERVICE - expense is already inactive..."
            );

            throw new RuntimeException(
                    "Expense is already inactive"
            );
        }

        expense.setIsActive(
                false
        );

        expenseRepo.save(
                expense
        );

        log.info(
                "SERVICE - expense deactivated successfully..."
        );
    }


    // =========================================================
    // GET EXPENSES BY DATE RANGE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseExpenseDTO> getExpensesByDateRange(
            LocalDate fromDate,
            LocalDate toDate
    ) {

        log.info(
                "SERVICE - request came in getExpensesByDateRange..."
        );

        return expenseRepo
                .findByExpenseDateBetweenAndIsActiveTrueOrderByExpenseDateDesc(
                        fromDate,
                        toDate
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // GET EXPENSES BY CATEGORY
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseExpenseDTO> getExpensesByCategory(
            ExpenseCategory category
    ) {

        log.info(
                "SERVICE - request came in getExpensesByCategory..."
        );

        return expenseRepo
                .findByCategoryAndIsActiveTrueOrderByExpenseDateDesc(
                        category
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // GET TOTAL EXPENSES (DASHBOARD SUMMARY)
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalExpenses(
            LocalDate fromDate,
            LocalDate toDate
    ) {

        log.info(
                "SERVICE - request came in getTotalExpenses..."
        );

        return expenseRepo.sumTotalExpensesByDateRange(
                fromDate,
                toDate
        );
    }


    // =========================================================
    // GET CATEGORY-WISE BREAKDOWN (DASHBOARD CHART)
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Map<ExpenseCategory, BigDecimal> getCategoryWiseBreakdown(
            LocalDate fromDate,
            LocalDate toDate
    ) {

        log.info(
                "SERVICE - request came in getCategoryWiseBreakdown..."
        );

        Map<ExpenseCategory, BigDecimal> breakdown =
                new EnumMap<>(ExpenseCategory.class);

        for (ExpenseCategory cat : ExpenseCategory.values()) {
            breakdown.put(cat, BigDecimal.ZERO);
        }

        List<Object[]> results =
                expenseRepo.findCategoryWiseBreakdownByDateRange(
                        fromDate,
                        toDate
                );

        for (Object[] row : results) {

            ExpenseCategory category =
                    (ExpenseCategory) row[0];

            BigDecimal totalAmount =
                    (BigDecimal) row[1];

            if (category != null && totalAmount != null) {
                breakdown.put(category, totalAmount);
            }
        }

        return breakdown;
    }


    // =========================================================
    // HELPER: MAP TO RESPONSE DTO
    // =========================================================

    private ResponseExpenseDTO mapToResponse(
            Expense expense
    ) {

        return modelMapper.map(
                expense,
                ResponseExpenseDTO.class
        );
    }
}
