package com.shary.carrental.service;

import com.shary.carrental.CarRentalApplication;
import com.shary.carrental.common.StatusEnum;
import com.shary.carrental.dto.AddReservationRequest;
import com.shary.carrental.dto.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CarRentalApplication.class)
class CarReservationServiceTest {

    @Autowired
    private CarReservationService carReservationService;

    @Test
    void getAvailableModels() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Result response = null;
        try {
            Date fromDate = format.parse("2022-03-27");
            Date toDate = format.parse("2022-03-26");
            response = carReservationService.getAvailableModels(fromDate, toDate);
        } catch (Exception e) {}
        assertEquals(StatusEnum.FAIL.getCode(), response.getCode());
        assertEquals("The input param 'fromDate' should be no later than 'toDate'.", response.getMessage());
    }

    @Test
    void addReservation() {
        AddReservationRequest request = new AddReservationRequest();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date fromDate = format.parse("2022-03-27");
            Date toDate = format.parse("2022-03-26");
            request.setModelId(1L);
            request.setFromDate(fromDate);
            request.setToDate(toDate);
        } catch (Exception e) {}
        Result response = carReservationService.addReservation(request);
        assertEquals(StatusEnum.FAIL.getCode(), response.getCode());
        assertEquals("The input param 'fromDate' should be no later than 'toDate'.", response.getMessage());
    }
}