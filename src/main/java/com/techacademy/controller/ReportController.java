package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;
import com.techacademy.service.EmployeeService;

@Controller
@RequestMapping("dailyreports")
public class ReportController {
	private final EmployeeService employeeService;
	private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService, EmployeeService employeeService) {
        this.reportService = reportService;
        this.employeeService = employeeService;
    }

    // 日報一覧画面
    @GetMapping
    public String reportList( Model model) {

        model.addAttribute("listSize", reportService.findAll().size());
        model.addAttribute("reportList", reportService.findAll());

        return "dailyreports/reportlist";
    }

    // 日報詳細画面
    @GetMapping(value = "/{code}/")
    public String reportDetail(@PathVariable("code") String code, Model model) {

    	model.addAttribute("report", reportService.findByCode(code));

    	return "dailyreports/reportdetail";

    }

    //日報　新規登録
    @GetMapping(value = "/newreport")
    public String createReport(@ModelAttribute Report report) {

    	return "dailyreports/newreport";
    }

    //日報　新規登録処理
    @PostMapping(value = "/newreport")
    public String newReport(@Validated Report report, BindingResult res, @AuthenticationPrincipal UserDetail userDetail, Model model) {

    	String name = userDetail.getEmployee().getName();
    	report.getEmployee().setName(name);

    	if(res.hasErrors()) {
    		return createReport(report);
    	}

    	try {
    		ErrorKinds result = reportService.saveReport(report);

    		if(ErrorMessage.contains(result)) {
    			model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
    			return createReport(report);
    		}
    	}catch(DataIntegrityViolationException e) {
    		model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
    					ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
    		return createReport(report);
    	}

    	return "redirect:/dairyreports";
    }

    //　日報更新
    @GetMapping(value = "/{code}/reportupdate/")
    public String reportUpdate(@PathVariable("code") String code, Employee employee, Model model) {

    	model.addAttribute("employee", employeeService.getEmployee(code));

    	return "dailyreports/reportupdate";
    }

}

