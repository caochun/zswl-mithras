package cn.zswltech.mithras.api.riskcontrol.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author yibin
 */
@Data
@ApiModel("集中度报送列表-返回体")
public class JzdReportListRSP {

    @ApiModelProperty("id")
    private Long id;


    @ApiModelProperty("数据时点")
    private LocalDate dataMonth;

    @ApiModelProperty("业务类型")
    private String bizType;

    @ApiModelProperty("标的物名称;根据业务类型联动显示，不可编辑")
    private String targetSubject;

    @ApiModelProperty("业务总额（万元）")
    private Long bizAmountTotal;

    @ApiModelProperty("业务余额（万元）")
    private Long bizAmountLeft;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("是否同业客户")
    private String clientSameTrade;

    @ApiModelProperty("企业经济成分")
    private String economicComposition;

    @ApiModelProperty("主办业务部门")
    private String sponsorOrgName;

    @ApiModelProperty("业务起始日期")
    private LocalDate bizStartDate;

    @ApiModelProperty("业务到期日")
    private LocalDate bizEndDate;

    @ApiModelProperty("合同保证价值（万元）")
    private Long ensureValue;

    @ApiModelProperty("担保人名称")
    private String guaranteeName;

    @ApiModelProperty("已计提减值（万元）")
    private Long yjtjzValue;

    @ApiModelProperty("逾期天数")
    private Long overdueDays;

    @ApiModelProperty("逾期金额")
    private Long overdueValue;

    @ApiModelProperty("资产质量分类")
    private String assetsCategory;

    @ApiModelProperty("报送状态")
    private String reportStatus;

    @ApiModelProperty("创建类型")
    private String createType;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

}
