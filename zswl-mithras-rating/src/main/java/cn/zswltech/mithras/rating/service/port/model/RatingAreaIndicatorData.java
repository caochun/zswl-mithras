package cn.zswltech.mithras.rating.service.port.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RatingAreaIndicatorData {

    private int year;

    private String indicatorCode;

    private BigDecimal indicatorValue;
}
