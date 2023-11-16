package org.tutorial.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/emps")
public class EmpController {
	@GetMapping
	public String getAllEmployees() {
		return "查詢員工資訊成功";
	}
}
