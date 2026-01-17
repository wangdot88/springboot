package dangod.springboot.service;

import dangod.springboot.model.dto.EquipmentBorrowDTO;
import dangod.springboot.model.dto.EquipmentRepairDTO;
import dangod.springboot.model.entity.Equipment;
import dangod.springboot.model.entity.EquipmentBorrow;
import dangod.springboot.model.entity.EquipmentRepair;
import dangod.springboot.model.entity.Student;
import dangod.springboot.repository.EquipmentBorrowRepository;
import dangod.springboot.repository.EquipmentRepository;
import dangod.springboot.repository.EquipmentRepairRepository;
import dangod.springboot.repository.StudentRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
    public EquipmentBorrow borrowEquipment(EquipmentBorrowDTO borrowDTO) {
        Long equipmentId = borrowDTO.getEquipmentId();
        Long studentId = borrowDTO.getStudentId();
        Integer borrowCount = borrowDTO.getBorrowQuantity();
        
        Equipment equipment = equipmentRepository.findOne(equipmentId);
        if (equipment == null) {
            throw new RuntimeException("器材不存在");
        }
        
        if (equipment.getAvailableCount() < borrowCount) {
            throw new RuntimeException("器材库存不足，当前可用：" + equipment.getAvailableCount());
        }
        
        Student student = studentRepository.findOne(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        
        EquipmentBorrow borrow = new EquipmentBorrow();
        borrow.setBorrowNo(generateBorrowNo());
        borrow.setEquipmentId(equipmentId);
        borrow.setStudentId(studentId);
        borrow.setBorrowCount(borrowCount);
        borrow.setBorrowDate(new Date());
        
        if (borrowDTO.getExpectedReturnDate() != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                borrow.setExpectedReturnDate(sdf.parse(borrowDTO.getExpectedReturnDate()));
            } catch (ParseException e) {
                throw new RuntimeException("日期格式错误");
            }
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            borrow.setExpectedReturnDate(calendar.getTime());
        }
        borrow.setStatus("BORROWED");
        
        equipment.setAvailableCount(equipment.getAvailableCount() - borrowCount);
        equipment.setBorrowedCount(equipment.getBorrowedCount() + borrowCount);
        updateEquipmentStatus(equipment);
        
        equipmentRepository.save(equipment);
        return equipmentBorrowRepository.save(borrow);
    }
    
    @Transactional
    public EquipmentBorrow returnEquipment(Long borrowId, int returnQuantity) {
        EquipmentBorrow borrow = equipmentBorrowRepository.findOne(borrowId);
        if (borrow == null) {
            throw new RuntimeException("借据不存在");
        }
        
        if ("RETURNED".equals(borrow.getStatus())) {
            throw new RuntimeException("器材已归还");
        }
        
        borrow.setActualReturnDate(new Date());
        borrow.setStatus("RETURNED");
        
        Equipment equipment = equipmentRepository.findOne(borrow.getEquipmentId());
        if (equipment != null) {
            equipment.setAvailableCount(equipment.getAvailableCount() + returnQuantity);
            equipment.setBorrowedCount(equipment.getBorrowedCount() - returnQuantity);
            updateEquipmentStatus(equipment);
            equipmentRepository.save(equipment);
        }
        
        return equipmentBorrowRepository.save(borrow);
    }
    
    @Transactional
    public EquipmentRepair reportRepair(EquipmentRepairDTO repairDTO) {
        Long equipmentId = repairDTO.getEquipmentId();
        String reporterName = repairDTO.getReporterName();
        String reporterPhone = repairDTO.getReporterPhone();
        Integer repairCount = repairDTO.getRepairQuantity();
        String problemDescription = repairDTO.getProblemDescription();
        
        Equipment equipment = equipmentRepository.findOne(equipmentId);
        if (equipment == null) {
            throw new RuntimeException("器材不存在");
        }
        if (equipment.getBrokenCount() + repairCount > equipment.getTotalCount()) {
            throw new RuntimeException("报修数量超过器材总数");
        }
        
        EquipmentRepair repair = new EquipmentRepair();
        repair.setRepairNo(generateRepairNo());
        repair.setEquipmentId(equipmentId);
        repair.setReporterId(null);
        repair.setReporterName(reporterName);
        repair.setRepairCount(repairCount);
        repair.setProblemDescription(problemDescription);
        repair.setStatus("PENDING");
        
        equipment.setBrokenCount(equipment.getBrokenCount() + repairCount);
        equipment.setAvailableCount(equipment.getAvailableCount() - repairCount);
        updateEquipmentStatus(equipment);
        
        equipmentRepository.save(equipment);
        return equipmentRepairRepository.save(repair);
    }
    
    @Transactional
    public EquipmentRepair updateRepairStatus(Long repairId, String status, String repairResult, Double repairCost) {
        EquipmentRepair repair = equipmentRepairRepository.findOne(repairId);
        if (repair == null) {
            throw new RuntimeException("报修单不存在");
        }
        repair.setStatus(status);
        repair.setRepairResult(repairResult);
        if (repairCost != null) {
            repair.setRepairCost(new java.math.BigDecimal(repairCost));
        }
        
        if ("COMPLETED".equals(status)) {
            Equipment equipment = equipmentRepository.findOne(repair.getEquipmentId());
            if (equipment != null) {
                equipment.setBrokenCount(equipment.getBrokenCount() - repair.getRepairCount());
                equipment.setAvailableCount(equipment.getAvailableCount() + repair.getRepairCount());
                updateEquipmentStatus(equipment);
                equipmentRepository.save(equipment);
            }
        }
        
        return equipmentRepairRepository.save(repair);
    }
    
    public List<EquipmentBorrow> getOverdueBorrows() {
        List<EquipmentBorrow> overdueList = equipmentBorrowRepository.findOverdueBorrows(new Date());
        overdueList.forEach(eb -> {
            eb.setStatus("OVERDUE");
            equipmentBorrowRepository.save(eb);
            
            Equipment equipment = equipmentRepository.findOne(eb.getEquipmentId());
            if (equipment != null) {
                eb.setEquipmentName(equipment.getEquipmentName());
            }
            Student student = studentRepository.findOne(eb.getStudentId());
            if (student != null) {
                eb.setStudentName(student.getName());
                eb.setStudentNo(student.getStudentNo());
            }
        });
        return overdueList;
    }
    
    public List<Equipment> getLowStockAlert() {
        return equipmentRepository.findLowStockEquipment();
    }
    
    public List<Equipment> getOutOfStockEquipment() {
        return equipmentRepository.findOutOfStockEquipment();
    }
    
    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }
    
    public Equipment getEquipmentById(Long id) {
        return equipmentRepository.findOne(id);
    }
    
    public List<Equipment> getEquipmentList(String name, String category, int page, int size) {
        PageRequest pageRequest = new PageRequest(page, size);
        List<Equipment> equipments;
        
        if (name != null && category != null) {
            equipments = equipmentRepository.findByEquipmentNameContainingAndCategory(name, category);
        } else if (name != null) {
            equipments = equipmentRepository.findByEquipmentNameContaining(name);
        } else if (category != null) {
            equipments = equipmentRepository.findByCategory(category);
        } else {
            equipments = equipmentRepository.findAll();
        }
        
        int start = Math.min(page * size, equipments.size());
        int end = Math.min((page + 1) * size, equipments.size());
        return equipments.subList(start, end);
    }
    
    public List<EquipmentBorrow> getBorrowHistoryByStudent(Long studentId) {
        List<EquipmentBorrow> borrows = equipmentBorrowRepository.findByStudentId(studentId);
        borrows.forEach(eb -> {
            Equipment equipment = equipmentRepository.findOne(eb.getEquipmentId());
            if (equipment != null) {
                eb.setEquipmentName(equipment.getEquipmentName());
            }
        });
        return borrows;
    }
    
    public List<EquipmentBorrow> getActiveBorrows() {
        return equipmentBorrowRepository.findByStatus("BORROWED");
    }
    
    public List<EquipmentRepair> getRepairListByStatus(String status) {
        return equipmentRepairRepository.findByStatus(status);
    }
    
    public List<EquipmentRepair> getAllRepairs() {
        return equipmentRepairRepository.findAll();
    }
    
    @Transactional
    public Equipment addEquipment(Equipment equipment) {
        equipment.setEquipmentNo(generateEquipmentNo());
        equipment.setAvailableCount(equipment.getTotalCount());
        equipment.setBorrowedCount(0);
        equipment.setBrokenCount(0);
        equipment.setStatus("NORMAL");
        return equipmentRepository.save(equipment);
    }
    
    @Transactional
    public Equipment updateEquipment(Equipment equipment) {
        Equipment existing = equipmentRepository.findOne(equipment.getId());
        if (existing == null) {
            throw new RuntimeException("器材不存在");
        }
        
        Equipment update = existing;
        update.setEquipmentName(equipment.getEquipmentName());
        update.setCategory(equipment.getCategory());
        update.setBrand(equipment.getBrand());
        update.setModel(equipment.getModel());
        update.setTotalCount(equipment.getTotalCount());
        update.setAlertThreshold(equipment.getAlertThreshold());
        update.setWarrantyPeriod(equipment.getWarrantyPeriod());
        
        int newAvailable = equipment.getTotalCount() - update.getBorrowedCount() - update.getBrokenCount();
        update.setAvailableCount(Math.max(0, newAvailable));
        updateEquipmentStatus(update);
        
        return equipmentRepository.save(update);
    }
    
    private void updateEquipmentStatus(Equipment equipment) {
        if (equipment.getAvailableCount() == 0) {
            equipment.setStatus("OUT_OF_STOCK");
        } else if (equipment.getAvailableCount() < equipment.getAlertThreshold()) {
            equipment.setStatus("LOW_STOCK");
        } else if (equipment.getBrokenCount() > 0) {
            equipment.setStatus("DAMAGED");
        } else {
            equipment.setStatus("NORMAL");
        }
    }
    
    private String generateBorrowNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        String maxNo = equipmentBorrowRepository.findByBorrowDate(new Date()).stream()
                .map(EquipmentBorrow::getBorrowNo)
                .filter(no -> no.startsWith("BR" + dateStr))
                .max(String::compareTo)
                .orElse("BR" + dateStr + "0000");
        
        int seq = Integer.parseInt(maxNo.substring(maxNo.length() - 4)) + 1;
        return "BR" + dateStr + String.format("%04d", seq);
    }
    
    private String generateRepairNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        String maxNo = equipmentRepairRepository.findAll().stream()
                .map(EquipmentRepair::getRepairNo)
                .filter(no -> no.startsWith("RP" + dateStr))
                .max(String::compareTo)
                .orElse("RP" + dateStr + "0000");
        
        int seq = Integer.parseInt(maxNo.substring(maxNo.length() - 4)) + 1;
        return "RP" + dateStr + String.format("%04d", seq);
    }
    
    private String generateEquipmentNo() {
        String maxNo = equipmentRepository.findAll().stream()
                .map(Equipment::getEquipmentNo)
                .max(String::compareTo)
                .orElse("EQ0000");
        
        int seq = Integer.parseInt(maxNo.substring(2)) + 1;
        return "EQ" + String.format("%04d", seq);
    }
    
    public void exportAnnualUsageReport(Integer year, HttpServletResponse response) throws IOException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, 0, 1);
        Date startDate = calendar.getTime();
        calendar.set(year, 11, 31);
        Date endDate = calendar.getTime();
        
        List<EquipmentBorrow> borrows = equipmentBorrowRepository.findByDateRange(startDate, endDate);
        List<EquipmentRepair> repairs = equipmentRepairRepository.findAll();
        List<Equipment> equipments = equipmentRepository.findAll();
        
        Workbook workbook = new XSSFWorkbook();
        
        Sheet summarySheet = workbook.createSheet("年度汇总");
        createAnnualSummarySheet(summarySheet, equipments, borrows, repairs, year);
        
        Sheet borrowSheet = workbook.createSheet("借出记录");
        createAnnualBorrowSheet(borrowSheet, borrows);
        
        Sheet repairSheet = workbook.createSheet("维修记录");
        createAnnualRepairSheet(repairSheet, repairs, year);
        
        Sheet stockSheet = workbook.createSheet("库存现状");
        createStockSheet(stockSheet, equipments);
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=equipment-annual-report-" + year + ".xlsx");
        
        workbook.write(response.getOutputStream());
        workbook.close();
    }
    
    public List<EquipmentBorrow> getBorrowRecords(Long equipmentId, Long studentId, String status, int page, int size) {
        PageRequest pageRequest = new PageRequest(page, size);
        List<EquipmentBorrow> borrows;
        
        if (equipmentId != null && studentId != null && status != null) {
            borrows = equipmentBorrowRepository.findByEquipmentIdAndStudentIdAndStatus(equipmentId, studentId, status);
        } else if (equipmentId != null && studentId != null) {
            borrows = equipmentBorrowRepository.findByEquipmentIdAndStudentId(equipmentId, studentId);
        } else if (equipmentId != null && status != null) {
            borrows = equipmentBorrowRepository.findByEquipmentIdAndStatus(equipmentId, status);
        } else if (studentId != null && status != null) {
            borrows = equipmentBorrowRepository.findByStudentIdAndStatus(studentId, status);
        } else if (equipmentId != null) {
            borrows = equipmentBorrowRepository.findByEquipmentId(equipmentId);
        } else if (studentId != null) {
            borrows = equipmentBorrowRepository.findByStudentId(studentId);
        } else if (status != null) {
            borrows = equipmentBorrowRepository.findByStatus(status);
        } else {
            borrows = equipmentBorrowRepository.findAll();
        }
        
        int start = Math.min(page * size, borrows.size());
        int end = Math.min((page + 1) * size, borrows.size());
        return borrows.subList(start, end);
    }
    
    public List<EquipmentRepair> getRepairRecords(Long equipmentId, String status, int page, int size) {
        PageRequest pageRequest = new PageRequest(page, size);
        List<EquipmentRepair> repairs;
        
        if (equipmentId != null && status != null) {
            repairs = equipmentRepairRepository.findByEquipmentIdAndStatus(equipmentId, status);
        } else if (equipmentId != null) {
            repairs = equipmentRepairRepository.findByEquipmentId(equipmentId);
        } else if (status != null) {
            repairs = equipmentRepairRepository.findByStatus(status);
        } else {
            repairs = equipmentRepairRepository.findAll();
        }
        
        int start = Math.min(page * size, repairs.size());
        int end = Math.min((page + 1) * size, repairs.size());
        return repairs.subList(start, end);
    }
    
    private void createAnnualSummarySheet(Sheet sheet, List<Equipment> equipments, 
                                         List<EquipmentBorrow> borrows, List<EquipmentRepair> repairs, int year) {
        String[] headers = {"统计项目", "数量"};
        
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        
        int totalEquipment = equipments.size();
        int totalBorrows = borrows.size();
        int totalRepairs = (int) repairs.stream()
                .filter(r -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(r.getCreateTime());
                    return cal.get(Calendar.YEAR) == year;
                })
                .count();
        
        double totalRepairCost = repairs.stream()
                .filter(r -> r.getRepairCost() != null)
                .mapToDouble(r -> r.getRepairCost().doubleValue())
                .sum();
        
        int lowStockCount = (int) equipments.stream().filter(e -> "LOW_STOCK".equals(e.getStatus())).count();
        int outOfStockCount = (int) equipments.stream().filter(e -> "OUT_OF_STOCK".equals(e.getStatus())).count();
        
        String[][] data = {
            {"器材总数", String.valueOf(totalEquipment)},
            {"年度借出次数", String.valueOf(totalBorrows)},
            {"年度维修次数", String.valueOf(totalRepairs)},
            {"年度维修总费用", String.format("%.2f", totalRepairCost)},
            {"低库存预警数", String.valueOf(lowStockCount)},
            {"无库存数", String.valueOf(outOfStockCount)},
            {"正常库存数", String.valueOf(equipments.size() - lowStockCount - outOfStockCount)}
        };
        
        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(data[i][0]);
            row.createCell(1).setCellValue(data[i][1]);
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private void createAnnualBorrowSheet(Sheet sheet, List<EquipmentBorrow> borrows) {
        String[] headers = {"借据编号", "器材名称", "借出数量", "借出日期", "预计归还日期", "实际归还日期", "状态", "借用人"};
        
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int rowNum = 1;
        
        for (EquipmentBorrow borrow : borrows) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(borrow.getBorrowNo());
            
            Equipment equipment = equipmentRepository.findOne(borrow.getEquipmentId());
            if (equipment != null) {
                row.createCell(1).setCellValue(equipment.getEquipmentName());
            }
            
            row.createCell(2).setCellValue(borrow.getBorrowCount());
            row.createCell(3).setCellValue(sdf.format(borrow.getBorrowDate()));
            row.createCell(4).setCellValue(sdf.format(borrow.getExpectedReturnDate()));
            row.createCell(5).setCellValue(borrow.getActualReturnDate() != null ? sdf.format(borrow.getActualReturnDate()) : "");
            row.createCell(6).setCellValue(borrow.getStatus());
            
            Student student = studentRepository.findOne(borrow.getStudentId());
            if (student != null) {
                row.createCell(7).setCellValue(student.getName());
            }
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private void createAnnualRepairSheet(Sheet sheet, List<EquipmentRepair> repairs, int year) {
        String[] headers = {"报修编号", "器材名称", "报修数量", "问题描述", "状态", "维修结果", "维修费用", "报修时间"};
        
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int rowNum = 1;
        
        for (EquipmentRepair repair : repairs) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(repair.getCreateTime());
            if (cal.get(Calendar.YEAR) != year) continue;
            
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(repair.getRepairNo());
            
            Equipment equipment = equipmentRepository.findOne(repair.getEquipmentId());
            if (equipment != null) {
                row.createCell(1).setCellValue(equipment.getEquipmentName());
            }
            
            row.createCell(2).setCellValue(repair.getRepairCount());
            row.createCell(3).setCellValue(repair.getProblemDescription());
            row.createCell(4).setCellValue(repair.getStatus());
            row.createCell(5).setCellValue(repair.getRepairResult() != null ? repair.getRepairResult() : "");
            row.createCell(6).setCellValue(repair.getRepairCost() != null ? repair.getRepairCost().toString() : "");
            row.createCell(7).setCellValue(sdf.format(repair.getCreateTime()));
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private void createStockSheet(Sheet sheet, List<Equipment> equipments) {
        String[] headers = {"器材编号", "器材名称", "类别", "品牌", "总数量", "可用数量", "已借出", "已损坏", "状态"};
        
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        
        int rowNum = 1;
        for (Equipment equipment : equipments) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(equipment.getEquipmentNo());
            row.createCell(1).setCellValue(equipment.getEquipmentName());
            row.createCell(2).setCellValue(equipment.getCategory());
            row.createCell(3).setCellValue(equipment.getBrand());
            row.createCell(4).setCellValue(equipment.getTotalCount());
            row.createCell(5).setCellValue(equipment.getAvailableCount());
            row.createCell(6).setCellValue(equipment.getBorrowedCount());
            row.createCell(7).setCellValue(equipment.getBrokenCount());
            row.createCell(8).setCellValue(equipment.getStatus());
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
