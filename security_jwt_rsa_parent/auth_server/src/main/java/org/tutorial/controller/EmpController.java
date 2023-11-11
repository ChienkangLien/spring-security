package org.tutorial.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tutorial.model.DTO.EmpDTO;
import org.tutorial.model.Enum.SortField;
import org.tutorial.model.PO.EmpPO;
import org.tutorial.service.EmpService;

@Secured("ROLE_EMP")
@RestController
@RequestMapping("/emps")
public class EmpController {
	@Autowired
	EmpService empService;

	@GetMapping
	public ResponseEntity<List<EmpDTO>> getAllEmployees() {
		return ResponseEntity.ok(empService.getAll());
	}

	@GetMapping("/{empno}")
	public ResponseEntity<EmpDTO> getEmployee(
			 @PathVariable Integer empno) {
		return ResponseEntity.ok(empService.getOneEmp(empno));
	}

	@PostMapping
	public ResponseEntity<EmpDTO> createEmployee(@RequestBody EmpPO empPO) {
		return ResponseEntity.status(HttpStatus.CREATED).body(empService.addEmp(empPO));
	}

	@PutMapping("/{empno}")
	public ResponseEntity<EmpDTO> updateEmployee(
			 @PathVariable Integer empno,
			@RequestBody EmpPO empPO) {
		// 在實際應用中，可能需要檢查empno是否匹配empPO中的員工編號
		return ResponseEntity.ok(empService.updateEmp(empPO));
	}

	@DeleteMapping("/{empno}")
	public void deleteEmployee(
			 @PathVariable Integer empno) {
		empService.deleteEmp(empno);
	}

	@GetMapping("/dept/{deptno}")
	public ResponseEntity<List<EmpDTO>> getEmployeesByDept(
			 @PathVariable Integer deptno) {
		return ResponseEntity.ok(empService.getEmpsByDeptno(deptno));
	}

	@GetMapping("/page")
	public ResponseEntity<Page<EmpDTO>> getEmployeesByPageAndSort(
			 @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
			 @RequestParam(name = "pageSize", defaultValue = "5") int pageSize,
			 @RequestParam(name = "sortField", defaultValue = "EMP_NO") SortField sortField,
			 @RequestParam(name = "sortDirection", defaultValue = "ASC") Sort.Direction sortDirection) {
		return ResponseEntity
				.ok(empService.getEmpsByPageAndSort(pageNumber, pageSize, sortField.getValue(), sortDirection));
	}

}
