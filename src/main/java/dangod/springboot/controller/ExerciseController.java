package dangod.springboot.controller;

import dangod.springboot.core.response.Response;
import dangod.springboot.dto.ExerciseRankingDTO;
import dangod.springboot.entity.ExerciseRecord;
import dangod.springboot.entity.Student;
import dangod.springboot.service.ExerciseService;
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
@RequestMapping("/api/exercise")
public class ExerciseController {

    @Autowired
    private ExerciseService exerciseService;

    @PostMapping("/record")
    public Response<ExerciseRecord> recordExercise(@RequestParam Long studentId,
                                                     @RequestParam String exerciseType,
                                                     @RequestParam(required = false) Double distance,
                                                     @RequestParam(required = false) Integer duration,
                                                     @RequestParam(required = false) Double calories,
                                                     @RequestParam(required = false) String location) {
        ExerciseRecord record = exerciseService.recordExercise(studentId, exerciseType, distance, duration, calories, location);
        if (record != null) {
            return Response.success(record);
        }
        return Response.error("记录失败，学生不存在");
    }

    @GetMapping("/student/{studentId}")
    public Response<List<ExerciseRecord>> getStudentRecords(@PathVariable Long studentId) {
        List<ExerciseRecord> records = exerciseService.getExerciseRecordsByStudent(studentId);
        return Response.success(records);
    }

    @GetMapping("/date/{date}")
    public Response<List<ExerciseRecord>> getRecordsByDate(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        List<ExerciseRecord> records = exerciseService.getExerciseRecordsByDate(date);
        return Response.success(records);
    }

    @GetMapping("/class/{classId}/date/{date}")
    public Response<List<ExerciseRecord>> getClassRecordsByDate(@PathVariable Long classId,
                                                                  @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        List<ExerciseRecord> records = exerciseService.getExerciseRecordsByClassAndDate(classId, date);
        return Response.success(records);
    }

    @GetMapping("/ranking/daily")
    public Response<List<ExerciseRankingDTO>> getDailyRanking(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        List<ExerciseRankingDTO> rankings = exerciseService.getDailyRanking(date);
        return Response.success(rankings);
    }

    @GetMapping("/ranking/class/{classId}")
    public Response<List<ExerciseRankingDTO>> getClassDailyRanking(@PathVariable Long classId,
                                                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        List<ExerciseRankingDTO> rankings = exerciseService.getClassDailyRanking(classId, date);
        return Response.success(rankings);
    }

    @GetMapping("/ranking/period")
    public Response<List<ExerciseRankingDTO>> getPeriodRanking(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<ExerciseRankingDTO> rankings = exerciseService.getPeriodRanking(startDate, endDate);
        return Response.success(rankings);
    }

    @GetMapping("/inactive/{classId}")
    public Response<List<Student>> getInactiveStudents(@PathVariable Long classId,
                                                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        List<Student> students = exerciseService.getInactiveStudents(classId, date);
        return Response.success(students);
    }

    @GetMapping("/report/daily/export")
    public void exportDailyReport(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
                                   HttpServletResponse response) {
        try {
            List<ExerciseRankingDTO> rankings = exerciseService.getDailyRanking(date);
            
            Workbook workbook = ExcelUtil.createWorkbook();
            Sheet sheet = ExcelUtil.createSheet(workbook, "每日运动报告");
            
            Row headerRow = ExcelUtil.createRow(sheet, 0);
            String[] headers = {"排名", "学号", "姓名", "班级", "总距离(米)"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = ExcelUtil.createCell(headerRow, i);
                ExcelUtil.setCellValue(cell, headers[i]);
            }
            
            for (int i = 0; i < rankings.size(); i++) {
                ExerciseRankingDTO ranking = rankings.get(i);
                Row row = ExcelUtil.createRow(sheet, i + 1);
                
                ExcelUtil.setCellValue(ExcelUtil.createCell(row, 0), ranking.getRank());
                ExcelUtil.setCellValue(ExcelUtil.createCell(row, 1), ranking.getStudentNo());
                ExcelUtil.setCellValue(ExcelUtil.createCell(row, 2), ranking.getName());
                ExcelUtil.setCellValue(ExcelUtil.createCell(row, 3), ranking.getClassName());
                ExcelUtil.setCellValue(ExcelUtil.createCell(row, 4), ranking.getTotalDistance());
            }
            
            ExcelUtil.autoSizeColumns(sheet);
            
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fileName = URLEncoder.encode("每日运动报告_" + sdf.format(date) + ".xlsx", "UTF-8");
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
    public Response<ExerciseRecord> save(@RequestBody ExerciseRecord exerciseRecord) {
        ExerciseRecord saved = exerciseService.save(exerciseRecord);
        return Response.success(saved);
    }

    @DeleteMapping("/{id}")
    public Response<String> delete(@PathVariable Long id) {
        exerciseService.delete(id);
        return Response.success("删除成功");
    }
}
