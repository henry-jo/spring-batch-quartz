package com.henry.springbatchquartz.batch

import mu.KLogging
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SimpleBatchJobConfig(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory
) {
    companion object : KLogging()

    @Bean(name = ["simpleJob"])
    fun job() = jobBuilderFactory.get("simpleJob")
        .start(step())
        .build()

    @Bean
    fun step() = stepBuilderFactory.get("step")
        .tasklet { _, _ ->
            logger.info("step example")
            RepeatStatus.FINISHED
        }.build()
}