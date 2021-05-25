package com.zeta.springbatchdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
//Spring Batch is a framework for building reliable and fault tolerance enterprise batch jobs.
// It supports many features like restarting a failed batch,
// recording the status of the batch execution and so on.
// In order to achieve that Spring Batch uses a database schema to store the status of the registered jobs,
// the auto-configuration already provides you the basic configuration of the required data source and it is this configuration that requires the relational database configuration.
public class SpringBatchDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchDemoApplication.class, args);
	}

}
