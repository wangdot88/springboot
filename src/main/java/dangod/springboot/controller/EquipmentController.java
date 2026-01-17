package dangod.springboot.controller;

import dangod.springboot.core.common.Result;
import dangod.springboot.model.dto.EquipmentBorrowDTO;
import dangod.springboot.model.dto.EquipmentRepairDTO;
import dangod.springboot.service.EquipmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Api(tags = "体育器材管理")
@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @ApiOperation("获取器材列表")
    @GetMapping("/list")
    public Result getEquipmentList(
            @ApiParam("器材名称") @RequestParam(required = false) String name,
            @ApiParam("器材类别") @RequestParam(required = false) String category,
            @ApiParam("页码") @RequestParam(defaultValue = "0") int page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") int size) {
        return Result.success(equipmentService.getEquipmentList(name, category, page, size));
    }

    @ApiOperation("器材借出")
    @PostMapping("/borrow")
    public Result borrowEquipment(@RequestBody EquipmentBorrowDTO borrowDTO) {
        try {
            equipmentService.borrowEquipment(borrowDTO);
            return Result.success("借出成功");
        } catch (Exception e) {
            return Result.error("借出失败：" + e.getMessage());
        }
    }

    @ApiOperation("器材归还")
    @PutMapping("/return")
    public Result returnEquipment(
            @ApiParam("借据ID") @RequestParam Long borrowId,
            @ApiParam("归还数量") @RequestParam int returnQuantity) {
        try {
            equipmentService.returnEquipment(borrowId, returnQuantity);
            return Result.success("归还成功");
        } catch (Exception e) {
            return Result.error("归还失败：" + e.getMessage());
        }
    }

    @ApiOperation("器材报修")
    @PostMapping("/repair")
    public Result reportRepair(@RequestBody EquipmentRepairDTO repairDTO) {
        try {
            equipmentService.reportRepair(repairDTO);
            return Result.success("报修成功");
        } catch (Exception e) {
            return Result.error("报修失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新维修状态")
    @PutMapping("/repair/status")
    public Result updateRepairStatus(
            @ApiParam("报修ID") @RequestParam Long repairId,
            @ApiParam("维修状态") @RequestParam String status,
            @ApiParam("维修结果") @RequestParam(required = false) String repairResult,
            @ApiParam("维修费用") @RequestParam(required = false) Double repairCost) {
        try {
            equipmentService.updateRepairStatus(repairId, status, repairResult, repairCost);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取逾期借出列表")
    @GetMapping("/overdue")
    public Result getOverdueBorrows() {
        return Result.success(equipmentService.getOverdueBorrows());
    }

    @ApiOperation("获取低库存预警")
    @GetMapping("/low-stock-alert")
    public Result getLowStockAlert() {
        return Result.success(equipmentService.getLowStockAlert());
    }

    @ApiOperation("获取借出记录")
    @GetMapping("/borrow-records")
    public Result getBorrowRecords(
            @ApiParam("器材ID") @RequestParam(required = false) Long equipmentId,
            @ApiParam("学生ID") @RequestParam(required = false) Long studentId,
            @ApiParam("状态") @RequestParam(required = false) String status,
            @ApiParam("页码") @RequestParam(defaultValue = "0") int page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") int size) {
        return Result.success(equipmentService.getBorrowRecords(equipmentId, studentId, status, page, size));
    }

    @ApiOperation("获取维修记录")
    @GetMapping("/repair-records")
    public Result getRepairRecords(
            @ApiParam("器材ID") @RequestParam(required = false) Long equipmentId,
            @ApiParam("状态") @RequestParam(required = false) String status,
            @ApiParam("页码") @RequestParam(defaultValue = "0") int page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") int size) {
        return Result.success(equipmentService.getRepairRecords(equipmentId, status, page, size));
    }

    @ApiOperation("导出器材年度使用报告")
    @GetMapping("/export-annual-report")
    public void exportAnnualUsageReport(
            @ApiParam("年份") @RequestParam Integer year,
            HttpServletResponse response) throws IOException {
        equipmentService.exportAnnualUsageReport(year, response);
    }
}
