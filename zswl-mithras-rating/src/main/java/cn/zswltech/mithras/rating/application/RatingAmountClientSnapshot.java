package cn.zswltech.mithras.rating.application;

import lombok.Data;

@Data
public class RatingAmountClientSnapshot {

    private Long id;

    private String clientName;

    private String clientCode;

    private String uscCode;

    private Long belongDeptId;

    private Long belongSponsorId;
}
