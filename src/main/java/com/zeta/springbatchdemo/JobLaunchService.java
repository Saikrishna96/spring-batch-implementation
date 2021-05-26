package com.zeta.springbatchdemo;

import com.zeta.springbatchdemo.lineAggregator.EmployeeLineAggregator;
import com.zeta.springbatchdemo.mapper.EmployeeRowMapper;
import com.zeta.springbatchdemo.model.Employee;
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

    public void runEmployeeJob() throws Exception {
        try {
            this.jobLauncher.run(createEmpJob(jobId()), new JobParameters());
        } catch (Exception exception) {
            log.error("Exception : ", exception);
        }
    }

    public String jobId() {
        DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("yyyyMMddHH:mm:ss");

        LocalDateTime localDateTime = LocalDateTime.now();
        String dateString = FOMATTER.format(localDateTime);
        System.out.println(dateString);     //07/15/2018
        return "employeeJob_" + dateString;
    }

    public Job createEmpJob(String jobId) throws Exception {
        return jobBuilderFactory.get(jobId)
                .start(employeeJobstep())
                .build();
    }

    public Step employeeJobstep() throws Exception {
        return stepBuilderFactory.get("step1" + LocalDateTime.now())
                .<Employee, Employee>chunk(2)
                .reader(cursorItemReader())
//                .reader(pagingItemReader())
                .processor(upperCaseItemProcessor())
//                .processor(validatingItemProcessor())
                .processor(filteringItemProcessor())
//                .processor(compositeItemProcessor())
                .writer(employeeFlatFileItemWriter())
                .build();
    }

    public JdbcCursorItemReader<Employee> cursorItemReader() {
        JdbcCursorItemReader<Employee> reader = new JdbcCursorItemReader<>();

        reader.setSql("select * from employees order by first_name, lastname");
        reader.setDataSource(dataSource);
        reader.setRowMapper(new EmployeeRowMapper());
        return reader;
    }

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

}
