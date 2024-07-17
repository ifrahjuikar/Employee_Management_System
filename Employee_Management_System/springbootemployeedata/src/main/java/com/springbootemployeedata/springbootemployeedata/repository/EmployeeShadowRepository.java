package com.springbootemployeedata.springbootemployeedata.repository;

import com.springbootemployeedata.springbootemployeedata.Entity.EmployeeShadow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository interface for managing EmployeeShadow entities.
 
@Repository
public interface EmployeeShadowRepository extends JpaRepository<EmployeeShadow, Integer> {
}
