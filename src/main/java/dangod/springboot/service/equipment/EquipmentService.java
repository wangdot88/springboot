package dangod.springboot.service.equipment;

import dangod.springboot.core.common.BusinessException;
import dangod.springboot.entity.Student;
import dangod.springboot.entity.equipment.Equipment;
import dangod.springboot.entity.equipment.EquipmentAnnualReport;
import dangod.springboot.entity.equipment.EquipmentBorrow;
import dangod.springboot.entity.equipment.EquipmentRepair;
import dangod.springboot.repository.StudentRepository;
import dangod.springboot.repository.equipment.EquipmentAnnualReportRepository;
import dangod.springboot.repository.equipment.EquipmentBorrowRepository;
import dangod.springboot.repository.equipment.EquipmentRepairRepository;
import dangod.springboot.repository.equipment.EquipmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
public class EquipmentService {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private EquipmentBorrowRepository borrowRepository;

    @Autowired
    private EquipmentRepairRepository repairRepository;

    @Autowired
    private EquipmentAnnualReportRepository annualReportRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Transactional
    public Equipment addEquipment(Equipment equipment) {
        if (equipmentRepository.findByCode(equipment.getCode()).isPresent()) {
            throw new BusinessException("器材编号已存在");
        }
        equipment.setAvailableQuantity(equipment.getTotalQuantity());
        equipment.setBorrowedQuantity(0);
        equipment.setRepairQuantity(0);
        equipment.setStatus("正常");
        return equipmentRepository.save(equipment);
    }

    @Transactional
    public Equipment updateEquipment(Long id, Equipment equipment) {
        Equipment existing = equipmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("器材不存在"));

        int diff = equipment.getTotalQuantity() - existing.getTotalQuantity();
        existing.setAvailableQuantity(existing.getAvailableQuantity() + diff);

        existing.setName(equipment.getName());
        existing.setCategory(equipment.getCategory());
        existing.setBrand(equipment.getBrand());
        existing.setModel(equipment.getModel());
        existing.setTotalQuantity(equipment.getTotalQuantity());
        existing.setMinStock(equipment.getMinStock());
        existing.setLocation(equipment.getLocation());
        existing.setDescription(equipment.getDescription());

