package com.yaoxiang.component;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author
 */
@Data
@Component
@ConfigurationProperties(prefix = "db")
public class DBConfig {
    /**
     * 数据库名字
     */
    private String name;
    /**
     *  用户名
     */
    private String user;
    /**
     *  密码
     */
    private String passwd;
}
