package com.zeta.springbatchdemo.processor;

import com.zeta.springbatchdemo.model.Employee;
import org.springframework.batch.item.ItemProcessor;


public class UpperCaseItemProcessor implements ItemProcessor<Employee, Employee> {

    @Override
    public Employee process(Employee employee) throws Exception {
        return new Employee(employee.getId(),
                employee.getEmailAddress(),
                employee.getFirstName().toUpperCase(),
                employee.getLastName().toUpperCase());
    }
}
