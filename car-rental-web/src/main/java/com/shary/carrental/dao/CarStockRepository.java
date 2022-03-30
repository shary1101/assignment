package com.shary.carrental.dao;

import com.shary.carrental.domain.CarStock;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The JPA repository for {@link CarStock} entities.
 *
 * @author shary
 */
public interface CarStockRepository extends JpaRepository<CarStock, Long> {

}
