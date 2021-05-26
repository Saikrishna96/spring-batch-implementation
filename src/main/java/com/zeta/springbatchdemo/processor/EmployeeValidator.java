package com.zeta.springbatchdemo.processor;

import com.zeta.springbatchdemo.model.Employee;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

public class EmployeeValidator implements Validator<Employee> {

    @Override
    public void validate(Employee employee) throws ValidationException {
        if (!employee.getEmailAddress().contains("@gmail.com"))
            throw new ValidationException("Incorrect email format in : " + employee);
    }

}
