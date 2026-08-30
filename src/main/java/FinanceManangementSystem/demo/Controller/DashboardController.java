package FinanceManangementSystem.demo.Controller;

import FinanceManangementSystem.demo.APIResponse.APIResponse;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.DashboardSummaryDTO;
import FinanceManangementSystem.demo.Service.DashboardServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardServiceInterface dashboardService;

    @GetMapping
    public ResponseEntity<APIResponse<DashboardSummaryDTO>> getSummary() {
        log.info("CONTROLLER - request came in getDashboardSummary...");
        var dto = dashboardService.getDashboardSummary();
        return ResponseEntity.ok(new APIResponse<>("Dashboard summary", dto));
    }
}
