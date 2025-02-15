package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {

	private final ReportRepository reportRepository;

	public ReportService(ReportRepository reportRepository) {
		this.reportRepository = reportRepository;
	}

	//日報一覧表示処理
	public List<Report> findAll(){

		return reportRepository.findAll();
	}

	//idで検索
	@Transactional
	public Report getId(Integer id) {

		return reportRepository.findById(id).get(0);
	}

	//日報保存
	@Transactional
	public ErrorKinds saveReport(Report report) {

		//日付の重複チェック
		//Listないに同じ日付のデータがあるか判定(日付と名前を使ってる) .isEmpty()でListが空か調べる
		if(!findByReportDate(report.getReportDate(), report.getEmployee().getName()).isEmpty()) {

			return ErrorKinds.DATECHECK_ERROR;
		}
		report.setDeleteFlg(false);

		LocalDateTime now = LocalDateTime.now();
		report.setCreatedAt(now);
		report.setUpdatedAt(now);

		reportRepository.save(report);
		return ErrorKinds.SUCCESS;
	}
	// 日報更新
	@Transactional
	public ErrorKinds repUpdate(Report report, Integer id) {

		if(!findByRepDate(report.getReportDate()).isEmpty() && !report.getId().equals(getId(id).getId())) {

			return ErrorKinds.DATECHECK_ERROR;

		}


		LocalDateTime now = LocalDateTime.now();
		report.setUpdatedAt(now);
		LocalDateTime saveTime = report.getCreatedAt();
		report.setCreatedAt(saveTime);

		reportRepository.save(report);

		return ErrorKinds.SUCCESS;
	}

	//　日報削除
	@Transactional
	public ErrorKinds delete(Integer id) {

		Report report = getId(id);
		LocalDateTime now = LocalDateTime.now();
		report.setUpdatedAt(now);
		report.setDeleteFlg(true);

		return ErrorKinds.SUCCESS;
	}
	//日付と名前で検索
	public List<Report> findByReportDate(LocalDate reportDate, String name) {

		return reportRepository.findByReportDateAndEmployee_Name(reportDate, name);
	}

	//日付で検索
	public List<Report> findByRepDate(LocalDate reportDate) {

		return reportRepository.findByReportDate(reportDate);
	}

	public List<Report> findByEmployee(Employee employee) {
	    return reportRepository.findByEmployee(employee);
	}


}
