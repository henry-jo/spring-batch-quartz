package com.henry.springbatchquartz

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@EnableBatchProcessing // 배치 기능 활성화
@SpringBootApplication
class SpringBatchQuartzApplication

fun main(args: Array<String>) {
    SpringApplication.run(SpringBatchQuartzApplication::class.java, *args)
}