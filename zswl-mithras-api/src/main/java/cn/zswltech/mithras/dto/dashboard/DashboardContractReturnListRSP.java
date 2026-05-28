package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DashboardContractReturnListRSP extends DashboardApprovalBaseRSP {


    //运营总退回次数
    @ApiModelProperty("运营总退回次数")
    private Integer yunYingReturnCount;

    @ApiModelProperty("运营经办")
    private String yunYingGuanLiName;

    @ApiModelProperty("运营复核")
    private String yunYingGuanLiReviewName;

    // 退回节点+第X次退回（第1次退回是否可默认不展示从第2次退回时展示？）：流程备注
    @ApiModelProperty("退回原因")
    private String reasonReturn;

}
