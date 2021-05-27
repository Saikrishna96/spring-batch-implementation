package com.zeta.springbatchdemo.processor;

import com.zeta.springbatchdemo.model.User;
import org.springframework.batch.item.ItemProcessor;

import java.util.concurrent.atomic.AtomicInteger;


public class UpperCaseItemProcessor implements ItemProcessor<User, User> {

    AtomicInteger counter = new AtomicInteger(100001);
    @Override
    public User process(User user) throws Exception {
        System.out.println("processing : " + counter.get());
        return new User(counter.getAndIncrement(),
                user.getUserName(),
                user.getFirstName().toUpperCase(),
                user.getLastName().toUpperCase(),
                user.getGender(),
                user.getPassword(),
                user.getStatus());
    }
}
