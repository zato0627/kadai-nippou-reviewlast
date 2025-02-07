package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.entity.Employee;
import com.techacademy.service.ReportService;
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
        model.addAttribute("reporList", reportService.findAll());

        return "dailyreports/reportlist";
    }

    // 日報詳細画面
    @GetMapping(value = "/{code}/")
    public String reportDetail(@PathVariable("code") String code, Model model) {

    	model.addAttribute("employee", employeeService.findByCode(code));

    	return "dailyreports/reportdetail";

    }

    //日報　新規登録
    @GetMapping(value = "/newreport")
    public String createReport(@ModelAttribute Employee employee) {

    	return "dailyreports/newreport";
    }

    //　日報更新
    @GetMapping(value = "/{code}/reportupdate/")
    public String reportUpdate(@PathVariable("code") String code, Employee employee, Model model) {

    	model.addAttribute("employee", employeeService.getEmployee(code));

    	return "dailyreports/reportupdate";
    }

}

