package cn.zswltech.mithras.service.service;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CollectionRentActualReceiptStatus {

    private Boolean received;

    private LocalDate receivedDate;

    private Long receivedAmount;
}
