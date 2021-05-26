package com.zeta.springbatchdemo.controller;

import com.zeta.springbatchdemo.JobLaunchService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobLaunchController {

    @Autowired
    JobLaunchService jobLaunchService;

//    @Autowired
//    @Qualifier("jobToLaunch")
//    private Job job;
//
//    @Autowired
//    @Qualifier("employeeJob")
//    private Job employeeJob;

//    @PostMapping
//    public void launchSampleJob(@RequestParam("name") String name) throws Exception{
//        JobParameters jobParameters = new JobParametersBuilder()
//                .addString("name", name)
//                .toJobParameters();
//        this.jobLauncher.run(job, jobParameters);
//    }

    @GetMapping
    public void launchEmployeeJob() throws Exception {
        System.out.println(" launchEmployeeJob started");
        jobLaunchService.runEmployeeJob();
        System.out.println("Job executed");
    }
}
