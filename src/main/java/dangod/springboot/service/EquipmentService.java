package dangod.springboot.service;

import dangod.springboot.entity.Equipment;
import dangod.springboot.entity.EquipmentBorrow;
import dangod.springboot.entity.EquipmentRepair;
import dangod.springboot.entity.Student;
import dangod.springboot.repository.EquipmentBorrowRepository;
import dangod.springboot.repository.EquipmentRepository;
import dangod.springboot.repository.EquipmentRepairRepository;
import dangod.springboot.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    
    public Equipment addEquipment(String equipmentCode, String name, String category, String brand, String model, Integer totalQuantity, Integer minStockAlert, String description) {
        if (equipmentRepository.existsByEquipmentCode(equipmentCode)) {
            throw new RuntimeException("器材编码已存在");
        }
        
        Equipment equipment = new Equipment(equipmentCode, name, category, brand, model, totalQuantity, minStockAlert);
        equipment.setDescription(description);
        
        return equipmentRepository.save(equipment);
    }
    
    @Transactional
    public EquipmentBorrow borrowEquipment(String studentId, String equipmentCode, Integer quantity, LocalDateTime expectedReturnDate, String remarks) {
        Optional<Student> studentOpt = studentRepository.findByStudentId(studentId);
        if (!studentOpt.isPresent()) {
            throw new RuntimeException("学生不存在");
        }
        
        Optional<Equipment> equipmentOpt = equipmentRepository.findByEquipmentCode(equipmentCode);
        if (!equipmentOpt.isPresent()) {
            throw new RuntimeException("器材不存在");
        }
        
        Equipment equipment = equipmentOpt.get();
        
        if (equipment.getAvailableQuantity() < quantity) {
            throw new RuntimeException("器材库存不足");
        }
        
        EquipmentBorrow borrow = new EquipmentBorrow(studentOpt.get(), equipment, quantity, expectedReturnDate);
        borrow.setRemarks(remarks);
        
        equipment.setAvailableQuantity(equipment.getAvailableQuantity() - quantity);
        equipment.setBorrowedQuantity(equipment.getBorrowedQuantity() + quantity);
        
        equipmentRepository.save(equipment);
        return equipmentBorrowRepository.save(borrow);
    }
    
    @Transactional
    public EquipmentBorrow returnEquipment(Long borrowId, String remarks) {
        Optional<EquipmentBorrow> borrowOpt = equipmentBorrowRepository.findById(borrowId);
        if (!borrowOpt.isPresent()) {
            throw new RuntimeException("借用记录不存在");
        }
        
        EquipmentBorrow borrow = borrowOpt.get();
        if (!"BORROWED".equals(borrow.getStatus())) {
            throw new RuntimeException("器材已归还");
        }
        
        Equipment equipment = borrow.getEquipment();
        equipment.setAvailableQuantity(equipment.getAvailableQuantity() + borrow.getQuantity());
        equipment.setBorrowedQuantity(equipment.getBorrowedQuantity() - borrow.getQuantity());
        
        borrow.setStatus("RETURNED");
        borrow.setActualReturnDate(LocalDateTime.now());
        if (remarks != null && !remarks.isEmpty()) {
            borrow.setRemarks(borrow.getRemarks() + "; " + remarks);
        }
        
        equipmentRepository.save(equipment);
        return equipmentBorrowRepository.save(borrow);
    }
    
    public List<EquipmentBorrow> getOverdueBorrows() {
        return equipmentBorrowRepository.findOverdueBorrows(LocalDateTime.now());
    }
    
    public List<Equipment> getLowStockEquipment() {
        return equipmentRepository.findLowStockEquipment();
    }
    
    @Transactional
    public EquipmentRepair reportRepair(String equipmentCode, String studentId, Integer quantity, String repairReason, String repairDescription) {
        Optional<Equipment> equipmentOpt = equipmentRepository.findByEquipmentCode(equipmentCode);
        if (!equipmentOpt.isPresent()) {
            throw new RuntimeException("器材不存在");
        }
        
        Student student = null;
        if (studentId != null && !studentId.isEmpty()) {
            Optional<Student> studentOpt = studentRepository.findByStudentId(studentId);
            if (studentOpt.isPresent()) {
                student = studentOpt.get();
            }
        }
        
        Equipment equipment = equipmentOpt.get();
        
        if (equipment.getAvailableQuantity() < quantity) {
            throw new RuntimeException("可用器材数量不足");
        }
        
        EquipmentRepair repair = new EquipmentRepair(equipment, student, quantity, repairReason, repairDescription);
        
        equipment.setAvailableQuantity(equipment.getAvailableQuantity() - quantity);
        equipment.setRepairQuantity(equipment.getRepairQuantity() + quantity);
        
        equipmentRepository.save(equipment);
        return equipmentRepairRepository.save(repair);
    }
    
    @Transactional
    public EquipmentRepair startRepair(Long repairId) {
        Optional<EquipmentRepair> repairOpt = equipmentRepairRepository.findById(repairId);
        if (!repairOpt.isPresent()) {
            throw new RuntimeException("维修记录不存在");
        }
        
        EquipmentRepair repair = repairOpt.get();
        if (!"REPORTED".equals(repair.getStatus())) {
            throw new RuntimeException("维修状态不正确");
        }
        
        repair.setStatus("IN_PROGRESS");
        repair.setRepairDate(LocalDateTime.now());
        
        return equipmentRepairRepository.save(repair);
    }
    
    @Transactional
    public EquipmentRepair completeRepair(Long repairId, Double repairCost) {
        Optional<EquipmentRepair> repairOpt = equipmentRepairRepository.findById(repairId);
        if (!repairOpt.isPresent()) {
            throw new RuntimeException("维修记录不存在");
        }
        
        EquipmentRepair repair = repairOpt.get();
        if (!"IN_PROGRESS".equals(repair.getStatus())) {
            throw new RuntimeException("维修状态不正确");
        }
        
        Equipment equipment = repair.getEquipment();
        equipment.setAvailableQuantity(equipment.getAvailableQuantity() + repair.getQuantity());
        equipment.setRepairQuantity(equipment.getRepairQuantity() - repair.getQuantity());
        
        repair.setStatus("COMPLETED");
        repair.setCompletedDate(LocalDateTime.now());
        repair.setRepairCost(repairCost);
        
        equipmentRepository.save(equipment);
        return equipmentRepairRepository.save(repair);
    }
    
    public List<EquipmentBorrow> getCurrentBorrowsByStudent(String studentId) {
        return equipmentBorrowRepository.findCurrentBorrowsByStudent(studentId);
    }
    
    public List<EquipmentBorrow> getBorrowHistoryByStudent(String studentId) {
        Optional<Student> studentOpt = studentRepository.findByStudentId(studentId);
        if (!studentOpt.isPresent()) {
            throw new RuntimeException("学生不存在");
        }
        
        return equipmentBorrowRepository.findByStudent(studentOpt.get());
    }
    
    public List<EquipmentRepair> getPendingRepairs() {
        return equipmentRepairRepository.findPendingRepairs();
    }
    
    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }
    
    public Optional<Equipment> getEquipmentByCode(String equipmentCode) {
        return equipmentRepository.findByEquipmentCode(equipmentCode);
    }
    
    public Equipment updateEquipment(String equipmentCode, String name, String category, String brand, String model, Integer totalQuantity, Integer minStockAlert, String description) {
        Optional<Equipment> equipmentOpt = equipmentRepository.findByEquipmentCode(equipmentCode);
        if (!equipmentOpt.isPresent()) {
            throw new RuntimeException("器材不存在");
        }
        
        Equipment equipment = equipmentOpt.get();
        equipment.setName(name);
        equipment.setCategory(category);
        equipment.setBrand(brand);
        equipment.setModel(model);
        equipment.setTotalQuantity(totalQuantity);
        equipment.setMinStockAlert(minStockAlert);
        equipment.setDescription(description);
        
        return equipmentRepository.save(equipment);
    }
}