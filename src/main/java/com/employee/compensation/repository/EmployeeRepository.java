package com.employee.compensation.repository;

import com.employee.compensation.config.DatabaseConfig;
import com.employee.compensation.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {

    private final String connectionUrl;
    private final String username;
    private final String password;

    public EmployeeRepository() {
        this.connectionUrl = DatabaseConfig.getConnectionUrl();
        this.username = DatabaseConfig.getUsername();
        this.password = DatabaseConfig.getPassword();
    }

    public List<Employee> getAllEmployees() throws SQLException {

        List<Employee> employees = new ArrayList<>();

        String sql = """
                SELECT
                    EmployeeId,
                    FirstName,
                    LastName,
                    DepartmentID,
                    Salary,
                    Bonus,
                    HireDate
                FROM Employee
                ORDER BY EmployeeId
                """;

        try (
                Connection connection = DriverManager.getConnection(
                        connectionUrl, username, password);
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                employees.add(mapEmployee(resultSet));
            }
        }

        return employees;
    }

    public List<Employee> getEmployeesByDepartment(int departmentId)
            throws SQLException {

        List<Employee> employees = new ArrayList<>();

        String sql = """
                SELECT
                    EmployeeId,
                    FirstName,
                    LastName,
                    DepartmentID,
                    Salary,
                    Bonus,
                    HireDate
                FROM Employee
                WHERE DepartmentID = ?
                ORDER BY EmployeeId
                """;

        try (
                Connection connection = DriverManager.getConnection(
                        connectionUrl, username, password);
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, departmentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    employees.add(mapEmployee(resultSet));
                }
            }
        }

        return employees;
    }

    public Employee getEmployeeById(int employeeId) throws SQLException {

        String sql = """
                SELECT
                    EmployeeId,
                    FirstName,
                    LastName,
                    DepartmentID,
                    Salary,
                    Bonus,
                    HireDate
                FROM Employee
                WHERE EmployeeId = ?
                """;

        try (
                Connection connection = DriverManager.getConnection(
                        connectionUrl, username, password);
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, employeeId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapEmployee(resultSet);
                }
            }
        }

        return null;
    }

    public Employee createEmployee(Employee employee) throws SQLException {

        String sql = """
                INSERT INTO Employee
                (FirstName, LastName, DepartmentID, Salary, Bonus, HireDate)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DriverManager.getConnection(
                        connectionUrl, username, password);
                PreparedStatement statement = connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS)
        ) {

            statement.setString(1, employee.getFirstName());
            statement.setString(2, employee.getLastName());
            statement.setInt(3, employee.getDepartmentId());
            statement.setBigDecimal(4, employee.getSalary());

            if (employee.getBonus() == null) {
                statement.setNull(5, Types.DECIMAL);
            } else {
                statement.setBigDecimal(5, employee.getBonus());
            }

            statement.setDate(
                    6,
                    Date.valueOf(employee.getHireDate())
            );

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    employee.setEmployeeId(keys.getInt(1));
                }
            }
        }

        return employee;
    }

    public boolean updateEmployee(Employee employee) throws SQLException {

        String sql = """
                UPDATE Employee
                SET
                    FirstName = ?,
                    LastName = ?,
                    DepartmentID = ?,
                    Salary = ?,
                    Bonus = ?,
                    HireDate = ?
                WHERE EmployeeId = ?
                """;

        try (
                Connection connection = DriverManager.getConnection(
                        connectionUrl, username, password);
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, employee.getFirstName());
            statement.setString(2, employee.getLastName());
            statement.setInt(3, employee.getDepartmentId());
            statement.setBigDecimal(4, employee.getSalary());

            if (employee.getBonus() == null) {
                statement.setNull(5, Types.DECIMAL);
            } else {
                statement.setBigDecimal(5, employee.getBonus());
            }

            statement.setDate(
                    6,
                    Date.valueOf(employee.getHireDate())
            );

            statement.setInt(7, employee.getEmployeeId());

            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteEmployee(int employeeId) throws SQLException {

        String sql = """
                DELETE FROM Employee
                WHERE EmployeeId = ?
                """;

        try (
                Connection connection = DriverManager.getConnection(
                        connectionUrl, username, password);
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, employeeId);

            return statement.executeUpdate() > 0;
        }
    }

    private Employee mapEmployee(ResultSet resultSet) throws SQLException {

        return new Employee(
                resultSet.getInt("EmployeeId"),
                resultSet.getString("FirstName"),
                resultSet.getString("LastName"),
                resultSet.getInt("DepartmentID"),
                resultSet.getBigDecimal("Salary"),
                resultSet.getBigDecimal("Bonus"),
                resultSet.getDate("HireDate").toLocalDate()
        );
    }
}
