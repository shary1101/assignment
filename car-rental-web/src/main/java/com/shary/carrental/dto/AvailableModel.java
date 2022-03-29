package com.shary.carrental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableModel {
    private Long modelId;
    private String modelName;
    private int numLeft;
}
