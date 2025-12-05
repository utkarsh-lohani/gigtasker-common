package org.gigtasker.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy // <--- Enables AOP
@ComponentScan(basePackages = "org.gigtasker.common")
public class CommonConfig {
}
