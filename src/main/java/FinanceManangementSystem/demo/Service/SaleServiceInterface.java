public interface SaleServiceInterface {

    ResponseSaleDTO addSale(RequestSaleDTO dto);

    ResponseSaleDTO getSaleByPublicId(UUID publicId);

    List<ResponseSaleDTO> getAllSales();

    ResponseSaleDTO updateSale(
            UUID publicId,
            RequestSaleDTO dto
    );

    void cancelSale(UUID publicId);
}