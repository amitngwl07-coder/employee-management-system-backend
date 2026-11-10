
package com.example.employeeapi.service;

import com.example.employeeapi.dto.CreateEmployeeRequest;
import com.example.employeeapi.dto.EmpPaginationResponse;
import com.example.employeeapi.dto.EmployeeResponse;
import com.example.employeeapi.dto.EmployeeStatisticsResponse;
import com.example.employeeapi.entity.Employee;
import com.example.employeeapi.exception.DuplicateEmailException;
import com.example.employeeapi.exception.EmployeeNotFoundException;
import com.example.employeeapi.repository.EmployeeRepository;

import com.example.employeeapi.specification.EmployeeSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public EmployeeResponse create(CreateEmployeeRequest request) {

        if (repository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        Employee emp = new Employee();

        emp.setName(request.getName());
        emp.setEmail(request.getEmail());
        emp.setDepartment(request.getDepartment());

        emp.setJoiningDate(
                request.getJoiningDate() != null
                        ? request.getJoiningDate()
                        : LocalDate.now()
        );

        emp.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : "ACTIVE"
        );

        emp.setCreatedOn(LocalDateTime.now());

        // First save: generate the database ID
        Employee savedEmployee = repository.save(emp);

        // Generate employee code using the database ID
        if (savedEmployee.getId() > 9999) {
            throw new IllegalStateException(
                    "Employee code limit exceeded (maximum E9999)"
            );
        }

        savedEmployee.setEmployeeCode(
                String.format("E%04d", savedEmployee.getId())
        );

        // Second save: store the employee code
        savedEmployee = repository.save(savedEmployee);

        return mapToResponseDto(savedEmployee);
    }

    private EmployeeResponse mapToResponseDto(Employee employee) {

        return new EmployeeResponse(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getJoiningDate(),
                employee.getStatus(),
                employee.getCreatedOn()
        );
    }



    public EmpPaginationResponse getAll(
            Pageable pageable,
            String search,
            String department,
            String status
    ) {

        Page<Employee> page = repository.findAll(
                EmployeeSpecification.filterEmployees(
                        search,
                        department,
                        status
                ),
                pageable
        );

        List<EmployeeResponse> items = page.getContent()
                .stream()
                .map(this::mapToResponseDto)
                .toList();

        return new EmpPaginationResponse(
                items,
                page.getTotalElements()
        );
    }




    public EmployeeStatisticsResponse getEmployeeStatistics() {

        long totalEmployees = repository.count();

        long activeEmployees = repository.countByStatus("ACTIVE");

        long inactiveEmployees = repository.countByStatus("INACTIVE");

        return new EmployeeStatisticsResponse(
                totalEmployees,
                activeEmployees,
                inactiveEmployees
        );
    }


    public EmployeeResponse getById(Long id) {

        Employee employee = findByEmployeeId(id);

        return mapToResponseDto(employee);
    }

    private Employee findByEmployeeId(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public EmployeeResponse update(Long id, CreateEmployeeRequest request) {

        Employee employee = findByEmployeeId(id);

        // Check duplicate email
        if (repository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new DuplicateEmailException(request.getEmail());
        }

        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setDepartment(request.getDepartment());

        if (request.getJoiningDate() != null) {
            employee.setJoiningDate(request.getJoiningDate());
        }

        if (request.getStatus() != null) {
            employee.setStatus(request.getStatus());
        }

        Employee updatedEmployee = repository.save(employee);

        return mapToResponseDto(updatedEmployee);
    }

    public void delete(Long id) {

        Employee employee = findByEmployeeId(id);

        repository.delete(employee);
    }

    public void deleteALLEmployee() {

        repository.deleteAll();
    }
}
