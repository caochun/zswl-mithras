package cn.zswltech.mithras.dto.monthly;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MonthlyStampDutyFinRSP extends TabBaseRSP {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("融资id")
    private Long financingId;

    @ApiModelProperty("融资类型")
    private String type;

    @ApiModelProperty("月份")
    private String yearAndMonth;

    @ApiModelProperty("融资渠道")
    private String organizationName;

    @ApiModelProperty("融资编号")
    private String financingCode;

    @ApiModelProperty("本月计提印花税/元")
    private Long stampDuty;
}
