package com.zeta.springbatchdemo.configuration;

import com.zeta.springbatchdemo.lineAggregator.UserLineAggregator;
import com.zeta.springbatchdemo.mapper.UserRowMapper;
import com.zeta.springbatchdemo.model.User;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;
import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class UserDataJobConfiguration {

    @Autowired
    private DataSource dataSource;

    @Bean("userDataPagingItemReader")
    public JdbcPagingItemReader<User> userDataPagingItemReader() {
        System.out.println("User data job item reader bean initiated");
        JdbcPagingItemReader<User> reader = new JdbcPagingItemReader<>();

        reader.setDataSource(dataSource);
        reader.setFetchSize(50000);
        reader.setRowMapper(new UserRowMapper());

        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("user_id, username, first_name, last_name, gender, password, status");
        queryProvider.setFromClause("from user_details");

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("user_id", Order.ASCENDING);
        queryProvider.setSortKeys(sortKeys);

        reader.setQueryProvider(queryProvider);
        return reader;
    }

}
