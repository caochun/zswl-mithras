package cn.zswltech.mithras.dto.rating.ratingamount;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class RatingAmountDetailRSP {

    @ApiModelProperty(value = "债项评级id")
    private Long id;

    private Long reportId;

    private Long snapshotId;

    private Long clientId;

    private String clientUscCode;

    private Long projReviewId;

    private String projCode;

    private String projName;

    private Long evaluationSubjectId;

    private String evaluationSubjectName;

    private Boolean projSystem;

    private Boolean materialLeaseItem;

    private String modelCode;

    private String modelName;

    private String projQuota;

    private Long belongDeptId;

    private Long belongSponsorUserId;

    private String processStatus;

    private Boolean ratingStatus;

    private LocalDate effectTime;

    private LocalDate abandonTime;

    /**
     * 是否需要房地产调整
     */
    private Boolean isRealEstateAdjust;

    /**
     * 是否需要股权调整
     */
    private Boolean isStockRightsAdjust;


}
