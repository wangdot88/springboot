package dangod.springboot.controller;

import dangod.springboot.core.response.Response;
import dangod.springboot.dto.ClassReportDTO;
import dangod.springboot.entity.FitnessTest;
import dangod.springboot.entity.Student;
import dangod.springboot.service.FitnessTestService;
import dangod.springboot.util.ExcelUtil;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/fitness")
public class FitnessTestController {

    @Autowired
    private FitnessTestService fitnessTestService;

    @PostMapping("/import")
    public Response<String> importFitnessTests(@RequestParam("file") MultipartFile file) {
        try {
            List<dangod.springboot.dto.FitnessTestImportDTO> dtos = ExcelUtil.importFitnessTest(file);
            fitnessTestService.importFitnessTests(dtos);
            return Response.success("导入成功");
        } catch (Exception e) {
            return Response.error("导入失败：" + e.getMessage());
        }
    }

    @GetMapping("/list")
    public Response<List<FitnessTest>> getFitnessTests(@RequestParam Integer year, @RequestParam Integer semester) {
        List<FitnessTest> tests = fitnessTestService.getFitnessTestsByYearAndSemester(year, semester);
        return Response.success(tests);
    }

    @GetMapping("/class/{classId}")
    public Response<List<FitnessTest>> getFitnessTestsByClass(@PathVariable Long classId, 
                                                                @RequestParam Integer year, 
                                                                @RequestParam Integer semester) {
        List<FitnessTest> tests = fitnessTestService.getFitnessTestsByClassId(classId, year, semester);
        return Response.success(tests);
    }

    @GetMapping("/weak")
    public Response<List<Student>> getWeakStudents() {
        List<Student> students = fitnessTestService.getWeakStudents();
        return Response.success(students);
    }

    @GetMapping("/unqualified")
    public Response<List<FitnessTest>> getUnqualifiedStudents(@RequestParam Integer year, @RequestParam Integer semester) {
        List<FitnessTest> tests = fitnessTestService.getUnqualifiedStudents(year, semester);
        return Response.success(tests);
    }

    @GetMapping("/report/class")
    public Response<List<ClassReportDTO>> generateClassReport(@RequestParam Integer year, @RequestParam Integer semester) {
        List<ClassReportDTO> report = fitnessTestService.generateClassReport(year, semester);
        return Response.success(report);
    }

    @GetMapping("/report/class/export")
    public void exportClassReport(@RequestParam Integer year, @RequestParam Integer semester, 
                                   HttpServletResponse response) {
        try {
            List<ClassReportDTO> reports = fitnessTestService.generateClassReport(year, semester);
            
            Workbook workbook = ExcelUtil.createWorkbook();
            Sheet sheet = ExcelUtil.createSheet(workbook, "班级体测合格率报告");
            
            Row headerRow = ExcelUtil.createRow(sheet, 0);
            String[] headers = {"班级", "总人数", "测试人数", "合格人数", "合格率", "平均分", "A级", "B级", "C级", "D级"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = ExcelUtil.createCell(headerRow, i);
                ExcelUtil.setCellValue(cell, headers[i]);
            }
            
            for (int i = 0; i < reports.size(); i++) {
                ClassReportDTO report = reports.get(i);
                Row row = ExcelUtil.createRow(sheet, i + 1);
                
                ExcelUtil.setCellValue(ExcelUtil.createCell(row, 0), report.getClassName());
                ExcelUtil.setCellValue(ExcelUtil.createCell(row, 1), report.getTotalStudents());
                ExcelUtil.setCellValue(ExcelUtil.createCell(row, 2), report.getTestedStudents());
                ExcelUtil.setCellValue(ExcelUtil.createCell(row, 3), report.getQualifiedStudents());
                ExcelUtil.setCellValue(ExcelUtil.createCell(row, 4), report.getQualifiedRate());
                ExcelUtil.setCellValue(ExcelUtil.createCell(row, 5), report.getAverageScore());
                ExcelUtil.setCellValue(ExcelUtil.createCell(row, 6), report.getLevelA());
                ExcelUtil.setCellValue(ExcelUtil.createCell(row, 7), report.getLevelB());
                ExcelUtil.setCellValue(ExcelUtil.createCell(row, 8), report.getLevelC());
                ExcelUtil.setCellValue(ExcelUtil.createCell(row, 9), report.getLevelD());
            }
            
            ExcelUtil.autoSizeColumns(sheet);
            
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("班级体测合格率报告.xlsx", "UTF-8");
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
    public Response<FitnessTest> save(@RequestBody FitnessTest fitnessTest) {
        FitnessTest saved = fitnessTestService.save(fitnessTest);
        return Response.success(saved);
    }

    @DeleteMapping("/{id}")
    public Response<String> delete(@PathVariable Long id) {
        fitnessTestService.delete(id);
        return Response.success("删除成功");
    }
}
