package dangod.springboot.controller;

import dangod.springboot.entity.Student;
import dangod.springboot.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    
    @Autowired
    private StudentService studentService;
    
    @PostMapping("/add")
    public ResponseEntity<?> addStudent(@RequestBody Student student) {
        try {
            Student newStudent = studentService.addStudent(student);
            return ResponseEntity.ok(newStudent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("添加学生失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/update/{studentId}")
    public ResponseEntity<?> updateStudent(@PathVariable String studentId, @RequestBody Student studentDetails) {
        try {
            Student updatedStudent = studentService.updateStudent(studentId, studentDetails);
            return ResponseEntity.ok(updatedStudent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("更新学生信息失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/delete/{studentId}")
    public ResponseEntity<?> deleteStudent(@PathVariable String studentId) {
        try {
            studentService.deleteStudent(studentId);
            return ResponseEntity.ok("删除学生成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("删除学生失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{studentId}")
    public ResponseEntity<?> getStudentByStudentId(@PathVariable String studentId) {
        try {
            Optional<Student> student = studentService.getStudentByStudentId(studentId);
            if (student.isPresent()) {
                return ResponseEntity.ok(student.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取学生信息失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllStudents() {
        try {
            List<Student> students = studentService.getAllStudents();
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取所有学生失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/class/{className}")
    public ResponseEntity<?> getStudentsByClass(@PathVariable String className) {
        try {
            List<Student> students = studentService.getStudentsByClass(className);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取班级学生失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/major/{major}")
    public ResponseEntity<?> getStudentsByMajor(@PathVariable String major) {
        try {
            List<Student> students = studentService.getStudentsByMajor(major);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取专业学生失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/weak")
    public ResponseEntity<?> getWeakStudents() {
        try {
            List<Student> students = studentService.getWeakStudents();
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取体弱学生失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/weak/{className}")
    public ResponseEntity<?> getWeakStudentsByClass(@PathVariable String className) {
        try {
            List<Student> students = studentService.getWeakStudentsByClass(className);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取班级体弱学生失败: " + e.getMessage());
        }
    }
}