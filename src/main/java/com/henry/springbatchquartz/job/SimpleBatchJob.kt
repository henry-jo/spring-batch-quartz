package com.henry.springbatchquartz.job

import org.quartz.JobExecutionContext
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.configuration.JobLocator
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component

@Component
class SimpleBatchJob: QuartzJobBean() {
    @Autowired
    private lateinit var jobLauncher: JobLauncher

    @Autowired
    private lateinit var jobLocator: JobLocator

    override fun executeInternal(context: JobExecutionContext) {
        val jobDataMap = context.jobDetail.jobDataMap
        val job = jobLocator.getJob(jobDataMap.getString("jobName"))
        val params = JobParametersBuilder()
            .addString("JobID", System.currentTimeMillis().toString())
            .toJobParameters()

        jobLauncher.run(job, params)
    }
}