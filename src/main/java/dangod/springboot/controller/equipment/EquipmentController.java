package dangod.springboot.controller.equipment;

import dangod.springboot.core.common.Result;
import dangod.springboot.entity.equipment.Equipment;
import dangod.springboot.entity.equipment.EquipmentAnnualReport;
import dangod.springboot.entity.equipment.EquipmentBorrow;
import dangod.springboot.entity.equipment.EquipmentRepair;
import dangod.springboot.service.equipment.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @PostMapping
    public Result<Equipment> addEquipment(@RequestBody Equipment equipment) {
        Equipment saved = equipmentService.addEquipment(equipment);
        return Result.success("添加成功", saved);
    }

    @PutMapping("/{id}")
    public Result<Equipment> updateEquipment(@PathVariable Long id, @RequestBody Equipment equipment) {
        Equipment updated = equipmentService.updateEquipment(id, equipment);
        return Result.success("更新成功", updated);
    }

    @GetMapping
    public Result<List<Equipment>> getAllEquipments() {
        return Result.success(equipmentService.getAllEquipments());
    }

    @GetMapping("/{id}")
    public Result<Equipment> getEquipment(@PathVariable Long id) {
        Equipment equipment = equipmentService.getEquipment(id);
        if (equipment == null) {
            return Result.error("器材不存在");
        }
        return Result.success(equipment);
    }

    @GetMapping("/low-stock")
    public Result<List<Equipment>> getLowStockEquipments() {
        return Result.success(equipmentService.getLowStockEquipments());
    }

    @PostMapping("/borrow")
    public Result<EquipmentBorrow> borrowEquipment(
            @RequestParam("equipmentId") Long equipmentId,
            @RequestParam("studentId") Long studentId,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("expectedReturnDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate expectedReturnDate,
            @RequestParam(value = "purpose", required = false) String purpose) {
        EquipmentBorrow borrow = equipmentService.borrowEquipment(equipmentId, studentId, quantity, expectedReturnDate, purpose);
        return Result.success("借用成功", borrow);
    }

    @PostMapping("/return/{borrowId}")
    public Result<EquipmentBorrow> returnEquipment(@PathVariable Long borrowId) {
        EquipmentBorrow borrow = equipmentService.returnEquipment(borrowId);
        return Result.success("归还成功", borrow);
    }

    @GetMapping("/borrow-records")
    public Result<List<EquipmentBorrow>> getBorrowRecords(
            @RequestParam(value = "equipmentId", required = false) Long equipmentId,
            @RequestParam(value = "studentId", required = false) Long studentId) {
        return Result.success(equipmentService.getBorrowRecords(equipmentId, studentId));
    }

    @GetMapping("/overdue")
    public Result<List<EquipmentBorrow>> getOverdueBorrows() {
        return Result.success(equipmentService.getOverdueBorrows());
    }

    @GetMapping("/soon-overdue")
    public Result<List<EquipmentBorrow>> getSoonOverdueBorrows(
            @RequestParam(value = "days", defaultValue = "3") int days) {
        return Result.success(equipmentService.getSoonOverdueBorrows(days));
    }

    @PostMapping("/send-reminder")
    public Result<Void> sendReminder() {
        equipmentService.sendOverdueReminder();
        return Result.success("提醒已发送", null);
    }

    @PostMapping("/repair")
    public Result<EquipmentRepair> reportRepair(
            @RequestParam("equipmentId") Long equipmentId,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("description") String description) {
        EquipmentRepair repair = equipmentService.reportRepair(equipmentId, quantity, description);
        return Result.success("报修成功", repair);
    }

    @PostMapping("/repair/complete/{repairId}")
    public Result<EquipmentRepair> completeRepair(
            @PathVariable Long repairId,
            @RequestParam(value = "repairCost", required = false) Double repairCost,
            @RequestParam(value = "repairMan", required = false) String repairMan) {
        EquipmentRepair repair = equipmentService.completeRepair(repairId, repairCost, repairMan);
        return Result.success("维修完成", repair);
    }

    @GetMapping("/repair-records")
    public Result<List<EquipmentRepair>> getRepairRecords(
            @RequestParam(value = "equipmentId", required = false) Long equipmentId) {
        return Result.success(equipmentService.getRepairRecords(equipmentId));
    }

    @PostMapping("/annual-report/generate")
    public Result<Void> generateAnnualReport(@RequestParam("year") Integer year) {
        equipmentService.generateAnnualReport(year);
        return Result.success("年度报告生成成功", null);
    }

    @GetMapping("/annual-report")
    public Result<List<EquipmentAnnualReport>> getAnnualReports(@RequestParam("year") Integer year) {
        return Result.success(equipmentService.getAnnualReports(year));
    }

    @GetMapping("/annual-report/export")
    public ResponseEntity<byte[]> exportAnnualReport(@RequestParam("year") Integer year) throws IOException {
        byte[] data = equipmentService.exportAnnualReport(year);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "equipment_annual_report_" + year + ".xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }
}
