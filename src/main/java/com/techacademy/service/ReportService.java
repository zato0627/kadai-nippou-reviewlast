package com.techacademy.service;

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

	//1件を検索
	public Report findByCode(String code) {

		Optional<Report> option = reportRepository.findById(code);

		Report report = option.orElse(null);
		return report;
	}

	//日報保存
	@Transactional
	public ErrorKinds saveReport(Report report) {

		//if(report.getReportDate() != ) {
		//	return ErrorKinds.DATECHECK_ERROR;
		//}

		report.setDeleteFlg(false);

		LocalDateTime now = LocalDateTime.now();
		report.setCreatedAt(now);
		report.setUpdatedAt(now);

		reportRepository.save(report);
		return ErrorKinds.SUCCESS;
	}

}
