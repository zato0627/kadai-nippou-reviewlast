package com.techacademy.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Integer>{



	List<Report> findByReportDateAndEmployeeName(LocalDate reportDate, String name);
	List<Report> findByReportDate(LocalDate reportDate);
	List<Report> findByEmployee(Employee employee);



}
