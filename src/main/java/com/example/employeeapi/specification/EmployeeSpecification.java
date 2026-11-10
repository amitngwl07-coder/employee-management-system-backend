
package com.example.employeeapi.specification;

import com.example.employeeapi.entity.Employee;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeSpecification {

    public static Specification<Employee> filterEmployees(
            String search,
            String department,
            String status
    ) {

        return (root, query, criteriaBuilder) -> {

            var predicates = criteriaBuilder.conjunction();

            // Search across employee code, name, email and department
            if (search != null && !search.isBlank()) {

                String keyword = "%" + search.trim().toLowerCase() + "%";

                var searchPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("employeeCode")),
                                keyword
                        ),
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("name")),
                                keyword
                        ),
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("email")),
                                keyword
                        ),
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("department")),
                                keyword
                        )
                );

                predicates = criteriaBuilder.and(
                        predicates,
                        searchPredicate
                );
            }

            // Department filter
            if (department != null && !department.isBlank()) {

                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.equal(
                                criteriaBuilder.lower(root.get("department")),
                                department.trim().toLowerCase()
                        )
                );
            }

            // Status filter
            if (status != null && !status.isBlank()) {

                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.equal(
                                criteriaBuilder.upper(root.get("status")),
                                status.trim().toUpperCase()
                        )
                );
            }

            return predicates;
        };
    }
}
