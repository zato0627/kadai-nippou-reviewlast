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
import com.techacademy.service.EmployeeService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // 従業員一覧画面
    @GetMapping
    public String list(Model model) {

        model.addAttribute("listSize", employeeService.findAll().size());
        model.addAttribute("employeeList", employeeService.findAll());

        return "employees/list";
    }

    // 従業員詳細画面
    @GetMapping(value = "/{code}/")
    public String detail(@PathVariable("code") String code, Model model) {

        model.addAttribute("employee", employeeService.findByCode(code));

        return "employees/detail";
    }

    // 従業員新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Employee employee) {

        return "employees/new";
    }

    // 従業員新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Employee employee, BindingResult res, Model model) {

        // パスワード空白チェック
        /*
         * エンティティ側の入力チェックでも実装は行えるが、更新の方でパスワードが空白でもチェックエラーを出さずに
         * 更新出来る仕様となっているため上記を考慮した場合に別でエラーメッセージを出す方法が簡単だと判断
         */
        if ("".equals(employee.getPassword())) {
            // パスワードが空白だった場合
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.BLANK_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.BLANK_ERROR));

            return create(employee);

        }

        // 入力チェック
        if (res.hasErrors()) {
            return create(employee);
        }

        // 論理削除を行った従業員番号を指定すると例外となるためtry~catchで対応
        // (findByIdでは削除フラグがTRUEのデータが取得出来ないため)
        try {
            ErrorKinds result = employeeService.save(employee);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return create(employee);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return create(employee);
        }

        return "redirect:/employees";
    }

    //従業員更新機能

    /** 更新画面を表示 */
    @GetMapping(value = "/{code}/update/")
    public String getUpdate(@PathVariable("code") String code, Employee employee, Model model) {
    	if (code != null) {

    		//urlのcode(社員番号)から社員のデータを取得
    		model.addAttribute("employee", employeeService.getEmployee(code));				//modelにデータを登録

    		return "employees/update";	//updateに画面遷移

    	}else {
    		model.addAttribute("employee", employee);

    		return "employees/update";
    	}


    }
    /** 更新処理　*/
    @PostMapping(value = "/{code}/update/")
    public String postUpdate(@PathVariable("code") String code, @Validated Employee employee, BindingResult res, Model model) {
    	// 入力チェック
    	if(res.hasErrors()) {
    		code = null;

			return getUpdate(code, employee, model);
    	}

    	// 従業員新規登録処理のものを再利用
    	try {

			ErrorKinds result = employeeService.upDate(employee);	//employeeを更新し、resultに格納

			if (ErrorMessage.contains(result)) {	//エラーメッセージにresultが含まれているか確認

				model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));	//含まれていたらmodelにエラーメッセージの名前、値を追加

				return getUpdate(code, employee, model);	//getUpdateに結果を返す
			}

		} catch (DataIntegrityViolationException e) {	//データベースの整合性制約に違反した場合にスローされる例外発生した場合、このブロックが実行

			model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
        	ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));

			return getUpdate(code, employee, model);
		}
        return "redirect:/employees";	//一覧画面にリダイレクト

    }

    // 従業員削除処理
    @PostMapping(value = "/{code}/delete")
    public String delete(@PathVariable("code") String code, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        ErrorKinds result = employeeService.delete(code, userDetail);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("employee", employeeService.findByCode(code));
            return detail(code, model);
        }

        return "redirect:/employees";
    }

}
