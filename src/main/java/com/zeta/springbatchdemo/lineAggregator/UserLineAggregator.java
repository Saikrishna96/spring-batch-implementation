package com.zeta.springbatchdemo.lineAggregator;

import com.zeta.springbatchdemo.model.User;
import lombok.SneakyThrows;
import org.springframework.batch.item.file.transform.LineAggregator;

import java.util.StringJoiner;

public class UserLineAggregator implements LineAggregator<User> {

    @SneakyThrows
    @Override
    public String aggregate(User user) {
        StringJoiner data = new StringJoiner("|");
        data.add(String.valueOf(user.getUserId()))
                .add(user.getUserName())
                .add(user.getFirstName())
                .add(user.getLastName())
                .add(user.getGender())
                .add(user.getPassword())
                .add(String.valueOf(user.getStatus()));
        return data.toString();

    }
}