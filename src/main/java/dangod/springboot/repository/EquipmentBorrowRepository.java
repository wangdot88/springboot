package dangod.springboot.repository;

import dangod.springboot.entity.EquipmentBorrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EquipmentBorrowRepository extends JpaRepository<EquipmentBorrow, Long> {

    List<EquipmentBorrow> findByStudentId(Long studentId);

    List<EquipmentBorrow> findByEquipmentId(Long equipmentId);

    List<EquipmentBorrow> findByStatus(String status);

    @Query("SELECT eb FROM EquipmentBorrow eb WHERE eb.student.id = :studentId AND eb.status = 'BORROWED'")
    List<EquipmentBorrow> findActiveBorrowsByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT eb FROM EquipmentBorrow eb WHERE eb.status = 'BORROWED' AND eb.expectedReturnDate < :currentDate")
    List<EquipmentBorrow> findOverdueBorrows(@Param("currentDate") Date currentDate);

    @Query("SELECT COUNT(eb) FROM EquipmentBorrow eb WHERE eb.equipment.id = :equipmentId AND eb.status = 'BORROWED'")
    Long countActiveBorrowsByEquipmentId(@Param("equipmentId") Long equipmentId);

    @Query("SELECT COUNT(eb) FROM EquipmentBorrow eb WHERE eb.borrowDate BETWEEN :startDate AND :endDate")
    Long countBorrowsByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT eb FROM EquipmentBorrow eb WHERE eb.borrowDate BETWEEN :startDate AND :endDate")
    List<EquipmentBorrow> findByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
