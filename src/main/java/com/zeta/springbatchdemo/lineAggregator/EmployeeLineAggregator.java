package com.zeta.springbatchdemo.lineAggregator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeta.springbatchdemo.model.Employee;
import lombok.SneakyThrows;
import org.springframework.batch.item.file.transform.LineAggregator;

import java.util.StringJoiner;

public class EmployeeLineAggregator implements LineAggregator<Employee> {

    @SneakyThrows
    @Override
    public String aggregate(Employee employee) {
        StringJoiner data = new StringJoiner("|");
        data.add(String.valueOf(employee.getId()))
                .add(employee.getFirstName())
                .add(employee.getLastName())
                .add(employee.getEmailAddress());
        return data.toString();

    }
}
