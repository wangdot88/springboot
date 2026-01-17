package com.example.springboot.service;

import com.example.springboot.entity.*;
import com.example.springboot.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class EquipmentService {
    
    @Autowired
    private EquipmentRepository equipmentRepository;
    
    @Autowired
    private EquipmentBorrowRepository equipmentBorrowRepository;
    
    @Autowired
    private EquipmentRepairRepository equipmentRepairRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    public EquipmentBorrow borrowEquipment(Long studentId, Long equipmentId, Integer count, int borrowDays) {
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        Optional<Equipment> equipmentOpt = equipmentRepository.findById(equipmentId);
        
        if (studentOpt.isPresent() && equipmentOpt.isPresent()) {
            Equipment equipment = equipmentOpt.get();
            
            if (equipment.getAvailableCount() < count) {
                throw new RuntimeException("库存不足");
            }
            
            EquipmentBorrow borrow = new EquipmentBorrow();
            borrow.setStudent(studentOpt.get());
            borrow.setEquipment(equipment);
            borrow.setBorrowCount(count);
            borrow.setBorrowTime(LocalDateTime.now());
            borrow.setExpectedReturnTime(LocalDateTime.now().plusDays(borrowDays));
            borrow.setIsReturned(false);
            
            equipment.setAvailableCount(equipment.getAvailableCount() - count);
            equipmentRepository.save(equipment);
            
            return equipmentBorrowRepository.save(borrow);
        }
        
        return null;
    }
    
    public void returnEquipment(Long borrowId) {
        Optional<EquipmentBorrow> borrowOpt = equipmentBorrowRepository.findById(borrowId);
        if (borrowOpt.isPresent()) {
            EquipmentBorrow borrow = borrowOpt.get();
            borrow.setIsReturned(true);
            borrow.setReturnTime(LocalDateTime.now());
            
            Equipment equipment = borrow.getEquipment();
            equipment.setAvailableCount(equipment.getAvailableCount() + borrow.getBorrowCount());
            
            equipmentBorrowRepository.save(borrow);
            equipmentRepository.save(equipment);
        }
    }
    
    public List<EquipmentBorrow> getOverdueBorrows() {
        return equipmentBorrowRepository.findOverdueBorrows(LocalDateTime.now());
    }
    
    public EquipmentRepair reportRepair(Long equipmentId, Long studentId, String problem) {
        Optional<Equipment> equipmentOpt = equipmentRepository.findById(equipmentId);
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        
        if (equipmentOpt.isPresent()) {
            EquipmentRepair repair = new EquipmentRepair();
            repair.setEquipment(equipmentOpt.get());
            repair.setStudent(studentOpt.orElse(null));
            repair.setProblemDescription(problem);
            repair.setRepairStatus("pending");
            repair.setSubmitTime(LocalDateTime.now());
            
            Equipment equipment = equipmentOpt.get();
            equipment.setStatus("repairing");
            equipment.setAvailableCount(equipment.getAvailableCount() - 1);
            equipmentRepository.save(equipment);
            
            return equipmentRepairRepository.save(repair);
        }
        
        return null;
    }
    
    public void completeRepair(Long repairId, String notes) {
        Optional<EquipmentRepair> repairOpt = equipmentRepairRepository.findById(repairId);
        if (repairOpt.isPresent()) {
            EquipmentRepair repair = repairOpt.get();
            repair.setRepairStatus("completed");
            repair.setRepairNotes(notes);
            repair.setCompleteTime(LocalDateTime.now());
            
            Equipment equipment = repair.getEquipment();
            equipment.setStatus("available");
            equipment.setAvailableCount(equipment.getAvailableCount() + 1);
            
            equipmentRepairRepository.save(repair);
            equipmentRepository.save(equipment);
        }
    }
    
    public List<Equipment> getLowStockEquipment() {
        return equipmentRepository.findLowStockEquipment();
    }
    
    public Map<String, Object> getAnnualUsageReport(int year) {
        List<EquipmentBorrow> allBorrows = equipmentBorrowRepository.findAll();
        
        Map<String, Object> report = new HashMap<>();
        Map<String, Integer> equipmentUsage = new HashMap<>();
        Map<String, Integer> monthlyBorrows = new HashMap<>();
        
        int totalBorrows = 0;
        int overdueCount = 0;
        
        for (EquipmentBorrow borrow : allBorrows) {
            if (borrow.getBorrowTime().getYear() == year) {
                totalBorrows++;
                
                String equipmentName = borrow.getEquipment().getName();
                equipmentUsage.put(equipmentName, equipmentUsage.getOrDefault(equipmentName, 0) + borrow.getBorrowCount());
                
                String month = borrow.getBorrowTime().getMonthValue() + "月";
                monthlyBorrows.put(month, monthlyBorrows.getOrDefault(month, 0) + 1);
                
                if (borrow.getIsOverdue() != null && borrow.getIsOverdue()) {
                    overdueCount++;
                }
            }
        }
        
        report.put("year", year);
        report.put("totalBorrows", totalBorrows);
        report.put("overdueCount", overdueCount);
        report.put("equipmentUsage", equipmentUsage);
        report.put("monthlyBorrows", monthlyBorrows);
        report.put("lowStockEquipment", getLowStockEquipment());
        
        return report;
    }
    
    public List<Equipment> searchEquipment(String keyword) {
        return equipmentRepository.findByNameContainingOrTypeContaining(keyword, keyword);
    }
    
    public Equipment addEquipment(Equipment equipment) {
        equipment.setAvailableCount(equipment.getTotalCount());
        if (equipment.getWarningThreshold() == null) {
            equipment.setWarningThreshold(equipment.getTotalCount() / 10);
        }
        if (equipment.getStatus() == null) {
            equipment.setStatus("available");
        }
        return equipmentRepository.save(equipment);
    }
}
