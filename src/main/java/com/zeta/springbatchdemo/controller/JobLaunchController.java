package com.zeta.springbatchdemo.controller;

import com.zeta.springbatchdemo.JobLaunchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobLaunchController {

    @Autowired
    JobLaunchService jobLaunchService;

    @GetMapping
    public void launchEmployeeJob() throws Exception {
        System.out.println("Triggered User data job.");
        long start = System.currentTimeMillis();
        jobLaunchService.runUserDataJob();
        System.out.println("Elapsed time in millis : " + (System.currentTimeMillis() - start));
        System.out.println("Job executed");
    }


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


}
