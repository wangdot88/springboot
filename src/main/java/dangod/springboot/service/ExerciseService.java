package dangod.springboot.service;

import dangod.springboot.dto.ExerciseRankingDTO;
import dangod.springboot.entity.ExerciseRecord;
import dangod.springboot.entity.Student;
import dangod.springboot.repository.ExerciseRecordRepository;
import dangod.springboot.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ExerciseService {

    @Autowired
    private ExerciseRecordRepository exerciseRecordRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Transactional
    public ExerciseRecord recordExercise(Long studentId, String exerciseType, Double distance, 
                                          Integer duration, Double calories, String location) {
        Student student = studentRepository.findOne(studentId);
        if (student == null) {
            return null;
        }

        ExerciseRecord record = new ExerciseRecord();
        record.setStudent(student);
        record.setRecordDate(new Date());
        record.setExerciseType(exerciseType);
        record.setDistance(distance);
        record.setDuration(duration);
        record.setCalories(calories);
        record.setLocation(location);
        record.setCreateTime(new Date());

        return exerciseRecordRepository.save(record);
    }

    public List<ExerciseRecord> getExerciseRecordsByStudent(Long studentId) {
        return exerciseRecordRepository.findByStudentId(studentId);
    }

    public List<ExerciseRecord> getExerciseRecordsByDate(Date date) {
        return exerciseRecordRepository.findByRecordDate(date);
    }

    public List<ExerciseRecord> getExerciseRecordsByClassAndDate(Long classId, Date date) {
        return exerciseRecordRepository.findByClassIdAndRecordDate(classId, date);
    }

    public List<ExerciseRecord> getExerciseRecordsByDateRange(Date startDate, Date endDate) {
        return exerciseRecordRepository.findByDateRange(startDate, endDate);
    }

    public List<ExerciseRankingDTO> getDailyRanking(Date date) {
        List<Object[]> results = exerciseRecordRepository.findDailyRanking(date);
        List<ExerciseRankingDTO> rankings = new ArrayList<>();
        
        for (int i = 0; i < results.size(); i++) {
            Object[] row = results.get(i);
            Long studentId = (Long) row[0];
            Double totalDistance = (Double) row[1];
            
            Student student = studentRepository.findOne(studentId);
            if (student != null) {
                ExerciseRankingDTO dto = new ExerciseRankingDTO();
                dto.setStudentId(studentId);
                dto.setStudentNo(student.getStudentNo());
                dto.setName(student.getName());
                if (student.getClassInfo() != null) {
                    dto.setClassName(student.getClassInfo().getClassName());
                }
                dto.setTotalDistance(totalDistance);
                dto.setRank(i + 1);
                rankings.add(dto);
            }
        }
        
        return rankings;
    }

    public List<ExerciseRankingDTO> getClassDailyRanking(Long classId, Date date) {
        List<ExerciseRecord> records = exerciseRecordRepository.findByClassIdAndRecordDate(classId, date);
        List<ExerciseRankingDTO> rankings = new ArrayList<>();
        
        java.util.Map<Long, Double> distanceMap = new java.util.HashMap<>();
        for (ExerciseRecord record : records) {
            Long studentId = record.getStudent().getId();
            Double distance = distanceMap.getOrDefault(studentId, 0.0);
            if (record.getDistance() != null) {
                distance += record.getDistance();
            }
            distanceMap.put(studentId, distance);
        }
        
        List<java.util.Map.Entry<Long, Double>> sortedList = new ArrayList<>(distanceMap.entrySet());
        sortedList.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        for (int i = 0; i < sortedList.size(); i++) {
            java.util.Map.Entry<Long, Double> entry = sortedList.get(i);
            Long studentId = entry.getKey();
            Double totalDistance = entry.getValue();
            
            Student student = studentRepository.findOne(studentId);
            if (student != null) {
                ExerciseRankingDTO dto = new ExerciseRankingDTO();
                dto.setStudentId(studentId);
                dto.setStudentNo(student.getStudentNo());
                dto.setName(student.getName());
                if (student.getClassInfo() != null) {
                    dto.setClassName(student.getClassInfo().getClassName());
                }
                dto.setTotalDistance(totalDistance);
                dto.setRank(i + 1);
                rankings.add(dto);
            }
        }
        
        return rankings;
    }

    public List<ExerciseRankingDTO> getPeriodRanking(Date startDate, Date endDate) {
        List<Object[]> results = exerciseRecordRepository.findPeriodRanking(startDate, endDate);
        List<ExerciseRankingDTO> rankings = new ArrayList<>();
        
        for (int i = 0; i < results.size(); i++) {
            Object[] row = results.get(i);
            Long studentId = (Long) row[0];
            Double totalDistance = (Double) row[1];
            
            Student student = studentRepository.findOne(studentId);
            if (student != null) {
                ExerciseRankingDTO dto = new ExerciseRankingDTO();
                dto.setStudentId(studentId);
                dto.setStudentNo(student.getStudentNo());
                dto.setName(student.getName());
                if (student.getClassInfo() != null) {
                    dto.setClassName(student.getClassInfo().getClassName());
                }
                dto.setTotalDistance(totalDistance);
                dto.setRank(i + 1);
                rankings.add(dto);
            }
        }
        
        return rankings;
    }

    public List<Student> getInactiveStudents(Long classId, Date date) {
        List<Student> allStudents = studentRepository.findByClassInfoId(classId);
        List<ExerciseRecord> records = exerciseRecordRepository.findByClassIdAndRecordDate(classId, date);
        
        java.util.Set<Long> activeStudentIds = new java.util.HashSet<>();
        for (ExerciseRecord record : records) {
            activeStudentIds.add(record.getStudent().getId());
        }
        
        List<Student> inactiveStudents = new ArrayList<>();
        for (Student student : allStudents) {
            if (!activeStudentIds.contains(student.getId())) {
                inactiveStudents.add(student);
            }
        }
        
        return inactiveStudents;
    }

    public ExerciseRecord save(ExerciseRecord exerciseRecord) {
        if (exerciseRecord.getId() == null) {
            exerciseRecord.setCreateTime(new Date());
        }
        return exerciseRecordRepository.save(exerciseRecord);
    }

    public void delete(Long id) {
        exerciseRecordRepository.delete(id);
    }
}
