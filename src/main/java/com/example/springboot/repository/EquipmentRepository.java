package com.example.springboot.repository;

import com.example.springboot.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    Optional<Equipment> findById(Long id);
    List<Equipment> findByStatus(String status);
    
    @Query("SELECT e FROM Equipment e WHERE e.availableCount <= e.warningThreshold")
    List<Equipment> findLowStockEquipment();
    
    List<Equipment> findByNameContainingOrTypeContaining(String name, String type);
}
