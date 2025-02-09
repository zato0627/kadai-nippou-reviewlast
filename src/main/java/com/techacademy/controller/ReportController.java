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
    public String createReport(@ModelAttribute Report report, @AuthenticationPrincipal UserDetail userDetail, Model model) {
    	String code = userDetail.getEmployee().getCode();
    	String name = userDetail.getEmployee().getName();
    	model.addAttribute("code", code);
    	model.addAttribute("name", name);

    	return "dailyreports/newreport";
    }
    //日報　新規登録処理
    @PostMapping(value = "/newreport")
    public String newReport(@Validated Report report, BindingResult res, @AuthenticationPrincipal UserDetail userDetail, Model model) {
    	Employee employee = new Employee();
    	employee = userDetail.getEmployee();

    	model.addAttribute(employee);
    	if(res.hasErrors()) {
    			return createReport(report, userDetail, model);
    	}

    	try {
    		ErrorKinds result = reportService.saveReport(report);

    		if(ErrorMessage.contains(result)) {
    			model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
    			return createReport(report, userDetail, model);
    		}
    	}catch(DataIntegrityViolationException e) {
    		model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
    					ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
    		return createReport(report, userDetail, model);
    	}


    	System.out.println("名前" + report);

    	return "redirect:/dairyreports";
    }

    //　日報更新
    @GetMapping(value = "/{code}/reportupdate/")
    public String getUpdate(@PathVariable("code") String code, Report report, Model model) {
    	if(code != null) {
    		model.addAttribute("report", reportService.getReport(code));
    		return "dailyreports/reportupdate";
    	}else {
    		model.addAttribute("report", report);
    		return "dailyreports/reportupdate";
    	}
    }
    @PostMapping(value = "/{code}/reportupdate/")
    public String postUpdate(@PathVariable("code") String code, @Validated Report report, BindingResult res, Model model) {
    	//
    	if(res.hasErrors()) {
    		code = null;
    		return getUpdate(code, report, model);
    	}

    	try {
			ErrorKinds result = reportService.repUpdate(report, code);	//更新し、resultに格納

			if (ErrorMessage.contains(result)) {	//エラーメッセージにresultが含まれているか確認

				model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));	//含まれていたらmodelにエラーメッセージの名前、値を追加

				return getUpdate(code, report, model);	//結果を返す
			}

		} catch (DataIntegrityViolationException e) {	//データベースの整合性制約に違反した場合にスローされる例外発生した場合、このブロックが実行

			model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
        	ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));

			return getUpdate(code, report, model);
		}
    	System.out.println(report);
    	return "redirect:/dailyreports";
    }

    //日報削除処理
    @PostMapping(value = "/{code}/delete")
    public String delet(@PathVariable("code") String code) {

    	reportService.delete(code);

    	return "redirect:/dailyreports";
    }

}

