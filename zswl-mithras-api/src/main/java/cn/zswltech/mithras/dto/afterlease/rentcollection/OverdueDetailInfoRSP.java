package cn.zswltech.mithras.dto.afterlease.rentcollection;

import cn.zswltech.mithras.dto.collection.CollectionPenaltyInterestRSP;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2022-11-17
 **/

@Data
public class OverdueDetailInfoRSP extends CollectionPenaltyInterestRSP {

    @ApiModelProperty("是否通知财务系统收款")
    private String status;

}