        updateEquipmentStatus(existing);
        return equipmentRepository.save(existing);
    }

    private void updateEquipmentStatus(Equipment equipment) {
        if (equipment.getAvailableQuantity() <= 0) {
            equipment.setStatus("已借出");
        } else if (equipment.getAvailableQuantity() < equipment.getMinStock()) {
            equipment.setStatus("库存不足");
        } else {
            equipment.setStatus("正常");
        }
    }

    @Transactional
    public EquipmentBorrow borrowEquipment(Long equipmentId, Long studentId, Integer quantity, LocalDate expectedReturnDate, String purpose) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new BusinessException("器材不存在"));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException("学生不存在"));

        if (equipment.getAvailableQuantity() < quantity) {
            throw new BusinessException("库存不足，当前可用数量：" + equipment.getAvailableQuantity());
        }

        EquipmentBorrow borrow = new EquipmentBorrow();
        borrow.setEquipment(equipment);
        borrow.setStudent(student);
        borrow.setQuantity(quantity);
        borrow.setBorrowDate(LocalDate.now());
        borrow.setExpectedReturnDate(expectedReturnDate);
        borrow.setBorrowPurpose(purpose);
        borrow.setStatus("已借出");
        borrow.setIsOverdue(false);

        equipment.setAvailableQuantity(equipment.getAvailableQuantity() - quantity);
        equipment.setBorrowedQuantity(equipment.getBorrowedQuantity() + quantity);
        updateEquipmentStatus(equipment);

        equipmentRepository.save(equipment);
        return borrowRepository.save(borrow);
    }

    @Transactional
    public EquipmentBorrow returnEquipment(Long borrowId) {
        EquipmentBorrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new BusinessException("借用记录不存在"));

        if ("已归还".equals(borrow.getStatus())) {
            throw new BusinessException("该器材已归还");
        }

        Equipment equipment = borrow.getEquipment();
        equipment.setAvailableQuantity(equipment.getAvailableQuantity() + borrow.getQuantity());
        equipment.setBorrowedQuantity(equipment.getBorrowedQuantity() - borrow.getQuantity());
        updateEquipmentStatus(equipment);

        borrow.setActualReturnDate(LocalDate.now());
        borrow.setStatus("已归还");
        borrow.setIsOverdue(LocalDate.now().isAfter(borrow.getExpectedReturnDate()));

        equipmentRepository.save(equipment);
        return borrowRepository.save(borrow);
    }

    @Transactional
    public EquipmentRepair reportRepair(Long equipmentId, Integer quantity, String description) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new BusinessException("器材不存在"));

        if (equipment.getAvailableQuantity() < quantity) {
            throw new BusinessException("可用数量不足");
        }

        EquipmentRepair repair = new EquipmentRepair();
        repair.setEquipment(equipment);
        repair.setQuantity(quantity);
        repair.setDescription(description);
        repair.setReportDate(LocalDate.now());
        repair.setStatus("待维修");

        equipment.setAvailableQuantity(equipment.getAvailableQuantity() - quantity);
        equipment.setRepairQuantity(equipment.getRepairQuantity() + quantity);
        updateEquipmentStatus(equipment);

        equipmentRepository.save(equipment);
        return repairRepository.save(repair);
    }

    @Transactional
    public EquipmentRepair completeRepair(Long repairId, Double repairCost, String repairMan) {
        EquipmentRepair repair = repairRepository.findById(repairId)
                .orElseThrow(() -> new BusinessException("维修记录不存在"));

        Equipment equipment = repair.getEquipment();
        equipment.setAvailableQuantity(equipment.getAvailableQuantity() + repair.getQuantity());
        equipment.setRepairQuantity(equipment.getRepairQuantity() - repair.getQuantity());
        updateEquipmentStatus(equipment);

        repair.setRepairDate(LocalDate.now());
        repair.setCompleteDate(LocalDate.now());
        repair.setRepairCost(repairCost);
        repair.setRepairMan(repairMan);
        repair.setStatus("已完成");

        equipmentRepository.save(equipment);
        return repairRepository.save(repair);
    }

    public List<Equipment> getLowStockEquipments() {
        return equipmentRepository.findLowStockEquipments();
    }

    public List<EquipmentBorrow> getOverdueBorrows() {
        List<EquipmentBorrow> borrows = borrowRepository.findOverdueBorrows(LocalDate.now());
        for (EquipmentBorrow borrow : borrows) {
            if (!borrow.getIsOverdue()) {
                borrow.setIsOverdue(true);
                borrowRepository.save(borrow);
            }
        }
        return borrows;
    }

    public List<EquipmentBorrow> getSoonOverdueBorrows(int days) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);
        return borrowRepository.findSoonOverdueBorrows(today, endDate);
    }

    public void sendOverdueReminder() {
        List<EquipmentBorrow> overdueBorrows = getOverdueBorrows();
        for (EquipmentBorrow borrow : overdueBorrows) {
            long overdueDays = ChronoUnit.DAYS.between(borrow.getExpectedReturnDate(), LocalDate.now());
            log.info("提醒：学生{}借用的{}已超期{}天，请尽快归还",
                    borrow.getStudent().getName(),
                    borrow.getEquipment().getName(),
                    overdueDays);
        }

        List<EquipmentBorrow> soonOverdue = getSoonOverdueBorrows(3);
        for (EquipmentBorrow borrow : soonOverdue) {
            long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), borrow.getExpectedReturnDate());
            log.info("提醒：学生{}借用的{}将在{}天后到期",
                    borrow.getStudent().getName(),
                    borrow.getEquipment().getName(),
                    daysLeft);
        }
    }

    @Transactional
    public void generateAnnualReport(Integer year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<Equipment> equipments = equipmentRepository.findAll();
        for (Equipment equipment : equipments) {
            List<EquipmentBorrow> borrows = borrowRepository.findByDateRange(startDate, endDate);

            int totalBorrowCount = 0;
            int totalBorrowDays = 0;
            for (EquipmentBorrow borrow : borrows) {
                if (borrow.getEquipment().getId().equals(equipment.getId())) {
                    totalBorrowCount++;
                    if (borrow.getActualReturnDate() != null) {
                        totalBorrowDays += ChronoUnit.DAYS.between(borrow.getBorrowDate(), borrow.getActualReturnDate());
                    }
                }
            }

            List<EquipmentRepair> repairs = repairRepository.findByDateRange(startDate, endDate);
            int repairCount = 0;
            double totalRepairCost = 0;
            for (EquipmentRepair repair : repairs) {
                if (repair.getEquipment().getId().equals(equipment.getId())) {
                    repairCount++;
                    if (repair.getRepairCost() != null) {
                        totalRepairCost += repair.getRepairCost();
                    }
                }
            }

            EquipmentAnnualReport report = annualReportRepository.findByEquipmentIdAndYear(equipment.getId(), year);
            if (report == null) {
                report = new EquipmentAnnualReport();
                report.setEquipment(equipment);
                report.setYear(year);
            }

            report.setTotalBorrowCount(totalBorrowCount);
            report.setTotalBorrowDays(totalBorrowDays);
            report.setRepairCount(repairCount);
            report.setRepairCost(totalRepairCost);
            report.setUtilizationRate(equipment.getTotalQuantity() > 0 ? (totalBorrowDays * 100.0 / (365 * equipment.getTotalQuantity())) : 0);

            annualReportRepository.save(report);
        }
    }

    public List<EquipmentAnnualReport> getAnnualReports(Integer year) {
        return annualReportRepository.findByYear(year);
    }

    public byte[] exportAnnualReport(Integer year) throws IOException {
        List<EquipmentAnnualReport> reports = getAnnualReports(year);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("器材年度报告-" + year);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"器材名称", "器材编号", "类别", "借用次数", "借用天数", "维修次数", "维修费用", "利用率(%)"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (EquipmentAnnualReport report : reports) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(report.getEquipment().getName());
                row.createCell(1).setCellValue(report.getEquipment().getCode());
                row.createCell(2).setCellValue(report.getEquipment().getCategory());
                row.createCell(3).setCellValue(report.getTotalBorrowCount());
                row.createCell(4).setCellValue(report.getTotalBorrowDays());
                row.createCell(5).setCellValue(report.getRepairCount());
                row.createCell(6).setCellValue(report.getRepairCost());
                row.createCell(7).setCellValue(String.format("%.2f", report.getUtilizationRate()));
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public List<Equipment> getAllEquipments() {
        return equipmentRepository.findAll();
    }

    public Equipment getEquipment(Long id) {
        return equipmentRepository.findById(id).orElse(null);
    }

    public List<EquipmentBorrow> getBorrowRecords(Long equipmentId, Long studentId) {
        if (equipmentId != null) {
            return borrowRepository.findByEquipmentId(equipmentId);
        } else if (studentId != null) {
            return borrowRepository.findByStudentId(studentId);
        }
        return borrowRepository.findAll();
    }

    public List<EquipmentRepair> getRepairRecords(Long equipmentId) {
        if (equipmentId != null) {
            return repairRepository.findByEquipmentId(equipmentId);
        }
        return repairRepository.findAll();
    }
}
