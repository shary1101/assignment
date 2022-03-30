package com.shary.carrental.dao;

import com.shary.carrental.domain.CarReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * The JPA repository for {@link CarReservation} entities.
 *
 * @author shary
 */
public interface CarReservationRepository extends JpaRepository<CarReservation, Long> {

    @Query(value="select * from car_reservation " +
            "where from_date >= ?1 and from_date <= ?2 or to_date >= ?1 and to_date <= ?2", nativeQuery=true)
    public List<CarReservation> findOverlappedReservations(Date fromDate, Date toDate);

    @Query(value="select count(*) from car_reservation " +
            "where model_id = ?1 and (from_date >= ?2 and from_date <= ?3 or to_date >= ?2 and to_date <= ?3)", nativeQuery=true)
    public int findOverlappedReservationsById(Long modelId, Date fromDate, Date toDate);
}
