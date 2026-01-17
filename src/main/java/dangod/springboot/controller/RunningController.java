package dangod.springboot.controller;

import dangod.springboot.core.common.Result;
import dangod.springboot.model.dto.RunningRecordDTO;
import dangod.springboot.service.RunningService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Api(tags = "运动监督")
@RestController
@RequestMapping("/api/running")
public class RunningController {

    @Autowired
    private RunningService runningService;

    @ApiOperation("添加跑步打卡记录")
    @PostMapping("/record")
    public Result addRunningRecord(@RequestBody RunningRecordDTO record) {
        try {
            runningService.addRunningRecord(record);
            return Result.success("打卡成功");
        } catch (Exception e) {
            return Result.error("打卡失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取跑步打卡记录")
    @GetMapping("/records")
    public Result getRunningRecords(
            @ApiParam("学生ID") @RequestParam(required = false) Long studentId,
            @ApiParam("开始日期") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期") @RequestParam(required = false) String endDate,
            @ApiParam("页码") @RequestParam(defaultValue = "0") int page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") int size) {
        return Result.success(runningService.getRunningRecords(studentId, startDate, endDate, page, size));
    }

    @ApiOperation("获取每日跑步排行榜")
    @GetMapping("/daily-ranking")
    public Result getDailyRanking(
            @ApiParam("日期") @RequestParam(required = false) String date) {
        List<Map<String, Object>> ranking = runningService.getDailyRanking(date);
        return Result.success(ranking);
    }

    @ApiOperation("获取班级运动排行榜")
    @GetMapping("/class-ranking")
    public Result getClassRanking(
            @ApiParam("开始日期") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期") @RequestParam(required = false) String endDate) {
        List<Map<String, Object>> ranking = runningService.getClassRanking(startDate, endDate);
        return Result.success(ranking);
    }

    @ApiOperation("获取未运动学生列表")
    @GetMapping("/students-not-exercised")
    public Result getStudentsNotExercised(
            @ApiParam("日期") @RequestParam(required = false) String date) {
        List<Map<String, Object>> students = runningService.getStudentsNotExercised(date);
        return Result.success(students);
    }

    @ApiOperation("获取学生周运动统计")
    @GetMapping("/student-weekly-stats")
    public Result getStudentWeeklyStats(@ApiParam("学生ID") @RequestParam Long studentId) {
        Map<String, Object> stats = runningService.getStudentWeeklyStats(studentId);
        return Result.success(stats);
    }

    @ApiOperation("导出每日运动报告")
    @GetMapping("/export-daily-report")
    public void exportDailyReport(
            @ApiParam("日期") @RequestParam String date,
            HttpServletResponse response) throws IOException {
        runningService.exportDailyReport(date, response);
    }
}
