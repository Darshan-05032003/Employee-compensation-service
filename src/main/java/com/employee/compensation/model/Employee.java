package com.employee.compensation.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Employee {

    private int employeeId;
    private String firstName;
    private String lastName;
    private int departmentId;
    private String departmentName;
    private BigDecimal salary;
    private BigDecimal bonus;
    private LocalDate hireDate;

    public Employee() {
    }

    public Employee(
            int employeeId,
            String firstName,
            String lastName,
            int departmentId,
            BigDecimal salary,
            BigDecimal bonus,
            LocalDate hireDate) {

        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.departmentId = departmentId;
        this.salary = salary;
        this.bonus = bonus;
        this.hireDate = hireDate;
    }

    public Employee(
            int employeeId,
            String firstName,
            String lastName,
            int departmentId,
            String departmentName,
            BigDecimal salary,
            BigDecimal bonus,
            LocalDate hireDate) {

        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.salary = salary;
        this.bonus = bonus;
        this.hireDate = hireDate;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public BigDecimal getBonus() {
        return bonus;
    }

    public void setBonus(BigDecimal bonus) {
        this.bonus = bonus;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", salary=" + salary +
                ", bonus=" + bonus +
                ", hireDate=" + hireDate +
                '}';
    }
}
