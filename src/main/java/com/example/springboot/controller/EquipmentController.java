package com.example.springboot.controller;

import com.example.springboot.entity.Equipment;
import com.example.springboot.entity.EquipmentBorrow;
import com.example.springboot.entity.EquipmentRepair;
import com.example.springboot.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipment")
@CrossOrigin(origins = "*")
public class EquipmentController {
    
    @Autowired
    private EquipmentService equipmentService;
    
    @PostMapping("/borrow")
    public ResponseEntity<?> borrowEquipment(
            @RequestParam Long studentId,
            @RequestParam Long equipmentId,
            @RequestParam Integer count,
            @RequestParam(defaultValue = "7") int borrowDays) {
        try {
            EquipmentBorrow borrow = equipmentService.borrowEquipment(studentId, equipmentId, count, borrowDays);
            return ResponseEntity.ok(borrow);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/return/{borrowId}")
    public ResponseEntity<String> returnEquipment(@PathVariable Long borrowId) {
        equipmentService.returnEquipment(borrowId);
        return ResponseEntity.ok("归还成功");
    }
    
    @GetMapping("/overdue")
    public ResponseEntity<List<EquipmentBorrow>> getOverdueBorrows() {
        return ResponseEntity.ok(equipmentService.getOverdueBorrows());
    }
    
    @PostMapping("/repair")
    public ResponseEntity<EquipmentRepair> reportRepair(
            @RequestParam Long equipmentId,
            @RequestParam Long studentId,
            @RequestParam String problem) {
        EquipmentRepair repair = equipmentService.reportRepair(equipmentId, studentId, problem);
        if (repair != null) {
            return ResponseEntity.ok(repair);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/repair/{repairId}/complete")
    public ResponseEntity<String> completeRepair(
            @PathVariable Long repairId,
            @RequestParam String notes) {
        equipmentService.completeRepair(repairId, notes);
        return ResponseEntity.ok("维修完成");
    }
    
    @GetMapping("/low-stock")
    public ResponseEntity<List<Equipment>> getLowStockEquipment() {
        return ResponseEntity.ok(equipmentService.getLowStockEquipment());
    }
    
    @GetMapping("/report/annual")
    public ResponseEntity<Map<String, Object>> getAnnualReport(
            @RequestParam(defaultValue = "2024") int year) {
        return ResponseEntity.ok(equipmentService.getAnnualUsageReport(year));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Equipment>> searchEquipment(@RequestParam String keyword) {
        return ResponseEntity.ok(equipmentService.searchEquipment(keyword));
    }
    
    @PostMapping
    public ResponseEntity<Equipment> addEquipment(@RequestBody Equipment equipment) {
        return ResponseEntity.ok(equipmentService.addEquipment(equipment));
    }
    
    @GetMapping
    public ResponseEntity<List<Equipment>> getAllEquipment() {
        return ResponseEntity.ok(equipmentService.searchEquipment(""));
    }
}
