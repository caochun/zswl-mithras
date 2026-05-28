package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/17 14:18
 */
@ApiModel("直接融资-详情信息-返回体")
@Data
public class FundDirectFinancingBaseInfoDetailRSP {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "承销商")
    private String consignee;

    @ApiModelProperty(value = "融资编号")
    private String financingCode;

    @ApiModelProperty(value = "融资金额")
    private Long financingAmount;

    @ApiModelProperty(value = "发行规模")
    private Long issuingScale;

    @ApiModelProperty(value = "项目类型")
    private String directFinancingType;

    @ApiModelProperty(value = "交易流通场所")
    private String tradingVenues;

    @ApiModelProperty(value = "发行方式")
    private String issuanceMethod;

    @ApiModelProperty(value = "存续时间起")
    private LocalDate durationFrom;

    @ApiModelProperty(value = "存续时间止")
    private LocalDate durationTo;

    @ApiModelProperty(value = "首个兑付日")
    private LocalDate firstPaymentDate;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "资金经理")
    private String fundManagerName;

    @ApiModelProperty(value = "所属部门")
    private String deptName;

    @ApiModelProperty(value = "部门负责人")
    private String bizHeaderName;

    @ApiModelProperty(value = "分管领导")
    private String leaderName;
    
    @ApiModelProperty(value = "作废")
    private Boolean obsolete;

    /**
     * FTP收益率
     */
    @ApiModelProperty("FTP收益率")
    private Integer ftpYieldRate;
}
