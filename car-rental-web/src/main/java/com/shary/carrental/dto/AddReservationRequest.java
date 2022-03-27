package com.shary.carrental.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="AddReservationRequest",description="to reserve a car model for a period of time")
public class AddReservationRequest {
    @ApiModelProperty(value="modelId",name="long value",example="1",required=true)
    long modelId;
    @ApiModelProperty(value="fromDate",name="YYYY-MM-DD",example="2022-03-26",required=true)
    Date fromDate;
    @ApiModelProperty(value="toDate",name="YYYY-MM-DD",example="2022-03-27",required=true)
    Date toDate;
}
