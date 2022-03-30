package com.shary.carrental.service;

import com.shary.carrental.dto.AddReservationRequest;
import com.shary.carrental.dto.AvailableModelsResponse;
import com.shary.carrental.dto.Result;

import java.util.Date;

/**
 * Service that handles car reservations related calls using domain services
 *
 * @author shary
 *
 */
public interface CarReservationService {

    Result<AvailableModelsResponse> getAvailableModels(Date fromDate, Date toDate);

    Result addReservation(AddReservationRequest request);
}
