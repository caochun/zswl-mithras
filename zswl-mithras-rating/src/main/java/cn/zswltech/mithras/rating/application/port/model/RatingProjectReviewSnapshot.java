package cn.zswltech.mithras.rating.application.port.model;

import lombok.Data;

@Data
public class RatingProjectReviewSnapshot {

    private Long id;

    private String projName;

    private String projCode;

    private Long clientId;

    private Long evaluationSubjectId;

    private String lesseeInfo;

    private String riskControlIndustryClassify;

    private String province;

    private String city;

    private String district;

    private String leaseTypes;

    private String regionalProjectClassify;

    private String projectClassify;

    private String fundsPurpose;

    private String bizType;
}
