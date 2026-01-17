package com.example.springboot.repository;

import com.example.springboot.entity.EquipmentRepair;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepairRepository extends JpaRepository<EquipmentRepair, Long> {
    Optional<EquipmentRepair> findById(Long id);
    List<EquipmentRepair> findByRepairStatus(String repairStatus);
    List<EquipmentRepair> findByEquipmentId(Long equipmentId);
}
