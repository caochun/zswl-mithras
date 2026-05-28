package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2022-08-23
 **/

@Data
public class PenaltyInterestListRSP {
    @ApiModelProperty("记录id")
    private Long id;

    @ApiModelProperty("记录时间")
    private Long recordDate;

    @ApiModelProperty("逾期金额")
    private Long overdueAmount;


    @ApiModelProperty("单日产生罚息")
    private Long dayPenaltyInterest;


    @ApiModelProperty("罚息余额")
    private Long lastPenaltyInterest;
}
