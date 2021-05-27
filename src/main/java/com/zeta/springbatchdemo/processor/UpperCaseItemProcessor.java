package com.zeta.springbatchdemo.processor;

import com.zeta.springbatchdemo.model.User;
import org.springframework.batch.item.ItemProcessor;


public class UpperCaseItemProcessor implements ItemProcessor<User, User> {

    @Override
    public User process(User user) throws Exception {

        return new User(user.getUserId(),
                user.getUserName(),
                user.getFirstName().toUpperCase(),
                user.getLastName().toUpperCase(),
                user.getGender(),
                user.getPassword(),
                user.getStatus());
    }
}
