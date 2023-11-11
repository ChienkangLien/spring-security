package org.tutorial.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;
import org.tutorial.model.DTO.DeptDTO;
import org.tutorial.model.PO.DeptPO;
import org.tutorial.service.DeptService;

@Secured("ROLE_DEPT")
@RestController
@RequestMapping("/depts") // 更改為複數形式，表示部門資源的集合
public class DeptController {

	@Autowired
	private DeptService deptService;

	@GetMapping // 不需要指定路徑，表示獲取所有部門
	public ResponseEntity<List<DeptDTO>> getAllDepts() {
		return ResponseEntity.ok(deptService.getAll());
	}

	@GetMapping("/{deptno}") // 使用部門編號作為路徑參數
	public ResponseEntity<DeptDTO> getDept(@PathVariable Integer deptno) {
		return ResponseEntity.ok(deptService.getOneDept(deptno));
	}

	@PostMapping // 不需要指定路徑，表示創建新部門
	public ResponseEntity<DeptDTO> createDept(@RequestBody DeptPO deptPO) {
		return ResponseEntity.status(HttpStatus.CREATED).body(deptService.insert(deptPO));
	}

	@PutMapping("/{deptno}") // 使用部門編號作為路徑參數，表示更新特定部門
	public ResponseEntity<DeptDTO> updateDept(@PathVariable Integer deptno, @RequestBody DeptPO deptPO) {
		// 在實際應用中，您可能需要檢查deptno是否匹配deptPO中的部門編號
		return ResponseEntity.ok(deptService.update(deptPO));
	}

	@DeleteMapping("/{deptno}") // 使用部門編號作為路徑參數，表示刪除特定部門
	public ResponseEntity<Void> deleteDept(@PathVariable Integer deptno) {
		deptService.deleteDept(deptno);
		return ResponseEntity.noContent().build();
	}
}