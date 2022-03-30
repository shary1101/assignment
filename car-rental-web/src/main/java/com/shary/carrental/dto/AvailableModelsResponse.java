package com.shary.carrental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Wrapper around AvailableModel object
 *
 * @author shary
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableModelsResponse {
    private List<AvailableModel> availableModels;
}
