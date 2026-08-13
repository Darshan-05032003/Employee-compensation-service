package com.employee.compensation.service;

import com.employee.compensation.repository.CompensationReportRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class CompensationReportService {

    private final CompensationReportRepository repository;

    public CompensationReportService() {
        this.repository = new CompensationReportRepository();
    }

    public BigDecimal getTotalBonus() throws SQLException {
        return repository.getTotalBonus();
    }

    public List<Map<String, Object>> getEmployeesWithoutBonus()
            throws SQLException {
        return repository.getEmployeesWithoutBonus();
    }

    public List<Map<String, Object>> getBonusPercentages()
            throws SQLException {
        return repository.getBonusPercentages();
    }

    public List<Map<String, Object>>
    getDepartmentsWhereBonusExceedsAverageSalary()
            throws SQLException {

        return repository
                .getDepartmentsWhereBonusExceedsAverageSalary();
    }

    public List<Map<String, Object>> getEmployeesRankedByBonus()
            throws SQLException {

        return repository.getEmployeesRankedByBonus();
    }

    public Map<String, Object> getHighestSalaryComparison()
            throws SQLException {

        return repository.getHighestSalaryComparison();
    }
}
