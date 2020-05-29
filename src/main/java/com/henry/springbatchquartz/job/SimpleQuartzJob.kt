package com.henry.springbatchquartz.job

import com.henry.springbatchquartz.service.JobBusinessService
import mu.KLogging
import org.quartz.JobExecutionContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component

@Component
class SimpleQuartzJob : QuartzJobBean() {
    companion object : KLogging()

    @Autowired
    private lateinit var jobBusinessService: JobBusinessService

    override fun executeInternal(context: JobExecutionContext) {
        val jobDataMap = context.jobDetail.jobDataMap
        val id = jobDataMap.getIntValue("id")

        logger.info("-------simple job execution [$id]-------")
        jobBusinessService.printLog()
    }
}