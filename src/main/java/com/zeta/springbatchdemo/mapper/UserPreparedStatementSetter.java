package com.zeta.springbatchdemo.mapper;

import com.zeta.springbatchdemo.model.User;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserPreparedStatementSetter implements ItemPreparedStatementSetter<User> {

    @Override
    public void setValues(User user,
                          PreparedStatement preparedStatement) throws SQLException, SQLException {
        preparedStatement.setInt(1, user.getUserId());
        preparedStatement.setString(2, user.getUserName());
        preparedStatement.setString(3, user.getFirstName());
        preparedStatement.setString(4, user.getLastName());
        preparedStatement.setString(5, user.getGender());
        preparedStatement.setString(6, user.getPassword());
        preparedStatement.setInt(7, user.getStatus());
    }
}