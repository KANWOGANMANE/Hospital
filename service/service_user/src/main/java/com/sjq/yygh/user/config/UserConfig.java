package com.sjq.yygh.user.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.sjq.yygh.user.mapper")
public class UserConfig {
}
