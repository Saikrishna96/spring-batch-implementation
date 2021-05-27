package com.zeta.springbatchdemo.configuration;

import com.zeta.springbatchdemo.lineAggregator.EmployeeLineAggregator;
import com.zeta.springbatchdemo.mapper.EmployeeRowMapper;
import com.zeta.springbatchdemo.mapper.UserFieldSetMapper;
import com.zeta.springbatchdemo.mapper.UserPreparedStatementSetter;
import com.zeta.springbatchdemo.mapper.UserRowMapper;
import com.zeta.springbatchdemo.model.Employee;
import com.zeta.springbatchdemo.model.User;
import com.zeta.springbatchdemo.processor.EmployeeValidator;
import com.zeta.springbatchdemo.processor.FilteringItemProcessor;
import com.zeta.springbatchdemo.processor.UpperCaseItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.PathResource;

import javax.sql.DataSource;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class DatabaseJobConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

//    @Bean
    public JdbcCursorItemReader<Employee> cursorItemReader() {
        JdbcCursorItemReader<Employee> reader = new JdbcCursorItemReader<>();

        reader.setSql("select * from employees order by first_name, lastname");
        reader.setDataSource(dataSource);
        reader.setRowMapper(new EmployeeRowMapper());
        return reader;
    }

//    @Bean
    public JdbcPagingItemReader<Employee> pagingItemReader() {
        System.out.println("Employee job item reader");
        JdbcPagingItemReader<Employee> reader = new JdbcPagingItemReader<>();

        reader.setDataSource(dataSource);
        reader.setFetchSize(2);
        reader.setRowMapper(new EmployeeRowMapper());

        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("id, email_address, first_name, lastname");
        queryProvider.setFromClause("from employees");

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        reader.setQueryProvider(queryProvider);

        return reader;
    }

//    @Bean
    public FlatFileItemReader<User> userFlatFileItemReader() {
        System.out.println(" userFlatFileItemReader started");
        FlatFileItemReader<User> reader = new FlatFileItemReader<>();
        reader.setResource(new PathResource("/Users/sk/Downloads/spring_batch_user_data.csv"));

        DefaultLineMapper<User> userDefaultLineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(new String[] {"userId", "userName", "firstName",
                "lastName", "gender", "password", "status"});

        userDefaultLineMapper.setLineTokenizer(tokenizer);
        userDefaultLineMapper.setFieldSetMapper(new UserFieldSetMapper());
        userDefaultLineMapper.afterPropertiesSet();

        reader.setLineMapper(userDefaultLineMapper);
        return reader;
    }

//    @Bean
    public UpperCaseItemProcessor upperCaseItemProcessor() {
        return new UpperCaseItemProcessor();
    }

//    @Bean
    public FilteringItemProcessor filteringItemProcessor() {
        return new FilteringItemProcessor();
    }

//    @Bean
    public ValidatingItemProcessor<Employee> validatingItemProcessor() {
        ValidatingItemProcessor<Employee> validatingItemProcessor = new ValidatingItemProcessor<>(new EmployeeValidator());
        validatingItemProcessor.setFilter(true);
        return validatingItemProcessor;
    }

    // all processors added here, validates, filter out & converts remaining to uppercase
//    @Bean
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

//    @Bean
    public FlatFileItemWriter<Employee> employeeFlatFileItemWriter() throws Exception {
        System.out.println("Employee job item writer");
        FlatFileItemWriter<Employee> itemWriter = new FlatFileItemWriter<>();

        String fileHeaders = "id|firstName|lastName|emailAddress";

        itemWriter.setLineAggregator(new EmployeeLineAggregator());
        itemWriter.setHeaderCallback(writer -> {
            writer.write(fileHeaders);
        });
        String outputPath = File.createTempFile("employee_output_" + LocalDateTime.now(), ".out")
                .getAbsolutePath();
        System.out.println("employee_output_file path : " + outputPath);
        itemWriter.setResource(new FileSystemResource(outputPath));
        itemWriter.afterPropertiesSet();
        return itemWriter;
    }

//    @Bean
    public JdbcBatchItemWriter<User> userJdbcBatchItemWriter() {
        JdbcBatchItemWriter<User> writer = new JdbcBatchItemWriter<>();

         final String QUERY_INSERT_USER = "INSERT " +
                "INTO user_details(user_id, username, first_name, last_name, gender, password, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        writer.setDataSource(dataSource);
        writer.setSql(QUERY_INSERT_USER);
        writer.setItemPreparedStatementSetter(new UserPreparedStatementSetter());
        writer.afterPropertiesSet();
        return writer;
    }

//    @Bean
    public ItemWriter<Employee> employeeItemWriter() {
        return items -> {
            for (Employee employee : items) {
                System.out.println(employee.toString());
            }
        };
    }

    public ItemWriter<User> userItemWriter() {
        System.out.println("userItemWriter started");
        return items -> {
            for (User user : items) {
                System.out.println(user.toString());
            }
        };
    }

//    @Bean
//    @StepScope
    public Tasklet tasklet(@Value("#{jobParameters['name']}") String name) {
        return (stepContribution, chunkContext) -> {
            System.out.println(String.format("The job ran for name : %s", name));
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step step1() throws Exception {
        System.out.println("Step1 is initiated");
        return stepBuilderFactory.get("step1" + LocalDateTime.now())
                .<User, User>chunk(100000)
                .reader(userFlatFileItemReader())
//                .reader(cursorItemReader())
//                .reader(pagingItemReader())
                .processor(upperCaseItemProcessor())
//                .processor(validatingItemProcessor())
//                .processor(filteringItemProcessor())
//                .processor(compositeItemProcessor())
//                .writer(employeeFlatFileItemWriter())
//                .writer(userItemWriter())
                .writer(userJdbcBatchItemWriter())
                .build();
    }

//    @Bean("employeeJob")
    public Job job() throws Exception {
        return jobBuilderFactory.get("EmployeeJobNew" + LocalDateTime.now())
                .start(step1())
                .build();
    }

//    @Bean("jobToLaunch")
    public Job jobToLaunch() throws Exception {
        return jobBuilderFactory.get("SampleJob" + LocalDateTime.now())
                .start(stepBuilderFactory.get("newStep")
                    .tasklet(tasklet(null))
                    .build())
                .build();
    }
}
