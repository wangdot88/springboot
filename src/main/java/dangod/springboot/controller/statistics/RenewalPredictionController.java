package dangod.springboot.controller.statistics;

import dangod.springboot.dto.RenewalPredictionDto;
import dangod.springboot.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/renewal-predictions")
public class RenewalPredictionController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<List<RenewalPredictionDto>> predictMemberRenewals() {
        List<RenewalPredictionDto> predictions = statisticsService.predictMemberRenewals();
        return ResponseEntity.ok(predictions);
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<RenewalPredictionDto>> predictMemberRenewalsByStore(@PathVariable String storeId) {
        List<RenewalPredictionDto> predictions = statisticsService.predictMemberRenewalsByStore(storeId);
        return ResponseEntity.ok(predictions);
    }

    @GetMapping("/risk/{riskLevel}")
    public ResponseEntity<List<RenewalPredictionDto>> predictMemberRenewalsByRiskLevel(@PathVariable String riskLevel) {
        List<RenewalPredictionDto> predictions = statisticsService.predictMemberRenewalsByRiskLevel(riskLevel);
        return ResponseEntity.ok(predictions);
    }
}