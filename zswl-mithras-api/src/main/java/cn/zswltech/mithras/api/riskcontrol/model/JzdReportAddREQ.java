package cn.zswltech.mithras.api.riskcontrol.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author yibin
 */
@Data
@ApiModel("集中度报送-手动添加-请求体")
public class JzdReportAddREQ {

    @NotNull
    @ApiModelProperty("数据时点")
    private LocalDate dataMonth;

    @NotBlank
    @ApiModelProperty("业务类型")
    private String bizType;

    @NotBlank
    @ApiModelProperty("标的物名称;根据业务类型联动显示，不可编辑")
    private String targetSubject;

    @NotNull
    @ApiModelProperty("业务总额（万元）")
    private Long bizAmountTotal;

    @NotNull
    @ApiModelProperty("业务余额（万元）")
    private Long bizAmountLeft;

    @NotNull
    @ApiModelProperty("客户名称")
    private String clientName;

    @NotNull
    @ApiModelProperty("是否同业客户")
    private String clientSameTrade;

    @NotNull
    @ApiModelProperty("企业经济成分")
    private String economicComposition;

    @NotNull
    @ApiModelProperty("主办业务部门")
    private String sponsorOrgName;

    @NotNull
    @ApiModelProperty("业务起始日期")
    private LocalDate bizStartDate;

    @NotNull
    @ApiModelProperty("业务到期日")
    private LocalDate bizEndDate;

    @NotNull
    @ApiModelProperty("合同保证价值（万元）")
    private Long ensureValue;

    @ApiModelProperty("担保人名称")
    private String guaranteeName;

    @NotNull
    @ApiModelProperty("已计提减值（万元）")
    private Long yjtjzValue;

    @NotNull
    @ApiModelProperty("逾期天数")
    private Long overdueDays;

    @NotNull
    @ApiModelProperty("逾期金额")
    private Long overdueValue;

    @NotBlank
    @ApiModelProperty("资产质量分类")
    private String assetsCategory;

    @NotBlank
    @ApiModelProperty("创建类型")
    private String createType;

}
