package dangod.springboot.service;

import dangod.springboot.dto.EquipmentReportDTO;
import dangod.springboot.entity.*;
import dangod.springboot.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Transactional
    public EquipmentBorrow borrowEquipment(Long studentId, Long equipmentId, Integer borrowCount, 
                                            Date expectedReturnDate, String remarks) {
        Student student = studentRepository.findOne(studentId);
        Equipment equipment = equipmentRepository.findOne(equipmentId);
        
        if (student == null || equipment == null) {
            return null;
        }
        
        if (equipment.getAvailableCount() < borrowCount) {
            return null;
        }
        
        EquipmentBorrow borrow = new EquipmentBorrow();
        borrow.setStudent(student);
        borrow.setEquipment(equipment);
        borrow.setBorrowCount(borrowCount);
        borrow.setBorrowDate(new Date());
        borrow.setExpectedReturnDate(expectedReturnDate);
        borrow.setStatus("BORROWED");
        borrow.setRemarks(remarks);
        borrow.setCreateTime(new Date());
        borrow.setUpdateTime(new Date());
        
        equipment.setAvailableCount(equipment.getAvailableCount() - borrowCount);
        equipmentRepository.save(equipment);
        
        return equipmentBorrowRepository.save(borrow);
    }

    @Transactional
    public EquipmentBorrow returnEquipment(Long borrowId) {
        EquipmentBorrow borrow = equipmentBorrowRepository.findOne(borrowId);
        if (borrow == null || !"BORROWED".equals(borrow.getStatus())) {
            return null;
        }
        
        borrow.setStatus("RETURNED");
        borrow.setReturnDate(new Date());
        borrow.setUpdateTime(new Date());
        
        Equipment equipment = borrow.getEquipment();
        equipment.setAvailableCount(equipment.getAvailableCount() + borrow.getBorrowCount());
        equipmentRepository.save(equipment);
        
        return equipmentBorrowRepository.save(borrow);
    }

    @Transactional
    public EquipmentRepair reportRepair(Long equipmentId, String repairReason, Integer repairCount, 
                                       String reporter, String reporterPhone, String remarks) {
        Equipment equipment = equipmentRepository.findOne(equipmentId);
        if (equipment == null) {
            return null;
        }
        
        EquipmentRepair repair = new EquipmentRepair();
        repair.setEquipment(equipment);
        repair.setRepairReason(repairReason);
        repair.setRepairCount(repairCount);
        repair.setReporter(reporter);
        repair.setReporterPhone(reporterPhone);
        repair.setStatus("PENDING");
        repair.setReportDate(new Date());
        repair.setRemarks(remarks);
        repair.setCreateTime(new Date());
        repair.setUpdateTime(new Date());
        
        equipment.setStatus("REPAIRING");
        equipmentRepository.save(equipment);
        
        return equipmentRepairRepository.save(repair);
    }

    @Transactional
    public EquipmentRepair completeRepair(Long repairId, String repairResult, Double repairCost) {
        EquipmentRepair repair = equipmentRepairRepository.findOne(repairId);
        if (repair == null) {
            return null;
        }
        
        repair.setStatus("COMPLETED");
        repair.setRepairResult(repairResult);
        repair.setRepairCost(repairCost);
        repair.setCompleteDate(new Date());
        repair.setUpdateTime(new Date());
        
        Equipment equipment = repair.getEquipment();
        equipment.setStatus("AVAILABLE");
        equipmentRepository.save(equipment);
        
        return equipmentRepairRepository.save(repair);
    }

    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    public List<Equipment> getLowStockEquipment() {
        return equipmentRepository.findLowStockEquipment();
    }

    public List<EquipmentBorrow> getOverdueBorrows() {
        return equipmentBorrowRepository.findOverdueBorrows(new Date());
    }

    public List<EquipmentBorrow> getActiveBorrowsByStudent(Long studentId) {
        return equipmentBorrowRepository.findActiveBorrowsByStudentId(studentId);
    }

    public List<EquipmentBorrow> getBorrowsByEquipment(Long equipmentId) {
        return equipmentBorrowRepository.findByEquipmentId(equipmentId);
    }

    public List<EquipmentRepair> getRepairsByEquipment(Long equipmentId) {
        return equipmentRepairRepository.findByEquipmentId(equipmentId);
    }

    public List<EquipmentRepair> getPendingRepairs() {
        return equipmentRepairRepository.findByStatus("PENDING");
    }

    public List<EquipmentReportDTO> generateAnnualReport(Integer year) {
        Date startDate = new Date(year - 1900, 0, 1);
        Date endDate = new Date(year - 1900, 11, 31);
        
        List<Equipment> allEquipment = equipmentRepository.findAll();
        List<EquipmentReportDTO> reports = new ArrayList<>();
        
        for (Equipment equipment : allEquipment) {
            EquipmentReportDTO report = new EquipmentReportDTO();
            report.setEquipmentId(equipment.getId());
            report.setEquipmentNo(equipment.getEquipmentNo());
            report.setName(equipment.getName());
            report.setCategory(equipment.getCategory());
            report.setTotalCount(equipment.getTotalCount());
            
            Long borrowCount = equipmentBorrowRepository.countBorrowsByDateRange(startDate, endDate);
            report.setBorrowCount(borrowCount.intValue());
            
            Long repairCount = equipmentRepairRepository.countRepairsByDateRange(startDate, endDate);
            report.setRepairCount(repairCount.intValue());
            
            if (equipment.getTotalCount() > 0) {
                Double utilizationRate = (borrowCount * 100.0) / equipment.getTotalCount();
                report.setUtilizationRate(utilizationRate);
            } else {
                report.setUtilizationRate(0.0);
            }
            
            reports.add(report);
        }
        
        return reports;
    }

    public Equipment save(Equipment equipment) {
        if (equipment.getId() == null) {
            equipment.setCreateTime(new Date());
            equipment.setAvailableCount(equipment.getTotalCount());
            equipment.setStatus("AVAILABLE");
        }
        equipment.setUpdateTime(new Date());
        return equipmentRepository.save(equipment);
    }

    public void deleteEquipment(Long id) {
        equipmentRepository.delete(id);
    }

    public void deleteBorrow(Long id) {
        equipmentBorrowRepository.delete(id);
    }

    public void deleteRepair(Long id) {
        equipmentRepairRepository.delete(id);
    }
}
