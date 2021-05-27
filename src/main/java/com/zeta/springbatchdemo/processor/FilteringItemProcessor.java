package com.zeta.springbatchdemo.processor;

import com.zeta.springbatchdemo.model.User;
import org.springframework.batch.item.ItemProcessor;

public class FilteringItemProcessor implements ItemProcessor<User, User> {
    @Override
    public User process(User user) throws Exception {
        if (user.getUserId() % 2 == 0)
            return null;
        else
            return user;
    }
}
