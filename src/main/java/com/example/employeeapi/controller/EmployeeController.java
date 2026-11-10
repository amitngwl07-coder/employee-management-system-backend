package com.example.employeeapi.controller;

import com.example.employeeapi.dto.CreateEmployeeRequest;
import com.example.employeeapi.dto.EmpPaginationResponse;
import com.example.employeeapi.dto.EmployeeResponse;
import com.example.employeeapi.dto.EmployeeStatisticsResponse;
import com.example.employeeapi.entity.Employee;
import com.example.employeeapi.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @PostMapping
    //@ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody CreateEmployeeRequest request){
        EmployeeResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    @GetMapping
    public EmpPaginationResponse getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdOn") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status
    ) {

        Sort.Direction sortDirection =
                Sort.Direction.fromString(direction);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy)
        );

        return service.getAll(
                pageable,
                search,
                department,
                status
        );
    }



    @GetMapping("/{id}")
    public EmployeeResponse getById(@PathVariable Long id){
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public EmployeeResponse updateEmployee(@PathVariable Long id,
                                   @RequestBody CreateEmployeeRequest request){
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        service.delete(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll(){
        service.deleteALLEmployee();
    }

    @GetMapping("/statistics")
    public ResponseEntity<EmployeeStatisticsResponse> getEmployeeStatistics() {

        EmployeeStatisticsResponse statistics = service.getEmployeeStatistics();

        return ResponseEntity.ok(statistics);
    }
}
