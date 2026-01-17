package com.example.springboot.repository;

import com.example.springboot.entity.EquipmentBorrow;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EquipmentBorrowRepository extends JpaRepository<EquipmentBorrow, Long> {
    Optional<EquipmentBorrow> findById(Long id);
    List<EquipmentBorrow> findByIsReturned(Boolean isReturned);
    
    @Query("SELECT eb FROM EquipmentBorrow eb WHERE eb.isReturned = false AND eb.expectedReturnTime < :now")
    List<EquipmentBorrow> findOverdueBorrows(@Param("now") LocalDateTime now);
    
    @Query("SELECT eb FROM EquipmentBorrow eb WHERE eb.equipment.id = :equipmentId AND eb.isReturned = false")
    List<EquipmentBorrow> findActiveBorrowsByEquipment(@Param("equipmentId") Long equipmentId);
    
    @Query("SELECT eb FROM EquipmentBorrow eb WHERE eb.student.id = :studentId AND eb.isReturned = false")
    List<EquipmentBorrow> findActiveBorrowsByStudent(@Param("studentId") Long studentId);
}
