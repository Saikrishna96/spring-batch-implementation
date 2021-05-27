package com.zeta.springbatchdemo.processor;

import com.zeta.springbatchdemo.model.User;
import org.springframework.batch.item.ItemProcessor;

import java.util.concurrent.atomic.AtomicInteger;

// NOTE can do any processing here
public class UpperCaseItemProcessor implements ItemProcessor<User, User> {

//    NOTE used this counter for adding more entries to db table user_details
//    AtomicInteger counter = new AtomicInteger(901115);
    @Override
    public User process(User user) throws Exception {
//        System.out.println("processing : " + counter.get());
        return new User(user.getUserId(),
                user.getUserName(),
                user.getFirstName().toUpperCase(),
                user.getLastName().toUpperCase(),
                user.getGender(),
                user.getPassword(),
                user.getStatus());
    }
}
