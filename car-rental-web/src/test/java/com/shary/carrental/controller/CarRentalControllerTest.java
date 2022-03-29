package com.shary.carrental.controller;

import com.shary.carrental.CarRentalApplication;
import com.shary.carrental.common.StatusEnum;
import com.shary.carrental.dto.AddReservationRequest;
import com.shary.carrental.dto.AvailableModelsResponse;
import com.shary.carrental.dto.Result;
import com.shary.carrental.service.CarReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = CarRentalApplication.class)
class CarRentalControllerTest {

    Logger logger = LoggerFactory.getLogger(CarRentalControllerTest.class);

    @InjectMocks
    private CarRentalController carRentalController;
    @Mock
    private CarReservationService carReservationService;

    private static Date fromDate = null;
    private static Date toDate = null;

    @BeforeEach
    void init() {
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            fromDate = format.parse("2022-03-25");
            toDate = format.parse("2022-03-27");
        } catch (Exception e) {
            logger.error("Exception in init: ", e);
        }
    }
    @Test
    void queryAvailableModels_NoExceptionThrowing() {
        AvailableModelsResponse mockResponse = new AvailableModelsResponse();
        mockResponse.setAvailableModels(new ArrayList<>());
        Result<AvailableModelsResponse> mockResult = new Result<>(200, null, mockResponse);

        Mockito.when(carReservationService.getAvailableModels(fromDate, toDate)).thenReturn(mockResult);
        Result<AvailableModelsResponse> response = carRentalController.queryAvailableModels(fromDate, toDate);

        assertEquals(StatusEnum.SUCCESS.getCode(), response.getCode());
        assertEquals(new ArrayList<>(), response.getData().getAvailableModels());
    }

    @Test
    void queryAvailableModels_IllegalArgumentExceptionThrowing_500() {
        Mockito.when(carReservationService.getAvailableModels(fromDate, toDate)).thenThrow(new IllegalArgumentException("IllegalArgument Exception"));
        Result<AvailableModelsResponse> response = carRentalController.queryAvailableModels(fromDate, toDate);

        assertEquals(StatusEnum.FAIL.getCode(), response.getCode());
        assertEquals("IllegalArgument Exception", response.getMessage());
    }

    @Test
    void queryAvailableModels_ExceptionThrowing_500() {
        Mockito.when(carReservationService.getAvailableModels(fromDate, toDate)).thenThrow(new NullPointerException());
        Result<AvailableModelsResponse> response = carRentalController.queryAvailableModels(fromDate, toDate);

        assertEquals(StatusEnum.INTERNAL_SERVER_ERROR.getCode(), response.getCode());
        assertEquals("Internal Server Error", response.getMessage());
    }

    @Test
    void addReservation_NoExceptionThrowing() {
        AddReservationRequest request = new AddReservationRequest();
        Result mockResult = new Result(200, "Reservation is successful.", null);

        Mockito.when(carReservationService.addReservation(request)).thenReturn(mockResult);
        Result<AvailableModelsResponse> response = carRentalController.addReservation(request);

        assertEquals(StatusEnum.SUCCESS.getCode(), response.getCode());
        assertEquals("Reservation is successful.", response.getMessage());
    }

    @Test
    void addReservation_IllegalArgumentExceptionThrowing_500() {
        AddReservationRequest request = new AddReservationRequest();

        Mockito.when(carReservationService.addReservation(request)).thenThrow(new IllegalArgumentException("IllegalArgument Exception"));
        Result<AvailableModelsResponse> response = carRentalController.addReservation(request);

        assertEquals(StatusEnum.FAIL.getCode(), response.getCode());
        assertEquals("IllegalArgument Exception", response.getMessage());
    }

    @Test
    void addReservation_ExceptionThrowing_500() {
        AddReservationRequest request = new AddReservationRequest();

        Mockito.when(carReservationService.addReservation(request)).thenThrow(new NullPointerException());
        Result<AvailableModelsResponse> response = carRentalController.addReservation(request);

        assertEquals(StatusEnum.INTERNAL_SERVER_ERROR.getCode(), response.getCode());
        assertEquals("Internal Server Error", response.getMessage());
    }
}