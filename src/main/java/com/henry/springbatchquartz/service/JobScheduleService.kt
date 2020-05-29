package com.henry.springbatchquartz.service

import org.quartz.Job
import org.quartz.JobBuilder
import org.quartz.JobDataMap
import org.quartz.Scheduler
import org.quartz.TriggerBuilder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.Date

@Service
class JobScheduleService(
    private val scheduler: Scheduler,
    private val clock: Clock = Clock.systemDefaultZone()
) {
    fun schedule(
        jobClass: Class<out Job>,
        jobData: Map<String, Any>,
        delay: Long,
        unit: ChronoUnit
    ) {
        schedule(jobClass, jobData, ZonedDateTime.now(clock).plus(delay, unit))
    }

    fun schedule(
        jobClass: Class<out Job>,
        jobData: Map<String, Any>,
        dateTime: ZonedDateTime
    ) {
        val jobDetail = JobBuilder
            .newJob(jobClass)
            .usingJobData(JobDataMap(jobData))
            .build()

        val trigger = TriggerBuilder
            .newTrigger()
            .startAt(Date.from(dateTime.toInstant()))
            .build()

        scheduler.scheduleJob(jobDetail, trigger)
        scheduler.start()
    }
}