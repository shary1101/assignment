package com.shary.carrental.service;

import com.shary.carrental.common.StatusEnum;
import com.shary.carrental.dao.CarReservation;
import com.shary.carrental.dao.CarReservationRepository;
import com.shary.carrental.dao.CarStock;
import com.shary.carrental.dao.CarStockRepository;
import com.shary.carrental.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarReservationService {

    @Autowired
    private CarStockRepository carStockRepository;

    @Autowired
    private CarReservationRepository carReservationRepository;


    public Result getAvailableModels(Date fromDate, Date toDate) {

        if (fromDate.compareTo(toDate) > 0) {
            return new Result(StatusEnum.FAIL.getStatus(), "The input param 'fromDate' should be no later than 'toDate'.", null);
        }
        List<AvailableModel> availableModels = null;
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

        return new Result(StatusEnum.SUCCESS.getStatus(), null, new AvailableModelsResponse(availableModels));
    }

    public Result addReservation(AddReservationRequest request) {

        Result result = null;
        long modelId = request.getModelId();
        Date fromDate = request.getFromDate();
        Date toDate = request.getToDate();
        if (fromDate.compareTo(toDate) > 0) {
            return new Result(StatusEnum.FAIL.getStatus(), "The input param 'fromDate' should be no later than 'toDate'.", null);
        }

        CarReservation carReservation = populateCarReservation(modelId, fromDate, toDate);

        Optional<CarStock> carStock = carStockRepository.findById(modelId);
        if (!carStock.isPresent()) {
             return new Result(StatusEnum.FAIL.getStatus(), "This car model is not provided in our company.", null);
        }

        int stock = carStock.get().getStock();
        // use synchronized to deal with concurrency, better to use redis in distributed environment
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
        
        return result;
    }

    private CarReservation populateCarReservation(Long modelId, Date fromDate, Date toDate) {
        CarReservation carReservation = new CarReservation();
        carReservation.setModelId(modelId);
        carReservation.setFromDate(fromDate);
        carReservation.setToDate(toDate);

        return carReservation;
    }

}
