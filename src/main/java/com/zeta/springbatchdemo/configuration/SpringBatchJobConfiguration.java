package com.zeta.springbatchdemo.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class SpringBatchJobConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step step1(){
        return stepBuilderFactory.get("step1")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("Step1 is executed.");
                        return RepeatStatus.FINISHED;
                    }
                }).build();

    }

    @Bean
    public Step step2(){
        return stepBuilderFactory.get("step2")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("Step2 is executed.");
                    return RepeatStatus.FINISHED;
                }).build();

    }

    @Bean
    public Step step3(){
        return stepBuilderFactory.get("step3")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("Step3 is executed.");
                    return RepeatStatus.FINISHED;
                }).build();

    }

//    @Bean
//    public Job helloWorldJob() {
//        return jobBuilderFactory.get("helloWorldJob")
//                .start(step1())
//                .build();
//    }

    @Bean
    public Job transitionJobSampleNext() {
        return jobBuilderFactory.get("transitionJobSampleNext")
                .start(step1())
                .next(step2())
                .next(step3())
                .build();
    }
}
