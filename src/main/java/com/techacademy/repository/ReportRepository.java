package com.techacademy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Employee.Role;
import com.techacademy.entity.Report;

public interface ReportRepository extends JpaRepository<Report, String>{

	List<Report> findByEmployee_Role(Role role);
	List<Report> findByEmployee_Name(String code);

}
