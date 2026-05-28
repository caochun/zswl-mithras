package cn.zswltech.mithras.dto.collection;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CollectionFlowCenterBusinessPaymentListREQ extends PageReq {

    @ApiModelProperty("计划付款日期-从")
    private LocalDate applyPaymentDateFrom;

    @ApiModelProperty("计划付款日期-到")
    private LocalDate applyPaymentDateTo;

    @ApiModelProperty("客户id")
    private Integer clientId;

    @ApiModelProperty("核销状态")
    private List<String> writeOffStatusList;

    private String writeOffStatus;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("现金流项目 PaymentFlowItemEnum")
    private List<String> cashFlowItemList;

    private String cashFlowItem;

    @ApiModelProperty("业务部门id")
    private Long bizDeptId;


}
