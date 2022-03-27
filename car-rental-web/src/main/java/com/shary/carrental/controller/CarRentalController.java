package com.shary.carrental.controller;

import com.shary.carrental.common.StatusEnum;
//import com.shary.carrental.dao.CarStock;
import com.shary.carrental.dto.*;
import com.shary.carrental.service.CarReservationService;
import com.shary.carrental.service.CarStockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
//import java.util.List;

@Api(value="CarRentalController",tags={"car rental interface"})
@RestController
public class CarRentalController {

    @Autowired
    CarReservationService carReservationService;
    @Autowired
    CarStockService carStockService;

//    @GetMapping("/test")
//    public String test(String message) {
//        return "Congrats! Your app is deployed successfully in Azure!";
//    }

    @ApiOperation(value="get all available models within given target period",notes="Note: please do not include double quote around the value for 'fromDate' and 'toDate'. Example url: https://shary-carrental.azurewebsites.net/availableModels?fromDate=2022-03-25&toDate=2022-03-26")
    @GetMapping("/availableModels")
    public Result<AvailableModelsResponse> queryAvailableModels(
            @ApiParam(name="fromDate",value="yyyy-MM-dd",required=true)
            @RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @ApiParam(name="toDate",value="yyyy-MM-dd",required=true)
            @RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {

        Result result = null;
        AvailableModelsResponse response = null;
        try {
            result = carReservationService.getAvailableModels(fromDate, toDate);
        } catch(Exception e) {
            return new Result<>(StatusEnum.INTERNAL_SERVER_ERROR.getCode(), "Internal Server Error", null);
        }

        return result;
    }

    @ApiOperation(value="add a car reservation",notes="do not include double quote around 'fromDate' and 'toDate'")
    @PostMapping("/reserve")
    public Result addReservation(@RequestBody AddReservationRequest request) {

        Result result = null;
        try {
            result = carReservationService.addReservation(request);
        } catch (IllegalArgumentException ie) {
            return new Result<>(StatusEnum.FAIL.getCode(), ie.getMessage(), null);
        } catch (Exception e) {
            return new Result<>(StatusEnum.INTERNAL_SERVER_ERROR.getCode(), "Internal Server Error", null);
        }

        return result;
    }

//    @GetMapping("/carStocks")
//    @ResponseBody
//    public List<CarStock> findAllCarStocks() {
//        return carStockService.findAllCarStocks();
//    }
//
//    @PostMapping("/carStock")
//    public CarStock addCarStock(@RequestBody CarStock carStock) {
//        return carStockService.addCarStock(carStock);
//    }
}