package com.viet.aodai.product.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class SqlHolder {

    private final String              dataSQL;
    private final String              countSQL;
    private final Map<String, Object> params;
}
