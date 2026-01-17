package dangod.springboot.repository;

import dangod.springboot.model.entity.EquipmentBorrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentBorrowRepository extends JpaRepository<EquipmentBorrow, Long> {
    Optional<EquipmentBorrow> findByBorrowNo(String borrowNo);
    List<EquipmentBorrow> findByStudentId(Long studentId);
    List<EquipmentBorrow> findByEquipmentId(Long equipmentId);
    List<EquipmentBorrow> findByStatus(String status);
    List<EquipmentBorrow> findByBorrowDate(Date borrowDate);
    
    @Query("SELECT eb FROM EquipmentBorrow eb WHERE eb.status = 'BORROWED' AND eb.expectedReturnDate < :currentDate")
    List<EquipmentBorrow> findOverdueBorrows(@Param("currentDate") Date currentDate);
    
    @Query("SELECT eb FROM EquipmentBorrow eb WHERE eb.studentId = :studentId AND eb.status = 'BORROWED'")
    List<EquipmentBorrow> findActiveBorrowsByStudent(@Param("studentId") Long studentId);
    
    @Query("SELECT eb FROM EquipmentBorrow eb WHERE eb.equipmentId = :equipmentId AND eb.status IN ('BORROWED', 'OVERDUE')")
    List<EquipmentBorrow> findActiveBorrowsByEquipment(@Param("equipmentId") Long equipmentId);
    
    @Query("SELECT COUNT(eb) FROM EquipmentBorrow eb WHERE eb.equipmentId = :equipmentId AND eb.status IN ('BORROWED', 'OVERDUE')")
    long countActiveBorrowsByEquipment(@Param("equipmentId") Long equipmentId);
    
    @Query("SELECT eb FROM EquipmentBorrow eb WHERE eb.borrowDate BETWEEN :startDate AND :endDate")
    List<EquipmentBorrow> findByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    List<EquipmentBorrow> findByEquipmentIdAndStudentId(Long equipmentId, Long studentId);
    List<EquipmentBorrow> findByEquipmentIdAndStatus(Long equipmentId, String status);
    List<EquipmentBorrow> findByStudentIdAndStatus(Long studentId, String status);
    List<EquipmentBorrow> findByEquipmentIdAndStudentIdAndStatus(Long equipmentId, Long studentId, String status);
}
