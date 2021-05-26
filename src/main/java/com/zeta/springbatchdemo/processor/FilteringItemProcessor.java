package com.zeta.springbatchdemo.processor;

import com.zeta.springbatchdemo.model.Employee;
import org.springframework.batch.item.ItemProcessor;

public class FilteringItemProcessor implements ItemProcessor<Employee, Employee> {
    @Override
    public Employee process(Employee employee) throws Exception {
        if (employee.getId() % 2 == 0)
            return null;
        else
            return employee;
    }
}
