package cn.zswltech.mithras.dto.monthly;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MonthlyStampDutyProjRSP extends TabBaseRSP {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("月份")
    private String yearAndMonth;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("本月计提印花税")
    private Long stampDuty;




}
