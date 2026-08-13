CREATE TABLE Department (
    DepartmentID INT PRIMARY KEY IDENTITY(1,1),
    DepartmentName VARCHAR(100) NOT NULL,
    Location VARCHAR(100) NULL
);

CREATE TABLE Employee (
    EmployeeID INT PRIMARY KEY IDENTITY(1,1),
    FirstName VARCHAR(100) NOT NULL,
    LastName VARCHAR(100) NOT NULL,
    DepartmentID INT NOT NULL,
    Salary DECIMAL(15,2) NOT NULL,
    Bonus DECIMAL(15,2) NULL,
    HireDate DATE NOT NULL,

    CONSTRAINT FK_Employee_Department
        FOREIGN KEY (DepartmentID)
        REFERENCES Department(DepartmentID)
);
