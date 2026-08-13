package com.employee.compensation.functions;

import com.employee.compensation.model.Employee;
import com.employee.compensation.service.CompensationReportService;
import com.employee.compensation.service.EmployeeService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EmployeeFunction {

    private final EmployeeService employeeService;
    private final CompensationReportService compensationReportService;

    public EmployeeFunction() {
        this.employeeService = new EmployeeService();
        this.compensationReportService = new CompensationReportService();
    }

    // ============================================================
    // HELLO
    // ============================================================

    @FunctionName("helloEmployee")
    public HttpResponseMessage helloEmployee(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = com.microsoft.azure.functions.annotation.AuthorizationLevel.ANONYMOUS,
                    route = "helloEmployee"
            )
            HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {

        return request
                .createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "text/plain")
                .body("Hello Employee!")
                .build();
    }

    // ============================================================
    // GET ALL EMPLOYEES + OPTIONAL DEPARTMENT FILTER
    // ============================================================

    @FunctionName("getEmployees")
    public HttpResponseMessage getEmployees(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = com.microsoft.azure.functions.annotation.AuthorizationLevel.ANONYMOUS,
                    route = "employees"
            )
            HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {

        try {

            String departmentIdParam =
                    request.getQueryParameters().get("departmentId");

            List<Employee> employees;

            if (departmentIdParam != null
                    && !departmentIdParam.isBlank()) {

                int departmentId;

                try {
                    departmentId =
                            Integer.parseInt(departmentIdParam);
                } catch (NumberFormatException e) {

                    return request
                            .createResponseBuilder(
                                    HttpStatus.BAD_REQUEST)
                            .body("departmentId must be a number.")
                            .build();
                }

                employees =
                        employeeService.getEmployeesByDepartment(
                                departmentId);

            } else {

                employees =
                        employeeService.getAllEmployees();
            }

            return request
                    .createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(toJsonArray(employees))
                    .build();

        } catch (Exception e) {

            logError(context, "getEmployees", e);

            return request
                    .createResponseBuilder(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving employees: "
                            + e.getMessage())
                    .build();
        }
    }

    // ============================================================
    // GET EMPLOYEE BY ID
    // ============================================================

    @FunctionName("getEmployeeById")
    public HttpResponseMessage getEmployeeById(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = com.microsoft.azure.functions.annotation.AuthorizationLevel.ANONYMOUS,
                    route = "employees/{id}"
            )
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            ExecutionContext context) {

        try {

            int employeeId;

            try {
                employeeId = Integer.parseInt(id);
            } catch (NumberFormatException e) {

                return request
                        .createResponseBuilder(
                                HttpStatus.BAD_REQUEST)
                        .body("Employee ID must be a number.")
                        .build();
            }

            Employee employee =
                    employeeService.getEmployeeById(employeeId);

            if (employee == null) {

                return request
                        .createResponseBuilder(
                                HttpStatus.NOT_FOUND)
                        .body("Employee not found.")
                        .build();
            }

            return request
                    .createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(toJson(employee))
                    .build();

        } catch (Exception e) {

            logError(context, "getEmployeeById", e);

            return request
                    .createResponseBuilder(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving employee: "
                            + e.getMessage())
                    .build();
        }
    }

    // ============================================================
    // CREATE EMPLOYEE
    // ============================================================

    @FunctionName("createEmployee")
    public HttpResponseMessage createEmployee(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST},
                    authLevel = com.microsoft.azure.functions.annotation.AuthorizationLevel.ANONYMOUS,
                    route = "employees"
            )
            HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {

        try {

            if (request.getBody().isEmpty()) {

                return request
                        .createResponseBuilder(
                                HttpStatus.BAD_REQUEST)
                        .body("Request body is required.")
                        .build();
            }

            Employee employee =
                    parseEmployee(request.getBody().get());

            Employee created =
                    employeeService.createEmployee(employee);

            return request
                    .createResponseBuilder(
                            HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(toJson(created))
                    .build();

        } catch (IllegalArgumentException e) {

            return request
                    .createResponseBuilder(
                            HttpStatus.BAD_REQUEST)
                    .body(e.getMessage())
                    .build();

        } catch (Exception e) {

            logError(context, "createEmployee", e);

            return request
                    .createResponseBuilder(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating employee: "
                            + e.getMessage())
                    .build();
        }
    }

    // ============================================================
    // UPDATE EMPLOYEE
    // ============================================================

    @FunctionName("updateEmployee")
    public HttpResponseMessage updateEmployee(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.PUT},
                    authLevel = com.microsoft.azure.functions.annotation.AuthorizationLevel.ANONYMOUS,
                    route = "employees/{id}"
            )
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            ExecutionContext context) {

        try {

            int employeeId;

            try {
                employeeId = Integer.parseInt(id);
            } catch (NumberFormatException e) {

                return request
                        .createResponseBuilder(
                                HttpStatus.BAD_REQUEST)
                        .body("Employee ID must be a number.")
                        .build();
            }

            if (request.getBody().isEmpty()) {

                return request
                        .createResponseBuilder(
                                HttpStatus.BAD_REQUEST)
                        .body("Request body is required.")
                        .build();
            }

            Employee employee =
                    parseEmployee(request.getBody().get());

            employee.setEmployeeId(employeeId);

            boolean updated =
                    employeeService.updateEmployee(employee);

            if (!updated) {

                return request
                        .createResponseBuilder(
                                HttpStatus.NOT_FOUND)
                        .body("Employee not found.")
                        .build();
            }

            Employee updatedEmployee =
                    employeeService.getEmployeeById(employeeId);

            return request
                    .createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(toJson(updatedEmployee))
                    .build();

        } catch (IllegalArgumentException e) {

            return request
                    .createResponseBuilder(
                            HttpStatus.BAD_REQUEST)
                    .body(e.getMessage())
                    .build();

        } catch (Exception e) {

            logError(context, "updateEmployee", e);

            return request
                    .createResponseBuilder(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating employee: "
                            + e.getMessage())
                    .build();
        }
    }

    // ============================================================
    // DELETE EMPLOYEE
    // ============================================================

    @FunctionName("deleteEmployee")
    public HttpResponseMessage deleteEmployee(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.DELETE},
                    authLevel = com.microsoft.azure.functions.annotation.AuthorizationLevel.ANONYMOUS,
                    route = "employees/{id}"
            )
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            ExecutionContext context) {

        try {

            int employeeId;

            try {
                employeeId = Integer.parseInt(id);
            } catch (NumberFormatException e) {

                return request
                        .createResponseBuilder(
                                HttpStatus.BAD_REQUEST)
                        .body("Employee ID must be a number.")
                        .build();
            }

            boolean deleted =
                    employeeService.deleteEmployee(employeeId);

            if (!deleted) {

                return request
                        .createResponseBuilder(
                                HttpStatus.NOT_FOUND)
                        .body("Employee not found.")
                        .build();
            }

            return request
                    .createResponseBuilder(
                            HttpStatus.NO_CONTENT)
                    .build();

        } catch (Exception e) {

            logError(context, "deleteEmployee", e);

            return request
                    .createResponseBuilder(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting employee: "
                            + e.getMessage())
                    .build();
        }
    }

    // ============================================================
    // REPORT 1 - TOTAL BONUS
    // ============================================================

    @FunctionName("getTotalBonus")
    public HttpResponseMessage getTotalBonus(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = com.microsoft.azure.functions.annotation.AuthorizationLevel.ANONYMOUS,
                    route = "reports/total-bonus"
            )
            HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {

        try {

            BigDecimal totalBonus =
                    compensationReportService.getTotalBonus();

            return request
                    .createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(
                            "{\"totalBonus\":"
                                    + totalBonus
                                    + "}"
                    )
                    .build();

        } catch (Exception e) {

            logError(context, "getTotalBonus", e);

            return request
                    .createResponseBuilder(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating total bonus: "
                            + e.getMessage())
                    .build();
        }
    }

    // ============================================================
    // REPORT 2 - EMPLOYEES WITHOUT BONUS
    // ============================================================

    @FunctionName("getEmployeesWithoutBonus")
    public HttpResponseMessage getEmployeesWithoutBonus(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = com.microsoft.azure.functions.annotation.AuthorizationLevel.ANONYMOUS,
                    route = "reports/no-bonus"
            )
            HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {

        try {

            List<Map<String, Object>> employees =
                    compensationReportService
                            .getEmployeesWithoutBonus();

            return request
                    .createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(toJsonListOfMaps(employees))
                    .build();

        } catch (Exception e) {

            logError(
                    context,
                    "getEmployeesWithoutBonus",
                    e);

            return request
                    .createResponseBuilder(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving employees without bonus: "
                            + e.getMessage())
                    .build();
        }
    }

    // ============================================================
    // REPORT 3 - BONUS PERCENTAGES
    // ============================================================

    @FunctionName("getBonusPercentages")
    public HttpResponseMessage getBonusPercentages(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = com.microsoft.azure.functions.annotation.AuthorizationLevel.ANONYMOUS,
                    route = "reports/bonus-percentages"
            )
            HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {

        try {

            List<Map<String, Object>> employees =
                    compensationReportService
                            .getBonusPercentages();

            return request
                    .createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(toJsonListOfMaps(employees))
                    .build();

        } catch (Exception e) {

            logError(
                    context,
                    "getBonusPercentages",
                    e);

            return request
                    .createResponseBuilder(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating bonus percentages: "
                            + e.getMessage())
                    .build();
        }
    }

    // ============================================================
    // REPORT 4 - DEPARTMENTS WHERE BONUS > AVERAGE SALARY
    // ============================================================

    @FunctionName("getDepartmentsBonusExceedsAverage")
    public HttpResponseMessage
    getDepartmentsBonusExceedsAverage(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = com.microsoft.azure.functions.annotation.AuthorizationLevel.ANONYMOUS,
                    route = "reports/departments-bonus-exceeds-average"
            )
            HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {

        try {

            List<Map<String, Object>> departments =
                    compensationReportService
                            .getDepartmentsWhereBonusExceedsAverageSalary();

            return request
                    .createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(toJsonListOfMaps(departments))
                    .build();

        } catch (Exception e) {

            logError(
                    context,
                    "getDepartmentsBonusExceedsAverage",
                    e);

            return request
                    .createResponseBuilder(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving department report: "
                            + e.getMessage())
                    .build();
        }
    }

    // ============================================================
    // REPORT 5 - EMPLOYEES RANKED BY BONUS
    // ============================================================

    @FunctionName("getEmployeesRankedByBonus")
    public HttpResponseMessage getEmployeesRankedByBonus(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = com.microsoft.azure.functions.annotation.AuthorizationLevel.ANONYMOUS,
                    route = "reports/bonus-ranking"
            )
            HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {

        try {

            List<Map<String, Object>> employees =
                    compensationReportService
                            .getEmployeesRankedByBonus();

            return request
                    .createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(toJsonListOfMaps(employees))
                    .build();

        } catch (Exception e) {

            logError(
                    context,
                    "getEmployeesRankedByBonus",
                    e);

            return request
                    .createResponseBuilder(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating bonus ranking: "
                            + e.getMessage())
                    .build();
        }
    }

    // ============================================================
    // REPORT 6 - HIGHEST SALARY VS HIGHEST COMPENSATION
    // ============================================================

    @FunctionName("getHighestSalaryComparison")
    public HttpResponseMessage getHighestSalaryComparison(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = com.microsoft.azure.functions.annotation.AuthorizationLevel.ANONYMOUS,
                    route = "reports/highest-salary"
            )
            HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {

        try {

            Map<String, Object> result =
                    compensationReportService
                            .getHighestSalaryComparison();

            return request
                    .createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(toJsonObject(result))
                    .build();

        } catch (Exception e) {

            logError(
                    context,
                    "getHighestSalaryComparison",
                    e);

            return request
                    .createResponseBuilder(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating salary comparison: "
                            + e.getMessage())
                    .build();
        }
    }

    // ============================================================
    // EMPLOYEE REQUEST PARSER
    // ============================================================

    private Employee parseEmployee(String body) {

        String firstName =
                extractString(body, "firstName");

        String lastName =
                extractString(body, "lastName");

        int departmentId =
                Integer.parseInt(
                        extractString(body, "departmentId"));

        BigDecimal salary =
                new BigDecimal(
                        extractString(body, "salary"));

        String bonusValue =
                extractNullableString(body, "bonus");

        BigDecimal bonus = null;

        if (bonusValue != null
                && !bonusValue.isBlank()
                && !bonusValue.equalsIgnoreCase("null")) {

            bonus = new BigDecimal(bonusValue);
        }

        LocalDate hireDate =
                LocalDate.parse(
                        extractString(body, "hireDate"));

        return new Employee(
                0,
                firstName,
                lastName,
                departmentId,
                salary,
                bonus,
                hireDate
        );
    }

    private static String extractString(
            String json,
            String key) {

        String value =
                extractNullableString(json, key);

        if (value == null || value.isBlank()) {

            throw new IllegalArgumentException(
                    key + " is required.");
        }

        return value;
    }

    private static String extractNullableString(
            String json,
            String key) {

        String search =
                "\"" + key + "\"";

        int keyPosition =
                json.indexOf(search);

        if (keyPosition < 0) {
            return null;
        }

        int colon =
                json.indexOf(
                        ":",
                        keyPosition);

        if (colon < 0) {
            return null;
        }

        int start =
                colon + 1;

        while (start < json.length()
                && Character.isWhitespace(
                        json.charAt(start))) {

            start++;
        }

        if (start >= json.length()) {
            return null;
        }

        if (json.charAt(start) == '"') {

            int end =
                    json.indexOf(
                            '"',
                            start + 1);

            if (end < 0) {
                return null;
            }

            return json.substring(
                    start + 1,
                    end);
        }

        int end = start;

        while (end < json.length()
                && json.charAt(end) != ','
                && json.charAt(end) != '}') {

            end++;
        }

        return json.substring(
                start,
                end)
                .trim();
    }

    // ============================================================
    // JSON HELPERS
    // ============================================================

    private static String toJsonArray(
            List<Employee> employees) {

        StringBuilder json =
                new StringBuilder("[");

        for (int i = 0;
             i < employees.size();
             i++) {

            json.append(
                    toJson(
                            employees.get(i)));

            if (i < employees.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");

        return json.toString();
    }

    private static String toJson(
            Employee employee) {

        return "{"
                + "\"employeeId\":"
                + employee.getEmployeeId()
                + ","
                + "\"firstName\":\""
                + escape(employee.getFirstName())
                + "\","
                + "\"lastName\":\""
                + escape(employee.getLastName())
                + "\","
                + "\"departmentId\":"
                + employee.getDepartmentId()
                + ","
                + "\"salary\":"
                + employee.getSalary()
                + ","
                + "\"bonus\":"
                + (employee.getBonus() == null
                    ? "null"
                    : employee.getBonus())
                + ","
                + "\"hireDate\":\""
                + employee.getHireDate()
                + "\""
                + "}";
    }

    private static String toJsonListOfMaps(
            List<Map<String, Object>> items) {

        StringBuilder json =
                new StringBuilder("[");

        for (int i = 0;
             i < items.size();
             i++) {

            json.append(
                    toJsonObject(items.get(i)));

            if (i < items.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");

        return json.toString();
    }

    private static String toJsonObject(
            Map<String, Object> map) {

        StringBuilder json =
                new StringBuilder("{");

        int index = 0;

        for (Map.Entry<String, Object> entry :
                map.entrySet()) {

            json.append("\"")
                    .append(escape(entry.getKey()))
                    .append("\":")
                    .append(toJsonValue(entry.getValue()));

            if (index < map.size() - 1) {
                json.append(",");
            }

            index++;
        }

        json.append("}");

        return json.toString();
    }

    private static String toJsonValue(Object value) {

        if (value == null) {
            return "null";
        }

        if (value instanceof String
                || value instanceof LocalDate) {

            return "\""
                    + escape(value.toString())
                    + "\"";
        }

        if (value instanceof Number
                || value instanceof Boolean) {

            return value.toString();
        }

        if (value instanceof Map<?, ?>) {

            @SuppressWarnings("unchecked")
            Map<String, Object> nested =
                    (Map<String, Object>) value;

            return toJsonObject(nested);
        }

        return "\""
                + escape(value.toString())
                + "\"";
    }

    private static String escape(
            String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    // ============================================================
    // ERROR LOGGING
    // ============================================================

    private static void logError(
            ExecutionContext context,
            String operation,
            Exception e) {

        context.getLogger().severe(
                "ERROR in "
                        + operation
                        + ": "
                        + e.getClass().getName()
                        + " - "
                        + e.getMessage()
        );
    }
}
