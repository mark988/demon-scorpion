package com.yaoxiang.component;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author
 */
@Data
@Component
@ConfigurationProperties(prefix = "canal")
public class CanalConfig {
    /**
     *  主机
     */
    private String host;
    /**
     *  端口
     */
    private Integer port;
    /**
     * 批量大小
     */
    private Integer batchSize;
    /**
     * 数据推送地址
     */
    private String pushUrl;
}
