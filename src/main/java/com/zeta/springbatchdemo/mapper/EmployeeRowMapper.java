package com.zeta.springbatchdemo.mapper;


import com.zeta.springbatchdemo.model.Employee;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeRowMapper implements RowMapper<Employee> {

    @Override
    public Employee mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Employee(resultSet.getInt("id"),
                resultSet.getString("email_address"),
                resultSet.getString("first_name"),
                resultSet.getString("lastname"));
    }
}
