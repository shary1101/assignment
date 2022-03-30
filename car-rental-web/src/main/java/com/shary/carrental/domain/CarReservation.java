package com.shary.carrental.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * This entity represents a specific reservation for a car model
 *
 */
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CarReservation implements Serializable {

    @Id
    @GeneratedValue
    private long id;
    private long modelId;
    private Date fromDate;
    private Date toDate;
}