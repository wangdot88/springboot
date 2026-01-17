package dangod.springboot.controller;

import dangod.springboot.core.response.Response;
import dangod.springboot.dto.EquipmentReportDTO;
import dangod.springboot.entity.*;
import dangod.springboot.service.EquipmentService;
import dangod.springboot.util.ExcelUtil;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @GetMapping("/list")
    public Response<List<Equipment>> getAllEquipment() {
        List<Equipment> equipment = equipmentService.getAllEquipment();
        return Response.success(equipment);
    }

    @GetMapping("/low-stock")
    public Response<List<Equipment>> getLowStockEquipment() {
        List<Equipment> equipment = equipmentService.getLowStockEquipment();
        return Response.success(equipment);
    }

    @PostMapping("/borrow")
    public Response<EquipmentBorrow> borrowEquipment(@RequestParam Long studentId,
                                                      @RequestParam Long equipmentId,
                                                      @RequestParam Integer borrowCount,
                                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date expectedReturnDate,
                                                      @RequestParam(required = false) String remarks) {
        EquipmentBorrow borrow = equipmentService.borrowEquipment(studentId, equipmentId, borrowCount, expectedReturnDate, remarks);
        if (borrow != null) {
            return Response.success(borrow);
        }
        return Response.error("借出失败，库存不足或学生/器材不存在");
    }

    @PostMapping("/return/{borrowId}")
    public Response<EquipmentBorrow> returnEquipment(@PathVariable Long borrowId) {
        EquipmentBorrow borrow = equipmentService.returnEquipment(borrowId);
        if (borrow != null) {
            return Response.success(borrow);
        }
        return Response.error("归还失败，借出记录不存在或已归还");
    }

    @GetMapping("/overdue")
    public Response<List<EquipmentBorrow>> getOverdueBorrows() {
        List<EquipmentBorrow> borrows = equipmentService.getOverdueBorrows();
        return Response.success(borrows);
    }

    @GetMapping("/student/{studentId}/active")
    public Response<List<EquipmentBorrow>> getActiveBorrowsByStudent(@PathVariable Long studentId) {
        List<EquipmentBorrow> borrows = equipmentService.getActiveBorrowsByStudent(studentId);
        return Response.success(borrows);
    }

    @GetMapping("/equipment/{equipmentId}/borrows")
    public Response<List<EquipmentBorrow>> getBorrowsByEquipment(@PathVariable Long equipmentId) {
        List<EquipmentBorrow> borrows = equipmentService.getBorrowsByEquipment(equipmentId);
        return Response.success(borrows);
    }

    @PostMapping("/repair")
    public Response<EquipmentRepair> reportRepair(@RequestParam Long equipmentId,
                                                   @RequestParam String repairReason,
                                                   @RequestParam(required = false) Integer repairCount,
                                                   @RequestParam(required = false) String reporter,
                                                   @RequestParam(required = false) String reporterPhone,
                                                   @RequestParam(required = false) String remarks) {
        EquipmentRepair repair = equipmentService.reportRepair(equipmentId, repairReason, repairCount, reporter, reporterPhone, remarks);
        if (repair != null) {
            return Response.success(repair);
        }
        return Response.error("报修失败，器材不存在");
    }

    @PostMapping("/repair/complete/{repairId}")
    public Response<EquipmentRepair> completeRepair(@PathVariable Long repairId,
                                                     @RequestParam String repairResult,
                                                     @RequestParam(required = false) Double repairCost) {
        EquipmentRepair repair = equipmentService.completeRepair(repairId, repairResult, repairCost);
        if (repair != null) {
            return Response.success(repair);
        }
        return Response.error("完成维修失败，维修记录不存在");
    }

    @GetMapping("/equipment/{equipmentId}/repairs")
    public Response<List<EquipmentRepair>> getRepairsByEquipment(@PathVariable Long equipmentId) {
        List<EquipmentRepair> repairs = equipmentService.getRepairsByEquipment(equipmentId);
        return Response.success(repairs);
    }

    @GetMapping("/repairs/pending")
    public Response<List<EquipmentRepair>> getPendingRepairs() {
        List<EquipmentRepair> repairs = equipmentService.getPendingRepairs();
        return Response.success(repairs);
    }

    @GetMapping("/report/annual")
    public Response<List<EquipmentReportDTO>> generateAnnualReport(@RequestParam Integer year) {
        List<EquipmentReportDTO> report = equipmentService.generateAnnualReport(year);
        return Response.success(report);
    }

    @GetMapping("/report/annual/export")
    public void exportAnnualReport(@RequestParam Integer year, HttpServletResponse response) {
        try {
            List<EquipmentReportDTO> reports = equipmentService.generateAnnualReport(year);
            
            Workbook workbook = ExcelUtil.createWorkbook();
            Sheet sheet = ExcelUtil.createSheet(workbook, "器材年度使用报告");
            
            Row headerRow = ExcelUtil.createRow(sheet, 0);
            String[] headers = {"器材编号", "器材名称", "类别", "总数", "借出次数", "维修次数", "利用率(%)"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = ExcelUtil.createCell(headerRow, i);
                ExcelUtil.setCellValue(cell, headers[i]);
            }
            
            for (int i = 0; i < reports.size(); i++) {
                EquipmentReportDTO report = reports.get(i);
                Row row = ExcelUtil.createRow(sheet, i + 1);
                
                ExcelUtil.setCellValue(ExcelUtil.createCell(row, 0), report.getEquipmentNo());
                ExcelUtil.setCellValue(ExcelUtil.createCell(row, 1), report.getName());
                ExcelUtil.setCellValue(ExcelUtil.createCell(row, 2), report.getCategory());
                ExcelUtil.setCellValue(ExcelUtil.createCell(row, 3), report.getTotalCount());
                ExcelUtil.setCellValue(ExcelUtil.createCell(row, 4), report.getBorrowCount());
                ExcelUtil.setCellValue(ExcelUtil.createCell(row, 5), report.getRepairCount());
                ExcelUtil.setCellValue(ExcelUtil.createCell(row, 6), report.getUtilizationRate());
            }
            
            ExcelUtil.autoSizeColumns(sheet);
            
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("器材年度使用报告_" + year + ".xlsx", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
            
            OutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/save")
    public Response<Equipment> save(@RequestBody Equipment equipment) {
        Equipment saved = equipmentService.save(equipment);
        return Response.success(saved);
    }

    @DeleteMapping("/{id}")
    public Response<String> deleteEquipment(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
        return Response.success("删除成功");
    }

    @DeleteMapping("/borrow/{id}")
    public Response<String> deleteBorrow(@PathVariable Long id) {
        equipmentService.deleteBorrow(id);
        return Response.success("删除成功");
    }

    @DeleteMapping("/repair/{id}")
    public Response<String> deleteRepair(@PathVariable Long id) {
        equipmentService.deleteRepair(id);
        return Response.success("删除成功");
    }
}
