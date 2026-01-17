package dangod.springboot.controller;

import dangod.springboot.common.BusinessException;
import dangod.springboot.common.Result;
import dangod.springboot.entity.BodyTest;
import dangod.springboot.entity.TrainingPlan;
import dangod.springboot.service.BodyTestService;
import dangod.springboot.service.TrainingPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/body-test")
@Api("体测管理API")
public class BodyTestController {
    private static final Logger logger = LoggerFactory.getLogger(BodyTestController.class);
    
    @Autowired
    private BodyTestService bodyTestService;
    
    @Autowired
    private TrainingPlanService trainingPlanService;
    
    @PostMapping("/create")
    @ApiOperation("创建体测记录")
    public Result createBodyTest(@RequestBody BodyTest bodyTest) {
        try {
            BodyTest created = bodyTestService.createBodyTest(bodyTest);
            return Result.success(created);
        } catch (BusinessException e) {
            logger.error("创建体测记录失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("创建体测记录异常", e);
            return Result.error("创建体测记录异常: " + e.getMessage());
        }
    }
    
    @PutMapping("/update")
    @ApiOperation("更新体测记录")
    public Result updateBodyTest(@RequestBody BodyTest bodyTest) {
        try {
            BodyTest updated = bodyTestService.updateBodyTest(bodyTest);
            return Result.success(updated);
        } catch (BusinessException e) {
            logger.error("更新体测记录失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("更新体测记录异常", e);
            return Result.error("更新体测记录异常: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/delete/{testId}")
    @ApiOperation("删除体测记录")
    public Result deleteBodyTest(@PathVariable Long testId) {
        try {
            bodyTestService.deleteBodyTest(testId);
            return Result.success("删除成功");
        } catch (BusinessException e) {
            logger.error("删除体测记录失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("删除体测记录异常", e);
            return Result.error("删除体测记录异常: " + e.getMessage());
        }
    }
    
    @GetMapping("/get/{testId}")
    @ApiOperation("获取体测记录详情")
    public Result getBodyTest(@PathVariable Long testId) {
        try {
            BodyTest bodyTest = bodyTestService.getBodyTestById(testId);
            if (bodyTest == null) {
                return Result.error("体测记录不存在");
            }
            return Result.success(bodyTest);
        } catch (Exception e) {
            logger.error("获取体测记录异常", e);
            return Result.error("获取体测记录异常: " + e.getMessage());
        }
    }
    
    @GetMapping("/by-member/{memberId}")
    @ApiOperation("获取会员体测记录列表")
    public Result getBodyTestsByMember(@PathVariable Long memberId) {
        try {
            List<BodyTest> bodyTests = bodyTestService.getBodyTestsByMember(memberId);
            return Result.success(bodyTests);
        } catch (Exception e) {
            logger.error("获取会员体测记录列表异常", e);
            return Result.error("获取会员体测记录列表异常: " + e.getMessage());
        }
    }
    
    @GetMapping("/latest/{memberId}")
    @ApiOperation("获取会员最新体测记录")
    public Result getLatestBodyTest(@PathVariable Long memberId) {
        try {
            BodyTest bodyTest = bodyTestService.getLatestBodyTest(memberId);
            if (bodyTest == null) {
                return Result.error("暂无体测记录");
            }
            return Result.success(bodyTest);
        } catch (Exception e) {
            logger.error("获取最新体测记录异常", e);
            return Result.error("获取最新体测记录异常: " + e.getMessage());
        }
    }
    
    @GetMapping("/chart/{memberId}")
    @ApiOperation("生成体测变化图表")
    public ResponseEntity<byte[]> generateChart(
            @PathVariable Long memberId,
            @ApiParam("指标类型: weight, bmi, bodyFat, muscleMass, visceralFat, bodyWater, metabolism") 
            @RequestParam(defaultValue = "weight") String indicator) {
        try {
            byte[] chartData = bodyTestService.generateBodyTestChart(memberId, indicator);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(chartData.length);
            
            return new ResponseEntity<>(chartData, headers, HttpStatus.OK);
        } catch (BusinessException e) {
            logger.error("生成图表失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("生成图表异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/abnormal")
    @ApiOperation("获取异常体测记录列表")
    public Result getAbnormalTests() {
        try {
            List<BodyTest> abnormalTests = bodyTestService.getAbnormalTests();
            return Result.success(abnormalTests);
        } catch (Exception e) {
            logger.error("获取异常体测记录列表异常", e);
            return Result.error("获取异常体测记录列表异常: " + e.getMessage());
        }
    }
    
    @PostMapping("/plan/create")
    @ApiOperation("创建训练计划")
    public Result createTrainingPlan(@RequestBody TrainingPlan plan) {
        try {
            TrainingPlan created = trainingPlanService.createPlan(plan);
            return Result.success(created);
        } catch (BusinessException e) {
            logger.error("创建训练计划失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("创建训练计划异常", e);
            return Result.error("创建训练计划异常: " + e.getMessage());
        }
    }
    
    @PutMapping("/plan/update")
    @ApiOperation("更新训练计划")
    public Result updateTrainingPlan(@RequestBody TrainingPlan plan) {
        try {
            TrainingPlan updated = trainingPlanService.updatePlan(plan);
            return Result.success(updated);
        } catch (BusinessException e) {
            logger.error("更新训练计划失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("更新训练计划异常", e);
            return Result.error("更新训练计划异常: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/plan/delete/{planId}")
    @ApiOperation("删除训练计划")
    public Result deleteTrainingPlan(@PathVariable Long planId) {
        try {
            trainingPlanService.deletePlan(planId);
            return Result.success("删除成功");
        } catch (BusinessException e) {
            logger.error("删除训练计划失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("删除训练计划异常", e);
            return Result.error("删除训练计划异常: " + e.getMessage());
        }
    }
    
    @GetMapping("/plan/get/{planId}")
    @ApiOperation("获取训练计划详情")
    public Result getTrainingPlan(@PathVariable Long planId) {
        try {
            TrainingPlan plan = trainingPlanService.getPlanById(planId);
            if (plan == null) {
                return Result.error("训练计划不存在");
            }
            return Result.success(plan);
        } catch (Exception e) {
            logger.error("获取训练计划异常", e);
            return Result.error("获取训练计划异常: " + e.getMessage());
        }
    }
    
    @GetMapping("/plan/by-member/{memberId}")
    @ApiOperation("获取会员训练计划列表")
    public Result getTrainingPlansByMember(@PathVariable Long memberId) {
        try {
            List<TrainingPlan> plans = trainingPlanService.getPlansByMember(memberId);
            return Result.success(plans);
        } catch (Exception e) {
            logger.error("获取会员训练计划列表异常", e);
            return Result.error("获取会员训练计划列表异常: " + e.getMessage());
        }
    }
    
    @GetMapping("/plan/active/{memberId}")
    @ApiOperation("获取会员当前有效训练计划")
    public Result getActiveTrainingPlan(@PathVariable Long memberId) {
        try {
            TrainingPlan plan = trainingPlanService.getActivePlan(memberId);
            if (plan == null) {
                return Result.error("暂无有效训练计划");
            }
            return Result.success(plan);
        } catch (Exception e) {
            logger.error("获取有效训练计划异常", e);
            return Result.error("获取有效训练计划异常: " + e.getMessage());
        }
    }
    
    @GetMapping("/export-report/{memberId}")
    @ApiOperation("导出健康报告")
    public ResponseEntity<byte[]> exportHealthReport(@PathVariable Long memberId) {
        try {
            byte[] reportData = trainingPlanService.exportHealthReport(memberId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "health_report_" + memberId + "_" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx");
            headers.setContentLength(reportData.length);
            
            return new ResponseEntity<>(reportData, headers, HttpStatus.OK);
        } catch (BusinessException e) {
            logger.error("导出健康报告失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("导出健康报告异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
