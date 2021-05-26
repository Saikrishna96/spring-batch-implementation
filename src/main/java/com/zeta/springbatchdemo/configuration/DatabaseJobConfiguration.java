package com.zeta.springbatchdemo.configuration;

import com.zeta.springbatchdemo.lineAggregator.EmployeeLineAggregator;
import com.zeta.springbatchdemo.mapper.EmployeeRowMapper;
import com.zeta.springbatchdemo.model.Employee;
import com.zeta.springbatchdemo.processor.EmployeeValidator;
import com.zeta.springbatchdemo.processor.FilteringItemProcessor;
import com.zeta.springbatchdemo.processor.UpperCaseItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

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

    @Bean
    public JdbcCursorItemReader<Employee> cursorItemReader() {
        JdbcCursorItemReader<Employee> reader = new JdbcCursorItemReader<>();

        reader.setSql("select * from employees order by first_name, lastname");
        reader.setDataSource(dataSource);
        reader.setRowMapper(new EmployeeRowMapper());
        return reader;
    }

    @Bean
    public JdbcPagingItemReader<Employee> pagingItemReader() {
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

    @Bean
    public UpperCaseItemProcessor upperCaseItemProcessor() {
        return new UpperCaseItemProcessor();
    }

    @Bean
    public FilteringItemProcessor filteringItemProcessor() {
        return new FilteringItemProcessor();
    }

    @Bean
    public ValidatingItemProcessor<Employee> validatingItemProcessor() {
        ValidatingItemProcessor<Employee> validatingItemProcessor = new ValidatingItemProcessor<>(new EmployeeValidator());
        validatingItemProcessor.setFilter(true);
        return validatingItemProcessor;
    }

    // all processors added here, validates, filter out & converts remaining to uppercase
    @Bean
    public CompositeItemProcessor<Employee, Employee> compositeItemProcessor() throws Exception {
        List<ItemProcessor<Employee, Employee>> delegates = new ArrayList<>();
        delegates.add(validatingItemProcessor());
        delegates.add(filteringItemProcessor());
        delegates.add(upperCaseItemProcessor());

        CompositeItemProcessor<Employee, Employee> compositeItemProcessor = new CompositeItemProcessor<>();
        compositeItemProcessor.setDelegates(delegates);
        compositeItemProcessor.afterPropertiesSet();

        return compositeItemProcessor;
    }

    @Bean
    public FlatFileItemWriter<Employee> employeeFlatFileItemWriter() throws Exception {
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
    public ItemWriter<Employee> employeeItemWriter() {
        return items -> {
            for (Employee employee : items) {
                System.out.println(employee.toString());
            }
        };
    }

    @Bean
    public Step step1() throws Exception {
        return stepBuilderFactory.get("step1" + LocalDateTime.now())
                .<Employee, Employee>chunk(2)
//                .reader(cursorItemReader())
                .reader(pagingItemReader())
//                .processor(upperCaseItemProcessor())
//                .processor(validatingItemProcessor())
//                .processor(filteringItemProcessor())
                .processor(compositeItemProcessor())
                .writer(employeeFlatFileItemWriter())
                .build();
    }

    @Bean
    public Job job() throws Exception {
        return jobBuilderFactory.get("job" + LocalDateTime.now())
                .start(step1())
                .build();
    }
}
