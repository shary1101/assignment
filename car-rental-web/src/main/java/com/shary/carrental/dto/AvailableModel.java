package com.shary.carrental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Available model data transfer object used to return available models from endpoints
 *
 * @author shary
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableModel {
    private Long modelId;
    private String modelName;
    private int numLeft;
}
