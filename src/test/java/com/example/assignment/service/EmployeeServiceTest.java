package com.example.assignment.service;

import com.example.assignment.dto.EmployeeCreateRequest;
import com.example.assignment.dto.EmployeeResponse;
import com.example.assignment.entity.Department;
import com.example.assignment.entity.Employee;
import com.example.assignment.repository.DepartmentRepository;
import com.example.assignment.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void createShouldSaveEmployeeAndReturnResponse() {
        Department department = new Department("Engineering");
        // department lookup result.
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee saved = invocation.getArgument(0);
            return saved;
        });

        EmployeeCreateRequest request = new EmployeeCreateRequest(
                "Amina Khan", "AMINA@example.com", 1L, new BigDecimal("6500.00"));

        EmployeeResponse response = employeeService.create(request);

        assertEquals("Amina Khan", response.name());
        assertEquals("amina@example.com", response.email());
        assertEquals("Engineering", response.departmentName());
        assertEquals(new BigDecimal("6500.00"), response.salary());

        verify(employeeRepository).save(captor.capture());
        assertEquals("amina@example.com", captor.getValue().getEmail());
    }

    @Test
    void getByIdShouldThrowWhenEmployeeDoesNotExist() {
        when(employeeRepository.findByIdWithDepartment(800L)).thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class,
                () -> employeeService.getById(800L));
    }
}
