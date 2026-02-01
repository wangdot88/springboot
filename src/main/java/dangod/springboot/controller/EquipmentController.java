package dangod.springboot.controller;

import dangod.springboot.entity.Equipment;
import dangod.springboot.entity.EquipmentBorrow;
import dangod.springboot.entity.EquipmentRepair;
import dangod.springboot.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {
    
    @Autowired
    private EquipmentService equipmentService;
    
    @PostMapping("/add")
    public ResponseEntity<?> addEquipment(@RequestParam String equipmentCode, 
                                         @RequestParam String name, 
                                         @RequestParam String category, 
                                         @RequestParam String brand, 
                                         @RequestParam String model, 
                                         @RequestParam Integer totalQuantity, 
                                         @RequestParam Integer minStockAlert, 
                                         @RequestParam(required = false) String description) {
        try {
            Equipment equipment = equipmentService.addEquipment(equipmentCode, name, category, brand, model, totalQuantity, minStockAlert, description);
            return ResponseEntity.ok(equipment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("添加器材失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/borrow")
    public ResponseEntity<?> borrowEquipment(@RequestParam String studentId, 
                                           @RequestParam String equipmentCode, 
                                           @RequestParam Integer quantity, 
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime expectedReturnDate, 
                                           @RequestParam(required = false) String remarks) {
        try {
            EquipmentBorrow borrow = equipmentService.borrowEquipment(studentId, equipmentCode, quantity, expectedReturnDate, remarks);
            return ResponseEntity.ok(borrow);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("借用器材失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/return")
    public ResponseEntity<?> returnEquipment(@RequestParam Long borrowId, 
                                           @RequestParam(required = false) String remarks) {
        try {
            EquipmentBorrow borrow = equipmentService.returnEquipment(borrowId, remarks);
            return ResponseEntity.ok(borrow);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("归还器材失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/overdue")
    public ResponseEntity<?> getOverdueBorrows() {
        try {
            List<EquipmentBorrow> overdueBorrows = equipmentService.getOverdueBorrows();
            return ResponseEntity.ok(overdueBorrows);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取逾期借用记录失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/low-stock")
    public ResponseEntity<?> getLowStockEquipment() {
        try {
            List<Equipment> lowStockEquipment = equipmentService.getLowStockEquipment();
            return ResponseEntity.ok(lowStockEquipment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取库存不足器材失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/report-repair")
    public ResponseEntity<?> reportRepair(@RequestParam String equipmentCode, 
                                         @RequestParam(required = false) String studentId, 
                                         @RequestParam Integer quantity, 
                                         @RequestParam String repairReason, 
                                         @RequestParam(required = false) String repairDescription) {
        try {
            EquipmentRepair repair = equipmentService.reportRepair(equipmentCode, studentId, quantity, repairReason, repairDescription);
            return ResponseEntity.ok(repair);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("报修失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/start-repair")
    public ResponseEntity<?> startRepair(@RequestParam Long repairId) {
        try {
            EquipmentRepair repair = equipmentService.startRepair(repairId);
            return ResponseEntity.ok(repair);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("开始维修失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/complete-repair")
    public ResponseEntity<?> completeRepair(@RequestParam Long repairId, 
                                           @RequestParam(required = false) Double repairCost) {
        try {
            EquipmentRepair repair = equipmentService.completeRepair(repairId, repairCost);
            return ResponseEntity.ok(repair);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("完成维修失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/current-borrows/{studentId}")
    public ResponseEntity<?> getCurrentBorrowsByStudent(@PathVariable String studentId) {
        try {
            List<EquipmentBorrow> borrows = equipmentService.getCurrentBorrowsByStudent(studentId);
            return ResponseEntity.ok(borrows);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取当前借用记录失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/borrow-history/{studentId}")
    public ResponseEntity<?> getBorrowHistoryByStudent(@PathVariable String studentId) {
        try {
            List<EquipmentBorrow> borrows = equipmentService.getBorrowHistoryByStudent(studentId);
            return ResponseEntity.ok(borrows);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取借用历史失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/pending-repairs")
    public ResponseEntity<?> getPendingRepairs() {
        try {
            List<EquipmentRepair> repairs = equipmentService.getPendingRepairs();
            return ResponseEntity.ok(repairs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取待维修记录失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllEquipment() {
        try {
            List<Equipment> equipment = equipmentService.getAllEquipment();
            return ResponseEntity.ok(equipment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取所有器材失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{equipmentCode}")
    public ResponseEntity<?> getEquipmentByCode(@PathVariable String equipmentCode) {
        try {
            Optional<Equipment> equipment = equipmentService.getEquipmentByCode(equipmentCode);
            if (equipment.isPresent()) {
                return ResponseEntity.ok(equipment.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取器材失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/update")
    public ResponseEntity<?> updateEquipment(@RequestParam String equipmentCode, 
                                            @RequestParam String name, 
                                            @RequestParam String category, 
                                            @RequestParam String brand, 
                                            @RequestParam String model, 
                                            @RequestParam Integer totalQuantity, 
                                            @RequestParam Integer minStockAlert, 
                                            @RequestParam(required = false) String description) {
        try {
            Equipment equipment = equipmentService.updateEquipment(equipmentCode, name, category, brand, model, totalQuantity, minStockAlert, description);
            return ResponseEntity.ok(equipment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("更新器材失败: " + e.getMessage());
        }
    }
}