package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
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

	//1件を検索
	public Report findByCode(String code) {

		Optional<Report> option = reportRepository.findById(code);

		Report report = option.orElse(null);
		return report;
	}

	//日報保存
	@Transactional
	public ErrorKinds saveReport(Report report) {

		report.setDeleteFlg(false);

		LocalDate savedate = report.getReportDate();
		report.setReportDate(savedate);
		LocalDateTime now = LocalDateTime.now();
		report.setCreatedAt(now);
		report.setUpdatedAt(now);

		reportRepository.save(report);
		return ErrorKinds.SUCCESS;
	}

	// 日報更新　コードから日報を1件取得
	/*@Transactional
	public Report getReport(String code) {
		return reportRepository.findById(code).get();
	}*/
	@Transactional
	public ErrorKinds repUpdate(Report report, Integer id) {

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

}
