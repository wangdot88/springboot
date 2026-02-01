package dangod.springboot.repository.equipment;

import dangod.springboot.entity.equipment.EquipmentBorrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentBorrowRepository extends JpaRepository<EquipmentBorrow, Long> {

    default Optional<EquipmentBorrow> findById(Long id) {
        return Optional.ofNullable(findOne(id));
    }

    List<EquipmentBorrow> findByStudentId(Long studentId);

    List<EquipmentBorrow> findByEquipmentId(Long equipmentId);

    List<EquipmentBorrow> findByStatus(String status);

    @Query("SELECT b FROM EquipmentBorrow b WHERE b.expectedReturnDate < :date AND b.actualReturnDate IS NULL")
    List<EquipmentBorrow> findOverdueBorrows(@Param("date") LocalDate date);

    @Query("SELECT b FROM EquipmentBorrow b WHERE b.expectedReturnDate BETWEEN :startDate AND :endDate AND b.actualReturnDate IS NULL")
    List<EquipmentBorrow> findSoonOverdueBorrows(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(b) FROM EquipmentBorrow b WHERE b.equipment.id = :equipmentId AND b.status = '已借出'")
    Long countBorrowedByEquipmentId(@Param("equipmentId") Long equipmentId);

    @Query("SELECT b FROM EquipmentBorrow b WHERE b.borrowDate BETWEEN :startDate AND :endDate")
    List<EquipmentBorrow> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT b.equipment.id, COUNT(b) FROM EquipmentBorrow b WHERE b.borrowDate BETWEEN :startDate AND :endDate GROUP BY b.equipment.id")
    List<Object[]> countBorrowsByEquipment(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
