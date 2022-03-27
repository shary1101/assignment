package com.shary.carrental.service;

import com.shary.carrental.dao.CarStock;
import com.shary.carrental.dao.CarStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarStockService {

    @Autowired
    CarStockRepository carStockRepository;

    public List<CarStock> findAllCarStocks() {
        return carStockRepository.findAll();
    }

    public CarStock addCarStock(CarStock carStock) {
        return carStockRepository.save(carStock);
    }
}
