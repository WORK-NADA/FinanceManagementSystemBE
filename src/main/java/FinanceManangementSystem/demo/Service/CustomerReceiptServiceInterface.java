public interface CustomerReceiptServiceInterface {

    ResponseCustomerReceiptDTO addReceipt(
            RequestCustomerReceiptDTO dto
    );

    ResponseCustomerReceiptDTO getReceiptByPublicId(
            UUID publicId
    );

    List<ResponseCustomerReceiptDTO> getReceiptsByCustomer(
            UUID customerPublicId
    );

    List<ResponseCustomerReceiptDTO> getReceiptsBySale(
            UUID salePublicId
    );
}