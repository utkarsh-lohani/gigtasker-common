package com.gigtasker.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy // <--- Enables AOP
@ComponentScan(basePackages = "com.gigtasker.common")
public class CommonConfig {
}
