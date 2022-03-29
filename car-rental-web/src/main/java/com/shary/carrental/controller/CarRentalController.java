package com.shary.carrental.controller;

import com.shary.carrental.common.StatusEnum;
import com.shary.carrental.dto.*;
import com.shary.carrental.service.CarReservationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Api(value="CarRentalController",tags={"car rental interface"})
@RestController
@RequestMapping("/cars")
public class CarRentalController {

    Logger logger = LoggerFactory.getLogger(CarRentalController.class);

    @Autowired
    CarReservationService carReservationService;

    @ApiOperation(value="get all available models within given target period",notes="Note: please do not include double quote around the value for 'fromDate' and 'toDate'. Example url: https://shary-carrental.azurewebsites.net/availableModels?fromDate=2022-03-25&toDate=2022-03-26")
    @GetMapping("/availability")
    public Result<AvailableModelsResponse> queryAvailableModels(
            @ApiParam(name="fromDate",value="yyyy-MM-dd",required=true)
            @RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @ApiParam(name="toDate",value="yyyy-MM-dd",required=true)
            @RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {

        Result result = null;
        try {
            result = carReservationService.getAvailableModels(fromDate, toDate);
        } catch (IllegalArgumentException ie) {
            logger.error("IllegalArgumentException in queryAvailableModels: ", ie);
            return new Result<>(StatusEnum.FAIL.getCode(), ie.getMessage(), null);
        } catch(Exception e) {
            logger.error("Exception in queryAvailableModels: ", e);
            return new Result<>(StatusEnum.INTERNAL_SERVER_ERROR.getCode(), "Internal Server Error", null);
        }

        return result;
    }

    @ApiOperation(value="add a car reservation",notes="do not include double quote around 'fromDate' and 'toDate'")
    @PostMapping("/reservation")
    public Result addReservation(@RequestBody AddReservationRequest request) {

        Result result = null;
        try {
            result = carReservationService.addReservation(request);
        } catch (IllegalArgumentException ie) {
            logger.error("IllegalArgumentException in addReservation: ", ie);
            return new Result(StatusEnum.FAIL.getCode(), ie.getMessage(), null);
        } catch (Exception e) {
            logger.error("Exception in addReservation: ", e);
            return new Result(StatusEnum.INTERNAL_SERVER_ERROR.getCode(), "Internal Server Error", null);
        }

        return result;
    }

}