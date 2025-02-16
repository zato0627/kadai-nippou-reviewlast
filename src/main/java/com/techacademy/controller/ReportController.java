package com.techacademy.controller;



import java.util.List;

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
import com.techacademy.entity.Employee.Role;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;
import com.techacademy.service.EmployeeService;

@Controller
@RequestMapping("dailyreports")
public class ReportController {
	private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService, EmployeeService employeeService) {
        this.reportService = reportService;
    }

    // 日報一覧画面
    @GetMapping
    public String reportList(@AuthenticationPrincipal UserDetail userDetail, Model model) {
    	//findByEmployeeで従業員のデータを取得しListに追加
    	List<Report> reports = reportService.findByEmployee(userDetail.getEmployee());

    	if(Role.GENERAL.equals(userDetail.getEmployee().getRole())){	//userDetailから従業員の権限を取得しGENERAL（一般）の場合の処理

    		model.addAttribute("listSize", reports.size());		//上記で作成したListから.sizeで要素数を取得
    		model.addAttribute("reportList", reportService.findByEmployee(userDetail.getEmployee()));	//findByEmployeeで従業員のデータを取得


    	}else if(Role.ADMIN.equals(userDetail.getEmployee().getRole())) {	//userDetailから従業員の権限を取得しADMIN（管理者）の場合の処理
    		model.addAttribute("listSize", reportService.findAll().size());
    		model.addAttribute("reportList", reportService.findAll());
    	}

        return "dailyreports/reportlist";
    }

    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String reportDetail(@PathVariable("id") Integer id, Model model) {

    	model.addAttribute("report", reportService.getId(id));

    	return "dailyreports/reportdetail";

    }

    //日報　新規登録
    @GetMapping(value = "/newreport")
    public String createReport(@ModelAttribute Report report, @AuthenticationPrincipal UserDetail userDetail, Model model) {

    	String name = userDetail.getEmployee().getName();

    	model.addAttribute("name", name);

    	return "dailyreports/newreport";
    }
    //日報　新規登録処理
    @PostMapping(value = "/newreport")
    public String newReport(@Validated Report report, BindingResult res, @AuthenticationPrincipal UserDetail userDetail, Model model) {
    	Employee employee = new Employee();
    	employee = userDetail.getEmployee();
    	report.setEmployee(employee);

    	//入力チェック
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
    		model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DATECHECK_ERROR),
    					ErrorMessage.getErrorValue(ErrorKinds.DATECHECK_ERROR));
    		return createReport(report, userDetail, model);
    	}

    	return "redirect:/dailyreports";
    }

    //　日報更新
    @GetMapping(value = "/{id}/reportupdate/")
    public String getUpdate(@PathVariable("id") Integer id, Report report, Model model) {
    	if(id != null) {
    		model.addAttribute("report", reportService.getId(id));
    		return "dailyreports/reportupdate";
    	}else {
    		model.addAttribute("report", report);
    		return "dailyreports/reportupdate";
    	}
    }
    @PostMapping(value = "/{id}/reportupdate/")
    public String postUpdate(@PathVariable("id") Integer id, @Validated Report report, BindingResult res, Model model) {

    	if(res.hasErrors()) {
    		id = null;
    		return getUpdate(id, report, model);
    	}

    	try {
			ErrorKinds result = reportService.repUpdate(report, id);	//更新し、resultに格納
System.out.println("コントローラークラス：" + report);
			if (ErrorMessage.contains(result)) {	//エラーメッセージにresultが含まれているか確認

				model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));	//含まれていたらmodelにエラーメッセージの名前、値を追加
				return getUpdate(id, report, model);	//結果を返す
			}

		} catch (DataIntegrityViolationException e) {	//データベースの整合性制約に違反した場合にスローされる例外発生した場合、このブロックが実行

			model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
        	ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));

			return getUpdate(id, report, model);
		}
    	return "redirect:/dailyreports";
    }

    //日報削除処理
    @PostMapping(value = "/{id}/delete")
    public String delet(@PathVariable("id") Integer id) {

    	reportService.delete(id);

    	return "redirect:/dailyreports";
    }

}

