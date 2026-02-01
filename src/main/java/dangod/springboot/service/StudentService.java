package dangod.springboot.service;

import dangod.springboot.entity.Student;
import dangod.springboot.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    public Student addStudent(Student student) {
        if (studentRepository.existsByStudentId(student.getStudentId())) {
            throw new RuntimeException("学号已存在");
        }
        
        return studentRepository.save(student);
    }
    
    public Student updateStudent(String studentId, Student studentDetails) {
        Optional<Student> studentOpt = studentRepository.findByStudentId(studentId);
        if (!studentOpt.isPresent()) {
            throw new RuntimeException("学生不存在");
        }
        
        Student student = studentOpt.get();
        student.setName(studentDetails.getName());
        student.setGender(studentDetails.getGender());
        student.setAge(studentDetails.getAge());
        student.setClassName(studentDetails.getClassName());
        student.setMajor(studentDetails.getMajor());
        student.setPhone(studentDetails.getPhone());
        student.setEmail(studentDetails.getEmail());
        
        return studentRepository.save(student);
    }
    
    public void deleteStudent(String studentId) {
        Optional<Student> studentOpt = studentRepository.findByStudentId(studentId);
        if (!studentOpt.isPresent()) {
            throw new RuntimeException("学生不存在");
        }
        
        studentRepository.delete(studentOpt.get());
    }
    
    public Optional<Student> getStudentByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId);
    }
    
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    
    public List<Student> getStudentsByClass(String className) {
        return studentRepository.findByClassName(className);
    }
    
    public List<Student> getStudentsByMajor(String major) {
        return studentRepository.findByMajor(major);
    }
    
    public List<Student> getWeakStudents() {
        return studentRepository.findByIsWeakPhysical(true);
    }
    
    public List<Student> getWeakStudentsByClass(String className) {
        return studentRepository.findWeakStudentsByClass(className);
    }
}