package com.shary.carrental.controller;

import com.shary.carrental.common.StatusEnum;
import com.shary.carrental.dao.CarReservation;
import com.shary.carrental.dao.CarReservationRepository;
import com.shary.carrental.dao.CarStock;
import com.shary.carrental.dao.CarStockRepository;
import com.shary.carrental.dto.AddReservationRequest;
import com.shary.carrental.dto.AvailableModel;
import com.shary.carrental.dto.AvailableModelsRequest;
import com.shary.carrental.dto.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

import java.util.*;

@RestController
public class CarRentalController {

    @Autowired
    private CarStockRepository carStockRepository;

    @Autowired
    private CarReservationRepository carReservationRepository;

    @GetMapping("/test")
    public String test(String message) {
        return "Congrats! Your app is deployed successfully in Azure!";
    }

    @GetMapping("/availableModels")
    public Result<List<AvailableModel>> queryAvailableModels(@RequestBody AvailableModelsRequest request) {

        Result<List<AvailableModel>> result = null;
        List<AvailableModel> availableModels = null;
        try {
            Date fromDate = request.getFromDate();
            Date toDate = request.getToDate();

            // find car reservations already reserved within given time slot
            List<CarReservation> reserved = carReservationRepository.findOverlappedReservations(fromDate, toDate);
            // group reserved cars by model id and calculate count for each
            Map<Long, Long> reservedNum = reserved.stream().
                    collect(java.util.stream.Collectors.groupingBy(CarReservation::getModelId,
                            Collectors.counting()));

            List<CarStock> carStocks = carStockRepository.findAll();
            // drop the out-of-stock models and build output with remaining ones
            availableModels = carStocks.stream().filter(carStock ->!(reservedNum.containsKey(carStock.getId())
                            && reservedNum.get(carStock.getId()) == carStock.getStock())).
                    map(carStock -> {
                                AvailableModel availableModel = new AvailableModel();
                                availableModel.setModelName(carStock.getModelName());
                                int numLeft = !reservedNum.containsKey(carStock.getId())? carStock.getStock(): carStock.getStock() - reservedNum.get(carStock.getId()).intValue();
                                availableModel.setNumLeft(numLeft);
                                return availableModel;
                            }).collect(Collectors.toList());

        } catch(Exception e) {
            result = new Result<>(StatusEnum.FAIL.getStatus(), "Internal Server Error: " + e.getMessage(), null);
        }

        return new Result<>(StatusEnum.SUCCESS.getStatus(), null, availableModels);
    }

    @PostMapping("/reserve")
    public Result addReservation(@RequestBody AddReservationRequest request) {
        Result result = null;

        try {
            long modelId = request.getModelId();
            Date fromDate = request.getFromDate();
            Date toDate = request.getToDate();
            CarReservation carReservation = populateCarReservation(modelId, fromDate, toDate);

            Optional<CarStock> carStock = carStockRepository.findById(modelId);
            if (!carStock.isPresent()) {
                return new Result(StatusEnum.FAIL.getStatus(), "This car model is not provided in our company.", null);
            }

            int stock = carStock.get().getStock();
            synchronized (this) {
                // number of already reserved cars for given model
                int reserved = carReservationRepository.findOverlappedReservationsById(modelId, fromDate, toDate);

                if (stock > reserved) {
                    // has stock, save the record into DB
                    CarReservation newReservation = carReservationRepository.save(carReservation);
                    result = newReservation == null? new Result(StatusEnum.FAIL.getStatus(), "Reservation Failed.", null)
                            : new Result(StatusEnum.SUCCESS.getStatus(), "Reservation is successful.", null);
                } else {
                    result = new Result(StatusEnum.FAIL.getStatus(), "This car model is not available in this time slot.", null);
                }
            }
        } catch (Exception e) {
            result = new Result<>(StatusEnum.FAIL.getStatus(), "Internal Server Error: " + e.getMessage(), null);
        }

        return result;
    }

    @GetMapping("/carStocks")
    @ResponseBody
    public List<CarStock> findAllCarStocks() {
        return carStockRepository.findAll();
    }

    @PostMapping("/carStock")
    public CarStock addCarStock(@RequestBody CarStock carStock) {
        return carStockRepository.save(carStock);
    }

    public CarReservation populateCarReservation(Long modelId, Date fromDate, Date toDate) {
        CarReservation carReservation = new CarReservation();
        carReservation.setModelId(modelId);
        carReservation.setFromDate(fromDate);
        carReservation.setToDate(toDate);

        return carReservation;
    }

}