package com.xquare.v1servicepoint.configuration.aop

import com.xquare.v1servicepoint.BASE_PACKAGE
import com.xquare.v1servicepoint.annotation.SagaStep
import com.xquare.v1servicepoint.annotation.UseCase
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType

@Configuration
@ComponentScan(
    basePackages = [BASE_PACKAGE],
    includeFilters = [
        Filter(
            type = FilterType.ANNOTATION,
            classes = [
                UseCase::class,
                SagaStep::class,
            ],
        ),
    ],
)
class ComponentScanConfiguration
