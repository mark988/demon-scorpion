package com.yaoxiang.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Builder
@Data
public class PushTable {
    private String binlog;
    private String schemaName;
    private String table;
    /**
     * 事件类型
     */
    private String eventType;
    /**
     * 表内容
     */
    private List<Map<String,TableColumn>> list;
}
