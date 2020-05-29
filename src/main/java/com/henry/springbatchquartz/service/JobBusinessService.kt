package com.henry.springbatchquartz.service

import mu.KLogging
import org.springframework.stereotype.Service

@Service
class JobBusinessService {
    companion object : KLogging()

    fun printLog() {
        logger.info("##########spring service job")
    }
}