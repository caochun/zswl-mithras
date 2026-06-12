package cn.zswltech.mithras.margin.application.port.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MarginRefundPaymentInfo {

    private Long marginId;

    private String marginCode;

    private String ourAccountNumber;

    private String ourAccountBank;

    private LocalDate collectionDate;

    private Long collectionAmount;

    private String collectionType;

    private String bankDetailNo;
}
