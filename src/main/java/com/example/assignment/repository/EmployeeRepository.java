package com.example.assignment.repository;

import com.example.assignment.dto.EmployeeDepartmentView;
import com.example.assignment.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    @Query("""
        select new com.example.assignment.dto.EmployeeDepartmentView(
            e.id, e.name, e.email, d.name
        )
        from Employee e
        join e.department d
        order by e.id
        """)
    java.util.List<EmployeeDepartmentView> findEmployeesWithDepartment();

    @EntityGraph(attributePaths = "department")
    @Query("select e from Employee e where e.id = :id")
    java.util.Optional<Employee> findByIdWithDepartment(@Param("id") Long id);

    @Override
    @EntityGraph(attributePaths = "department")
    Page<Employee> findAll(org.springframework.data.jpa.domain.Specification<Employee> spec, Pageable pageable);
}
