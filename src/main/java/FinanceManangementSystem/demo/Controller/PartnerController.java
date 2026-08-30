package FinanceManangementSystem.demo.Controller;

import FinanceManangementSystem.demo.APIResponse.APIResponse;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestPartnerDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponsePartnerDTO;
import FinanceManangementSystem.demo.Service.PartnerServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/partner")
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerServiceInterface partnerService;

    @PostMapping("/add")
    public ResponseEntity<APIResponse<ResponsePartnerDTO>> addPartner(@Valid @RequestBody RequestPartnerDTO dto) {
        log.info("CONTROLLER - request came in addPartner...");

        ResponsePartnerDTO resp = partnerService.addPartner(dto);

        return ResponseEntity.ok(new APIResponse<>("Partner added successfully", resp));
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<APIResponse<ResponsePartnerDTO>> getPartner(@PathVariable UUID publicId) {
        log.info("CONTROLLER - request came in getPartner...");

        ResponsePartnerDTO resp = partnerService.getPartnerByPublicId(publicId);

        return ResponseEntity.ok(new APIResponse<>("Partner fetched", resp));
    }

    @GetMapping("/all")
    public ResponseEntity<APIResponse<List<ResponsePartnerDTO>>> getAll() {
        log.info("CONTROLLER - request came in getAllPartners...");

        List<ResponsePartnerDTO> list = partnerService.getAllPartners();

        return ResponseEntity.ok(new APIResponse<>("All partners fetched", list));
    }

    @GetMapping("/active")
    public ResponseEntity<APIResponse<List<ResponsePartnerDTO>>> getActive() {
        log.info("CONTROLLER - request came in getAllActivePartners...");

        List<ResponsePartnerDTO> list = partnerService.getAllActivePartners();

        return ResponseEntity.ok(new APIResponse<>("Active partners fetched", list));
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<APIResponse<ResponsePartnerDTO>> updatePartner(
            @PathVariable UUID publicId,
            @Valid @RequestBody RequestPartnerDTO dto
    ) {
        log.info("CONTROLLER - request came in updatePartner...");

        ResponsePartnerDTO resp = partnerService.updatePartner(publicId, dto);

        return ResponseEntity.ok(new APIResponse<>("Partner updated", resp));
    }

    @PatchMapping("/{publicId}/deactivate")
    public ResponseEntity<APIResponse<Void>> deactivate(@PathVariable UUID publicId) {
        log.info("CONTROLLER - request came in deactivatePartner...");

        partnerService.deactivatePartner(publicId);

        return ResponseEntity.ok(new APIResponse<>("Partner deactivated", null));
    }

    @PatchMapping("/{publicId}/reactivate")
    public ResponseEntity<APIResponse<Void>> reactivate(@PathVariable UUID publicId) {
        log.info("CONTROLLER - request came in reactivatePartner...");

        partnerService.reactivatePartner(publicId);

        return ResponseEntity.ok(new APIResponse<>("Partner reactivated", null));
    }
}
