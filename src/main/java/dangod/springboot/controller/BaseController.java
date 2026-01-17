package dangod.springboot.controller;

import dangod.springboot.core.response.Response;
import dangod.springboot.entity.ClassInfo;
import dangod.springboot.entity.Student;
import dangod.springboot.repository.ClassInfoRepository;
import dangod.springboot.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/base")
public class BaseController {

    @Autowired
    private ClassInfoRepository classInfoRepository;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/classes")
    public Response<List<ClassInfo>> getAllClasses() {
        List<ClassInfo> classes = classInfoRepository.findAll();
        return Response.success(classes);
    }

    @GetMapping("/classes/{id}")
    public Response<ClassInfo> getClassById(@PathVariable Long id) {
        ClassInfo classInfo = classInfoRepository.findOne(id);
        if (classInfo != null) {
            return Response.success(classInfo);
        }
        return Response.error("班级不存在");
    }

    @PostMapping("/classes")
    public Response<ClassInfo> saveClass(@RequestBody ClassInfo classInfo) {
        ClassInfo saved = classInfoRepository.save(classInfo);
        return Response.success(saved);
    }

    @DeleteMapping("/classes/{id}")
    public Response<String> deleteClass(@PathVariable Long id) {
        classInfoRepository.delete(id);
        return Response.success("删除成功");
    }

    @GetMapping("/students")
    public Response<List<Student>> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        return Response.success(students);
    }

    @GetMapping("/students/{id}")
    public Response<Student> getStudentById(@PathVariable Long id) {
        Student student = studentRepository.findOne(id);
        if (student != null) {
            return Response.success(student);
        }
        return Response.error("学生不存在");
    }

    @GetMapping("/students/class/{classId}")
    public Response<List<Student>> getStudentsByClassId(@PathVariable Long classId) {
        List<Student> students = studentRepository.findByClassInfoId(classId);
        return Response.success(students);
    }

    @PostMapping("/students")
    public Response<Student> saveStudent(@RequestBody Student student) {
        Student saved = studentRepository.save(student);
        return Response.success(saved);
    }

    @DeleteMapping("/students/{id}")
    public Response<String> deleteStudent(@PathVariable Long id) {
        studentRepository.delete(id);
        return Response.success("删除成功");
    }
}
