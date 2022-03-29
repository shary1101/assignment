package com.shary.carrental.service.impl;

import com.shary.carrental.CarRentalApplication;
import com.shary.carrental.common.StatusEnum;
import com.shary.carrental.dao.CarReservation;
import com.shary.carrental.dao.CarReservationRepository;
import com.shary.carrental.dao.CarStock;
import com.shary.carrental.dao.CarStockRepository;
import com.shary.carrental.dto.AddReservationRequest;
import com.shary.carrental.dto.AvailableModel;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = CarRentalApplication.class)
class CarReservationServiceImplTest {

    Logger logger = LoggerFactory.getLogger(CarReservationServiceImplTest.class);

    @InjectMocks
    private CarReservationService carReservationService = new CarReservationServiceImpl();
    @Mock
    private CarReservationRepository carReservationRepository;
    @Mock
    private CarStockRepository carStockRepository;

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
    void getAvailableModels_FromDateLaterThanToDate_400() {
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date errorFromDate = format.parse("2022-03-27");
            Date errorToDate = format.parse("2022-03-25");

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                carReservationService.getAvailableModels(errorFromDate, errorToDate);
            });
            assertEquals("The input param 'fromDate' should be no later than 'toDate'.", thrown.getMessage());

        } catch (Exception e) {
            logger.error("Exception in getAvailableModels_FromDateLaterThanToDate_400: ", e);
        }
    }

    @Test
    void getAvailableModels_NoExceptionThrowing_200() {

        Mockito.when(carReservationRepository.findOverlappedReservations(fromDate, toDate)).thenReturn(new ArrayList<>());

        List<CarStock> carStocks = new ArrayList<>();
        CarStock carStock = new CarStock(1, "Toyota Camry", 2);
        carStocks.add(carStock);
        Mockito.when(carStockRepository.findAll()).thenReturn(carStocks);

        Result<AvailableModelsResponse> response = carReservationService.getAvailableModels(fromDate, toDate);
        List<AvailableModel> availableModels = new ArrayList<>();
        availableModels.add(new AvailableModel(1L, "Toyota Camry", 2));

        assertEquals(StatusEnum.SUCCESS.getCode(), response.getCode());
        assertEquals(availableModels, response.getData().getAvailableModels());
    }

    @Test
    void addReservation_ModelIdNotPresent_400() {

        AddReservationRequest request = new AddReservationRequest(null, fromDate, toDate);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            carReservationService.addReservation(request);
        });
        assertEquals("'modelId' is not provided", thrown.getMessage());
    }

    @Test
    void addReservation_FromDateNotPresent_400() {
        AddReservationRequest request = new AddReservationRequest(1L, null, toDate);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            carReservationService.addReservation(request);
        });
        assertEquals("'fromDate' is not provided", thrown.getMessage());
    }

    @Test
    void addReservation_ToDateNotPresent_400() {
        AddReservationRequest request = new AddReservationRequest(1L, fromDate, null);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            carReservationService.addReservation(request);
        });
        assertEquals("'toDate' is not provided", thrown.getMessage());
    }

    @Test
    void addReservation_FromDateLaterThanToDate_400() {
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date errorFromDate = format.parse("2022-03-27");
            Date errorToDate = format.parse("2022-03-25");
            AddReservationRequest request = new AddReservationRequest(1L, errorFromDate, errorToDate);
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                carReservationService.addReservation(request);
            });
            assertEquals("The input param 'fromDate' should be no later than 'toDate'.", thrown.getMessage());

        } catch (Exception e) {
            logger.error("Exception in addReservation_FromDateLaterThanToDate_400: ", e);
        }
    }

    @Test
    void addReservation_CarModelNotPresent_400() {
        Optional<CarStock> mockCarStock = Optional.empty();
        Mockito.when(carStockRepository.findById(3L)).thenReturn(mockCarStock);

        AddReservationRequest request = new AddReservationRequest(3L, fromDate, toDate);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            carReservationService.addReservation(request);
        });
        assertEquals("This car model is not provided in our company.", thrown.getMessage());
    }

    @Test
    void addReservation_HasAllStock_200() {
        Long modelId = 1L;

        CarStock carStock = new CarStock(modelId, "Toyota Camry", 2);
        Optional<CarStock> mockCarStock = Optional.of(carStock);
        Mockito.when(carStockRepository.findById(modelId)).thenReturn(mockCarStock);

        Mockito.when(carReservationRepository.findOverlappedReservationsById(modelId, fromDate, toDate)).thenReturn(0);

        CarReservation mockCarReservation = new CarReservation();
        mockCarReservation.setModelId(1);
        mockCarReservation.setFromDate(fromDate);
        mockCarReservation.setToDate(toDate);
        Mockito.when(carReservationRepository.save(mockCarReservation)).thenReturn(mockCarReservation);

        AddReservationRequest request = new AddReservationRequest(modelId, fromDate, toDate);
        Result<AvailableModelsResponse> response = carReservationService.addReservation(request);

        assertEquals(StatusEnum.SUCCESS.getCode(), response.getCode());
        assertEquals("Reservation is successful.", response.getMessage());

    }

    @Test
    void addReservation_HasPartialStock_200() {
        Long modelId = 1L;

        CarStock carStock = new CarStock(modelId, "Toyota Camry", 2);
        Optional<CarStock> mockCarStock = Optional.of(carStock);
        Mockito.when(carStockRepository.findById(modelId)).thenReturn(mockCarStock);

        Mockito.when(carReservationRepository.findOverlappedReservationsById(modelId, fromDate, toDate)).thenReturn(1);

        CarReservation mockCarReservation = new CarReservation();
        mockCarReservation.setModelId(1);
        mockCarReservation.setFromDate(fromDate);
        mockCarReservation.setToDate(toDate);
        Mockito.when(carReservationRepository.save(mockCarReservation)).thenReturn(mockCarReservation);

        AddReservationRequest request = new AddReservationRequest(modelId, fromDate, toDate);
        Result<AvailableModelsResponse> response = carReservationService.addReservation(request);

        assertEquals(StatusEnum.SUCCESS.getCode(), response.getCode());
        assertEquals("Reservation is successful.", response.getMessage());
    }

    @Test
    void addReservation_OutOfStock_400() {
            Long modelId = 1L;

            CarStock carStock = new CarStock(modelId, "Toyota Camry", 1);
            Optional<CarStock> mockCarStock = Optional.of(carStock);
            Mockito.when(carStockRepository.findById(modelId)).thenReturn(mockCarStock);

            Mockito.when(carReservationRepository.findOverlappedReservationsById(modelId, fromDate, toDate)).thenReturn(1);

            AddReservationRequest request = new AddReservationRequest(modelId, fromDate, toDate);
            Result<AvailableModelsResponse> response = carReservationService.addReservation(request);

            assertEquals(StatusEnum.FAIL.getCode(), response.getCode());
            assertEquals("This car model is not available in this time slot.", response.getMessage());
    }
}