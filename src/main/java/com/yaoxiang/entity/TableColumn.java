package com.yaoxiang.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TableColumn {
    private String value;
    private Boolean update;
}
