package com.employee.compensation.repository;

import com.employee.compensation.config.DatabaseConfig;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CompensationReportRepository {

    private final String connectionUrl;
    private final String username;
    private final String password;

    public CompensationReportRepository() {
        this.connectionUrl = DatabaseConfig.getConnectionUrl();
        this.username = DatabaseConfig.getUsername();
        this.password = DatabaseConfig.getPassword();
    }

    public BigDecimal getTotalBonus() throws SQLException {

        String sql = """
                SELECT COALESCE(SUM(COALESCE(Bonus, 0)), 0) AS TotalBonus
                FROM Employee
                """;

        try (
                Connection connection = DriverManager.getConnection(
                        connectionUrl, username, password);
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            if (resultSet.next()) {
                return resultSet.getBigDecimal("TotalBonus");
            }
        }

        return BigDecimal.ZERO;
    }

    public List<Map<String, Object>> getEmployeesWithoutBonus()
            throws SQLException {

        List<Map<String, Object>> employees = new ArrayList<>();

        String sql = """
                SELECT
                    EmployeeId,
                    FirstName,
                    LastName,
                    DepartmentID,
                    Salary,
                    HireDate
                FROM Employee
                WHERE Bonus IS NULL
                ORDER BY EmployeeId
                """;

        try (
                Connection connection = DriverManager.getConnection(
                        connectionUrl, username, password);
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {

                Map<String, Object> employee = new LinkedHashMap<>();

                employee.put(
                        "employeeId",
                        resultSet.getInt("EmployeeId"));

                employee.put(
                        "firstName",
                        resultSet.getString("FirstName"));

                employee.put(
                        "lastName",
                        resultSet.getString("LastName"));

                employee.put(
                        "departmentId",
                        resultSet.getInt("DepartmentID"));

                employee.put(
                        "salary",
                        resultSet.getBigDecimal("Salary"));

                employee.put(
                        "hireDate",
                        resultSet.getDate("HireDate").toLocalDate());

                employees.add(employee);
            }
        }

        return employees;
    }

    public List<Map<String, Object>> getBonusPercentages()
            throws SQLException {

        List<Map<String, Object>> employees = new ArrayList<>();

        String sql = """
                SELECT
                    EmployeeId,
                    FirstName,
                    LastName,
                    Salary,
                    Bonus,
                    CAST(
                        (Bonus * 100.0) / NULLIF(Salary, 0)
                        AS DECIMAL(12, 2)
                    ) AS BonusPercentage
                FROM Employee
                WHERE Bonus IS NOT NULL
                ORDER BY EmployeeId
                """;

        try (
                Connection connection = DriverManager.getConnection(
                        connectionUrl, username, password);
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {

                Map<String, Object> employee = new LinkedHashMap<>();

                employee.put(
                        "employeeId",
                        resultSet.getInt("EmployeeId"));

                employee.put(
                        "firstName",
                        resultSet.getString("FirstName"));

                employee.put(
                        "lastName",
                        resultSet.getString("LastName"));

                employee.put(
                        "salary",
                        resultSet.getBigDecimal("Salary"));

                employee.put(
                        "bonus",
                        resultSet.getBigDecimal("Bonus"));

                employee.put(
                        "bonusPercentage",
                        resultSet.getBigDecimal("BonusPercentage"));

                employees.add(employee);
            }
        }

        return employees;
    }

    public List<Map<String, Object>> getDepartmentsWhereBonusExceedsAverageSalary()
            throws SQLException {

        List<Map<String, Object>> departments = new ArrayList<>();

        String sql = """
                SELECT
                    d.DepartmentID,
                    d.DepartmentName,
                    SUM(COALESCE(e.Bonus, 0)) AS TotalBonus,
                    AVG(e.Salary) AS AverageSalary
                FROM Department d
                INNER JOIN Employee e
                    ON d.DepartmentID = e.DepartmentID
                GROUP BY
                    d.DepartmentID,
                    d.DepartmentName
                HAVING
                    SUM(COALESCE(e.Bonus, 0)) > AVG(e.Salary)
                ORDER BY d.DepartmentID
                """;

        try (
                Connection connection = DriverManager.getConnection(
                        connectionUrl, username, password);
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {

                Map<String, Object> department = new LinkedHashMap<>();

                department.put(
                        "departmentId",
                        resultSet.getInt("DepartmentID"));

                department.put(
                        "departmentName",
                        resultSet.getString("DepartmentName"));

                department.put(
                        "totalBonus",
                        resultSet.getBigDecimal("TotalBonus"));

                department.put(
                        "averageSalary",
                        resultSet.getBigDecimal("AverageSalary"));

                departments.add(department);
            }
        }

        return departments;
    }

    public List<Map<String, Object>> getEmployeesRankedByBonus()
            throws SQLException {

        List<Map<String, Object>> employees = new ArrayList<>();

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
                ORDER BY
                    CASE
                        WHEN Bonus IS NULL THEN 1
                        ELSE 0
                    END,
                    Bonus DESC,
                    EmployeeId
                """;

        try (
                Connection connection = DriverManager.getConnection(
                        connectionUrl, username, password);
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            int rank = 1;

            while (resultSet.next()) {

                Map<String, Object> employee = new LinkedHashMap<>();

                employee.put("rank", rank++);

                employee.put(
                        "employeeId",
                        resultSet.getInt("EmployeeId"));

                employee.put(
                        "firstName",
                        resultSet.getString("FirstName"));

                employee.put(
                        "lastName",
                        resultSet.getString("LastName"));

                employee.put(
                        "departmentId",
                        resultSet.getInt("DepartmentID"));

                employee.put(
                        "salary",
                        resultSet.getBigDecimal("Salary"));

                employee.put(
                        "bonus",
                        resultSet.getBigDecimal("Bonus"));

                employee.put(
                        "hireDate",
                        resultSet.getDate("HireDate").toLocalDate());

                employees.add(employee);
            }
        }

        return employees;
    }

    public Map<String, Object> getHighestSalaryComparison()
            throws SQLException {

        Map<String, Object> result = new LinkedHashMap<>();

        String highestSalarySql = """
                SELECT TOP 1
                    EmployeeId,
                    FirstName,
                    LastName,
                    Salary,
                    Bonus
                FROM Employee
                ORDER BY Salary DESC, EmployeeId
                """;

        String highestTotalCompensationSql = """
                SELECT TOP 1
                    EmployeeId,
                    FirstName,
                    LastName,
                    Salary,
                    Bonus,
                    (Salary + COALESCE(Bonus, 0)) AS TotalCompensation
                FROM Employee
                ORDER BY
                    (Salary + COALESCE(Bonus, 0)) DESC,
                    EmployeeId
                """;

        Map<String, Object> highestSalaryEmployee =
                new LinkedHashMap<>();

        Map<String, Object> highestTotalCompensationEmployee =
                new LinkedHashMap<>();

        try (
                Connection connection = DriverManager.getConnection(
                        connectionUrl, username, password);
                PreparedStatement salaryStatement =
                        connection.prepareStatement(highestSalarySql);
                PreparedStatement totalCompensationStatement =
                        connection.prepareStatement(
                                highestTotalCompensationSql)
        ) {

            try (ResultSet resultSet =
                         salaryStatement.executeQuery()) {

                if (resultSet.next()) {

                    highestSalaryEmployee.put(
                            "employeeId",
                            resultSet.getInt("EmployeeId"));

                    highestSalaryEmployee.put(
                            "firstName",
                            resultSet.getString("FirstName"));

                    highestSalaryEmployee.put(
                            "lastName",
                            resultSet.getString("LastName"));

                    highestSalaryEmployee.put(
                            "salary",
                            resultSet.getBigDecimal("Salary"));

                    highestSalaryEmployee.put(
                            "bonus",
                            resultSet.getBigDecimal("Bonus"));
                }
            }

            try (ResultSet resultSet =
                         totalCompensationStatement.executeQuery()) {

                if (resultSet.next()) {

                    highestTotalCompensationEmployee.put(
                            "employeeId",
                            resultSet.getInt("EmployeeId"));

                    highestTotalCompensationEmployee.put(
                            "firstName",
                            resultSet.getString("FirstName"));

                    highestTotalCompensationEmployee.put(
                            "lastName",
                            resultSet.getString("LastName"));

                    highestTotalCompensationEmployee.put(
                            "salary",
                            resultSet.getBigDecimal("Salary"));

                    highestTotalCompensationEmployee.put(
                            "bonus",
                            resultSet.getBigDecimal("Bonus"));

                    highestTotalCompensationEmployee.put(
                            "totalCompensation",
                            resultSet.getBigDecimal(
                                    "TotalCompensation"));
                }
            }
        }

        boolean samePerson =
                !highestSalaryEmployee.isEmpty()
                        && !highestTotalCompensationEmployee.isEmpty()
                        && highestSalaryEmployee
                        .get("employeeId")
                        .equals(
                                highestTotalCompensationEmployee
                                        .get("employeeId"));

        result.put(
                "highestBaseSalaryEmployee",
                highestSalaryEmployee);

        result.put(
                "highestTotalCompensationEmployee",
                highestTotalCompensationEmployee);

        result.put(
                "samePerson",
                samePerson);

        return result;
    }
}
