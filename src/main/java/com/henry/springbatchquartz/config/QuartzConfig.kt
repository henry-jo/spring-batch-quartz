package com.henry.springbatchquartz.config

import mu.KLogging
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.quartz.JobListener
import org.quartz.listeners.JobListenerSupport
import org.quartz.spi.TriggerFiredBundle
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.boot.autoconfigure.quartz.QuartzProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import org.springframework.scheduling.quartz.SpringBeanJobFactory
import javax.sql.DataSource

@Configuration
class QuartzConfig(
    private val dataSource: DataSource,
    private val applicationContext: ApplicationContext,
    private val quartzProperties: QuartzProperties
) {
    companion object : KLogging()

    /**
     * Scheduler를 생성해주는 Scheduler Factory 설정
     */
    @Bean(name = ["schedulerFactory"])
    fun schedulerFactoryBean(): SchedulerFactoryBean {
        return SchedulerFactoryBean().apply {
            setDataSource(dataSource)
            setJobFactory(springBeanJobFactory())
            setGlobalJobListeners(quartzJobListener())
            setQuartzProperties(quartzProperties.properties.toProperties())
            isAutoStartup = quartzProperties.properties["autoStartup"] == "true"
        }
    }

    @Bean
    fun springBeanJobFactory(): SpringBeanJobFactory {
        val jobFactory = AutowiringSpringBeanJobFactory()
        jobFactory.setApplicationContext(applicationContext)
        return jobFactory
    }

    /**
     * job listener 설정
     */
    @Bean
    fun quartzJobListener(): JobListener {
        return object : JobListenerSupport() {

            override fun getName(): String = "QuartzJobListener"

            override fun jobWasExecuted(
                context: JobExecutionContext,
                jobException: JobExecutionException?
            ) {
                logger.info("#######listener#######")
                if (jobException != null) {
                    logger.error("Quartz job failed", jobException)
                }
            }
        }
    }
}

/**
 * Quartz Job에서 Spring Container에서 주입을 받기 위한 설정
 */
private class AutowiringSpringBeanJobFactory : SpringBeanJobFactory(), ApplicationContextAware {

    @Transient
    private lateinit var beanFactory: AutowireCapableBeanFactory

    override fun setApplicationContext(context: ApplicationContext) {
        beanFactory = context.autowireCapableBeanFactory
    }

    override fun createJobInstance(bundle: TriggerFiredBundle): Any {
        val job = super.createJobInstance(bundle)
        beanFactory.autowireBean(job)
        return job
    }
}