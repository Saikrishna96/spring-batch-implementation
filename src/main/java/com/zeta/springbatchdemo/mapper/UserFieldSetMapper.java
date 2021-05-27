package com.zeta.springbatchdemo.mapper;

import com.zeta.springbatchdemo.model.User;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class UserFieldSetMapper implements FieldSetMapper<User> {

    @Override
    public User mapFieldSet(FieldSet fieldSet) throws BindException {
        return new User(fieldSet.readInt("userId"),
                fieldSet.readString("userName"),
                fieldSet.readString("firstName"),
                fieldSet.readString("lastName"),
                fieldSet.readString("gender"),
                fieldSet.readString("password"),
                fieldSet.readInt("status"));
    }
}
