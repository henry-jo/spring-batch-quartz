package com.henry.springbatchquartz.controller

import com.henry.springbatchquartz.job.SimpleBatchJob
import com.henry.springbatchquartz.service.JobScheduleService
import org.quartz.Job
import org.quartz.Scheduler
import org.springframework.batch.core.configuration.JobLocator
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.ZonedDateTime
import javax.servlet.http.HttpServletRequest
import kotlin.reflect.full.isSubclassOf

@RestController
@RequestMapping("/quartz")
class JobScheduleController(
    private val scheduler: Scheduler,
    private val jobLauncher: JobLauncher,
    private val jobLocator: JobLocator,
    private val jobScheduleService: JobScheduleService
) {
    @GetMapping(value = ["/current-job-count"])
    fun jobCount(request: HttpServletRequest) = scheduler.currentlyExecutingJobs.count()

    @Suppress("UNCHECKED_CAST")
    @PostMapping(value = ["/trigger"])
    fun trigger(
        request: HttpServletRequest,
        @RequestBody jobTriggerDTO: JobTriggerDTO
    ): ResponseEntity<String> {

        val jobClass = try {
            Class.forName(jobTriggerDTO.jobClassName)
        } catch (e: ClassNotFoundException) {
            return ResponseEntity.badRequest().body("${jobTriggerDTO.jobClassName} is not found.")
        }

        if (!jobClass.kotlin.isSubclassOf(Job::class)) {
            return ResponseEntity.badRequest()
                .body("$${jobTriggerDTO.jobClassName} is not quartz job.")
        }

        val jobData = jobTriggerDTO.jobData ?: emptyMap()
        jobScheduleService.schedule(
            jobClass as Class<out Job>,
            jobData,
            ZonedDateTime.now()
        )

        return ResponseEntity.ok("triggered: $${jobTriggerDTO.jobClassName} $jobData")
    }

    @PostMapping("/trigger/batch-job")
    fun triggerBatchJob(): ResponseEntity<Unit> {
        val jobData = hashMapOf(
            "jobName" to "simpleJob"
        )
        jobScheduleService.schedule(
            SimpleBatchJob::class.java as Class<out Job>,
            jobData,
            ZonedDateTime.now()
        )

        return ResponseEntity.noContent().build()
    }
}

data class JobTriggerDTO(val jobClassName: String?, val jobData: Map<String, Any>?)