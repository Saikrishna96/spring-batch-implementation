package com.zeta.springbatchdemo;

import com.zeta.springbatchdemo.lineAggregator.UserLineAggregator;
import com.zeta.springbatchdemo.mapper.UserRowMapper;
import com.zeta.springbatchdemo.model.Employee;
import com.zeta.springbatchdemo.model.User;
import com.zeta.springbatchdemo.processor.EmployeeValidator;
import com.zeta.springbatchdemo.processor.FilteringItemProcessor;
import com.zeta.springbatchdemo.processor.UpperCaseItemProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class JobLaunchService {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    @Autowired
    JdbcPagingItemReader<User> userDataPagingItemReader;

    public void runUserDataJob() throws Exception {
        try {
            this.jobLauncher.run(createUserJob(jobId()), new JobParameters());
        } catch (Exception exception) {
            log.error("Exception : ", exception);
        }
    }

    public String jobId() {
        DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("yyyyMMddHH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        String dateString = FOMATTER.format(localDateTime);
        System.out.println(dateString);     //07/15/2018
        return "UserDataJob_" + dateString;
    }

    public Job createUserJob(String jobId) throws Exception {
        return jobBuilderFactory.get(jobId)
                .start(userJobstep())
                .build();
    }

    public Step userJobstep() throws Exception {
        return stepBuilderFactory.get("step1" + LocalDateTime.now())
                .<User, User>chunk(10000)
//                .reader(cursorItemReader())
                .reader(userDataPagingItemReader)
                .processor(upperCaseItemProcessor())
//                .processor(validatingItemProcessor())
//                .processor(filteringItemProcessor())
//                .processor(compositeItemProcessor())
                .writer(userDataFlatFileItemWriter())
                .build();
    }

    public JdbcCursorItemReader<User> cursorItemReader() {
        JdbcCursorItemReader<User> reader = new JdbcCursorItemReader<>();

        reader.setSql("select * from user_details order by user_id limit 100000");
        reader.setDataSource(dataSource);
        reader.setRowMapper(new UserRowMapper());
        return reader;
    }

//    public JdbcPagingItemReader<User> userDataPagingItemReader() {
//        System.out.println("User data job item reader");
//        JdbcPagingItemReader<User> reader = new JdbcPagingItemReader<>();
//
//        reader.setDataSource(dataSource);
//        reader.setFetchSize(10000);
//        reader.setRowMapper(new UserRowMapper());
//
//        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
//        queryProvider.setSelectClause("user_id, username, first_name, last_name, gender, password, status");
//        queryProvider.setFromClause("from user_details");
//
//        Map<String, Order> sortKeys = new HashMap<>(1);
//        sortKeys.put("user_id", Order.ASCENDING);
//        queryProvider.setSortKeys(sortKeys);
//
//        reader.setQueryProvider(queryProvider);
//        return reader;
//    }

    public UpperCaseItemProcessor upperCaseItemProcessor() {
        return new UpperCaseItemProcessor();
    }

    public FilteringItemProcessor filteringItemProcessor() {
        return new FilteringItemProcessor();
    }

    public ValidatingItemProcessor<Employee> validatingItemProcessor() {
        ValidatingItemProcessor<Employee> validatingItemProcessor = new ValidatingItemProcessor<>(new EmployeeValidator());
        validatingItemProcessor.setFilter(true);
        return validatingItemProcessor;
    }

    // all processors added here, validates, filter out & converts remaining to uppercase
    public CompositeItemProcessor<User, User> compositeItemProcessor() throws Exception {
        List<ItemProcessor<User, User>> delegates = new ArrayList<>();
//        delegates.add(validatingItemProcessor());
        delegates.add(filteringItemProcessor());
        delegates.add(upperCaseItemProcessor());

        CompositeItemProcessor<User, User> compositeItemProcessor = new CompositeItemProcessor<>();
        compositeItemProcessor.setDelegates(delegates);
        compositeItemProcessor.afterPropertiesSet();

        return compositeItemProcessor;
    }

    public FlatFileItemWriter<User> userDataFlatFileItemWriter() throws Exception {
        System.out.println("User job item writer");
        FlatFileItemWriter<User> itemWriter = new FlatFileItemWriter<>();

        String fileHeaders = "user_id|username|first_name|last_name|gender|password|status";

        itemWriter.setLineAggregator(new UserLineAggregator());
        itemWriter.setHeaderCallback(writer -> {
            writer.write(fileHeaders);
        });
        String outputPath = File.createTempFile("user_output_" + LocalDateTime.now(), ".out")
                .getAbsolutePath();
        System.out.println("User_output_file path : " + outputPath);
        itemWriter.setResource(new FileSystemResource(outputPath));
        itemWriter.afterPropertiesSet();
        return itemWriter;
    }

}
