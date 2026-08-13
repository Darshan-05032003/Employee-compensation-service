package com.employee.compensation.service;

import com.employee.compensation.model.Employee;
import com.employee.compensation.repository.EmployeeRepository;

import java.sql.SQLException;
import java.util.List;

public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService() {
        this.employeeRepository = new EmployeeRepository();
    }

    public List<Employee> getAllEmployees() throws SQLException {
        return employeeRepository.getAllEmployees();
    }

    public List<Employee> getEmployeesByDepartment(int departmentId)
            throws SQLException {
        return employeeRepository.getEmployeesByDepartment(departmentId);
    }

    public Employee getEmployeeById(int employeeId) throws SQLException {
        return employeeRepository.getEmployeeById(employeeId);
    }

    public Employee createEmployee(Employee employee) throws SQLException {

        validateEmployee(employee);

        return employeeRepository.createEmployee(employee);
    }

    public boolean updateEmployee(Employee employee) throws SQLException {

        validateEmployee(employee);

        return employeeRepository.updateEmployee(employee);
    }

    public boolean deleteEmployee(int employeeId) throws SQLException {
        return employeeRepository.deleteEmployee(employeeId);
    }

    private void validateEmployee(Employee employee) {

        if (employee == null) {
            throw new IllegalArgumentException("Employee is required.");
        }

        if (employee.getFirstName() == null
                || employee.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required.");
        }

        if (employee.getLastName() == null
                || employee.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required.");
        }

        if (employee.getDepartmentId() <= 0) {
            throw new IllegalArgumentException(
                    "Department ID must be greater than zero.");
        }

        if (employee.getSalary() == null
                || employee.getSalary().signum() < 0) {
            throw new IllegalArgumentException(
                    "Salary must be zero or greater.");
        }

        if (employee.getBonus() != null
                && employee.getBonus().signum() < 0) {
            throw new IllegalArgumentException(
                    "Bonus must be zero or greater.");
        }

        if (employee.getHireDate() == null) {
            throw new IllegalArgumentException("Hire date is required.");
        }
    }
}
