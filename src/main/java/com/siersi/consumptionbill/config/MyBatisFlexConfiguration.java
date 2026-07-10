package com.siersi.consumptionbill.config;

import com.mybatisflex.core.audit.AuditManager;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisFlexConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MyBatisFlexConfiguration.class);

    @PostConstruct
    public void init() {
        AuditManager.setAuditEnable(true);
        AuditManager.setMessageCollector(auditMessage ->
                logger.info("{}, 耗时{}ms", auditMessage.getFullSql(), auditMessage.getElapsedTime())
        );
    }
}