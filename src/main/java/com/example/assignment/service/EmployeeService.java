package com.example.assignment.service;

import com.example.assignment.dto.*;
import com.example.assignment.entity.Department;
import com.example.assignment.entity.Employee;
import com.example.assignment.repository.DepartmentRepository;
import com.example.assignment.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeService(EmployeeRepository employeeRepository,
                           DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    public EmployeeResponse create(EmployeeCreateRequest request) {
        Department department = getDepartment(request.departmentId());
        Employee employee = new Employee(
                request.name().trim(), request.email().trim().toLowerCase(Locale.ROOT),
                department, request.salary());
        return toResponse(employeeRepository.save(employee));
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        return employeeRepository.findByIdWithDepartment(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + id));
    }

    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> search(String name, Long departmentId,
                                                 int page, int size, String sortBy, String direction) {
        if (page < 0) throw new IllegalArgumentException("page must be zero or greater");
        if (size < 1 || size > 100) throw new IllegalArgumentException("size must be between 1 and 100");

        String safeSortBy = switch (sortBy == null ? "id" : sortBy) {
            case "id", "name", "email", "salary" -> sortBy == null ? "id" : sortBy;
            default -> "id";
        };
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, safeSortBy));

        Specification<Employee> spec = (root, query, cb) -> cb.conjunction();
        if (name != null && !name.isBlank()) {
            String pattern = "%" + name.trim().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), pattern));
        }
        if (departmentId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("department").get("id"), departmentId));
        }

        Page<EmployeeResponse> result = employeeRepository.findAll(spec, pageable).map(this::toResponse);
        return PageResponse.from(result);
    }

    public EmployeeResponse update(Long id, EmployeeUpdateRequest request) {
        Employee employee = findEmployee(id);
        employee.setName(request.name().trim());
        employee.setEmail(request.email().trim().toLowerCase(Locale.ROOT));
        employee.setDepartment(getDepartment(request.departmentId()));
        employee.setSalary(request.salary());
        return toResponse(employeeRepository.save(employee));
    }

    public EmployeeResponse patch(Long id, EmployeePatchRequest request) {
        Employee employee = findEmployee(id);
        if (request.name() != null) {
            if (request.name().isBlank()) throw new IllegalArgumentException("name cannot be blank");
            employee.setName(request.name().trim());
        }
        if (request.email() != null) {
            employee.setEmail(request.email().trim().toLowerCase(Locale.ROOT));
        }
        if (request.departmentId() != null) {
            employee.setDepartment(getDepartment(request.departmentId()));
        }
        if (request.salary() != null) {
            if (request.salary().signum() < 0) throw new IllegalArgumentException("salary cannot be negative");
            employee.setSalary(request.salary());
        }
        return toResponse(employeeRepository.save(employee));
    }

    public void delete(Long id) {
        Employee employee = findEmployee(id);
        employeeRepository.delete(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeDepartmentView> findEmployeesWithDepartment() {
        return employeeRepository.findEmployeesWithDepartment();
    }

    private Employee findEmployee(Long id) {
        return employeeRepository.findByIdWithDepartment(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + id));
    }

    private Department getDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found: " + id));
    }

    private EmployeeResponse toResponse(Employee employee) {
        Department department = employee.getDepartment();
        return new EmployeeResponse(employee.getId(), employee.getName(), employee.getEmail(),
                department.getId(), department.getName(), employee.getSalary());
    }
}
