package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestExpenseDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseExpenseDTO;

import java.util.List;
import java.util.UUID;

public interface ExpenseServiceInterface {

    ResponseExpenseDTO addExpense(
            RequestExpenseDTO dto
    );

    ResponseExpenseDTO getExpenseByPublicId(
            UUID publicId
    );

    List<ResponseExpenseDTO> getAllExpenses();

    ResponseExpenseDTO updateExpense(
            UUID publicId,
            RequestExpenseDTO dto
    );

    void deleteExpense(
            UUID publicId
    );
}