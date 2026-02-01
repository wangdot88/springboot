package dangod.springboot.repository;

import dangod.springboot.entity.EquipmentBorrow;
import dangod.springboot.entity.Equipment;
import dangod.springboot.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EquipmentBorrowRepository extends JpaRepository<EquipmentBorrow, Long> {
    
    List<EquipmentBorrow> findByStudent(Student student);
    
    List<EquipmentBorrow> findByStudentStudentId(String studentId);
    
    List<EquipmentBorrow> findByEquipment(Equipment equipment);
    
    List<EquipmentBorrow> findByEquipmentEquipmentCode(String equipmentCode);
    
    List<EquipmentBorrow> findByStatus(String status);
    
    @Query("SELECT eb FROM EquipmentBorrow eb WHERE eb.status = 'BORROWED' AND eb.expectedReturnDate < :currentDate")
    List<EquipmentBorrow> findOverdueBorrows(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT eb FROM EquipmentBorrow eb WHERE eb.student.studentId = :studentId AND eb.status = 'BORROWED'")
    List<EquipmentBorrow> findCurrentBorrowsByStudent(@Param("studentId") String studentId);
    
    @Query("SELECT eb FROM EquipmentBorrow eb WHERE eb.borrowDate BETWEEN :startDate AND :endDate")
    List<EquipmentBorrow> findByBorrowDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(eb) FROM EquipmentBorrow eb WHERE eb.equipment.equipmentCode = :equipmentCode AND eb.status = 'BORROWED'")
    Long countCurrentBorrowsByEquipment(@Param("equipmentCode") String equipmentCode);
    
    @Query("SELECT eb FROM EquipmentBorrow eb WHERE eb.status = 'BORROWED'")
    List<EquipmentBorrow> findAllActiveBorrows();
}