package com.shary.carrental.service.impl;

import com.shary.carrental.common.StatusEnum;
import com.shary.carrental.domain.CarReservation;
import com.shary.carrental.dao.CarReservationRepository;
import com.shary.carrental.domain.CarStock;
import com.shary.carrental.dao.CarStockRepository;
import com.shary.carrental.dto.AddReservationRequest;
import com.shary.carrental.dto.AvailableModel;
import com.shary.carrental.dto.AvailableModelsResponse;
import com.shary.carrental.dto.Result;
import com.shary.carrental.service.CarReservationService;
import com.shary.carrental.util.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author shary
 */
@Service
public class CarReservationServiceImpl implements CarReservationService {

    @Autowired
    private CarStockRepository carStockRepository;

    @Autowired
    private CarReservationRepository carReservationRepository;

    public Result<AvailableModelsResponse> getAvailableModels(Date fromDate, Date toDate) {
        if (!Utilities.isValidDates(fromDate, toDate)) {
            throw new IllegalArgumentException("The input param 'fromDate' should be no later than 'toDate'.");
        }
        List<AvailableModel> availableModels = null;

        // find car reservations already reserved within the target period
        List<CarReservation> reserved = carReservationRepository.findOverlappedReservations(fromDate, toDate);

        // group reserved cars by model id and calculate count for each
        Map<Long, Long> reservedNum = reserved.stream()
                .collect(java.util.stream.Collectors.groupingBy(CarReservation::getModelId,
                        Collectors.counting()));

        List<CarStock> carStocks = carStockRepository.findAll();
        // drop the out-of-stock models and build output with remaining ones
        availableModels = carStocks.stream()
                .filter(carStock -> isModelAvailable(carStock, reservedNum))
                .map(carStock -> populateAvailableModel(carStock, reservedNum))
                .collect(Collectors.toList());

        return new Result(StatusEnum.SUCCESS.getCode(), null, new AvailableModelsResponse(availableModels));
    }

    public Result addReservation(AddReservationRequest request) {
        validateAddReservationRequest(request);

        Result result = null;
        long modelId = request.getModelId();
        Date fromDate = request.getFromDate();
        Date toDate = request.getToDate();

        CarReservation carReservation = populateCarReservation(modelId, fromDate, toDate);

        Optional<CarStock> carStock = carStockRepository.findById(modelId);
        if (!carStock.isPresent()) {
            throw new IllegalArgumentException("This car model is not provided in our company.");
        }

        // use synchronized to deal with concurrency, better to use redis in distributed environment
        synchronized (this) {
            // number of already reserved cars for given model
            int reserved = carReservationRepository.findOverlappedReservationsById(modelId, fromDate, toDate);
            int stock = carStock.get().getStock();

            if (stock > reserved) {
                // has stock, save the record into DB
                CarReservation newReservation = carReservationRepository.save(carReservation);

                result = newReservation == null? new Result(StatusEnum.FAIL.getCode(), "Reservation Failed.", null)
                        : new Result(StatusEnum.SUCCESS.getCode(), "Reservation is successful.", null);
            } else {
                // out of stock
                result = new Result(StatusEnum.FAIL.getCode(), "This car model is not available in this time slot.", null);
            }
        }

        return result;
    }

    private void validateAddReservationRequest(AddReservationRequest request) {
        if (request.getModelId() == null) {
            throw new IllegalArgumentException("'modelId' is not provided");
        }
        if (request.getFromDate() == null) {
            throw new IllegalArgumentException("'fromDate' is not provided");
        }
        if (request.getToDate() == null) {
            throw new IllegalArgumentException("'toDate' is not provided");
        }
        if (!Utilities.isValidDates(request.getFromDate(), request.getToDate())) {
            throw new IllegalArgumentException("The input param 'fromDate' should be no later than 'toDate'.");
        }
    }

    private CarReservation populateCarReservation(Long modelId, Date fromDate, Date toDate) {
        CarReservation carReservation = new CarReservation();
        carReservation.setModelId(modelId);
        carReservation.setFromDate(fromDate);
        carReservation.setToDate(toDate);

        return carReservation;
    }

    private AvailableModel populateAvailableModel(CarStock carStock, Map<Long, Long> reservedNum) {
        AvailableModel availableModel = new AvailableModel();

        availableModel.setModelId(carStock.getId());
        availableModel.setModelName(carStock.getModelName());
        int numLeft = !reservedNum.containsKey(carStock.getId())? carStock.getStock(): carStock.getStock() - reservedNum.get(carStock.getId()).intValue();
        availableModel.setNumLeft(numLeft);

        return availableModel;
    }

    private boolean isModelAvailable(CarStock carStock, Map<Long, Long> reservedNum) {
        return !reservedNum.containsKey(carStock.getId())
                || reservedNum.get(carStock.getId()) < carStock.getStock();
    }

}
