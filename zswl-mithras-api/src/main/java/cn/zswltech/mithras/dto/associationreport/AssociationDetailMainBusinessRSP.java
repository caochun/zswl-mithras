package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/4/22
 * @description
 */
@Data
public class AssociationDetailMainBusinessRSP extends AssociationDetailBaseRSP {

    /**
     * 自增主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty("序号")
    private String onum;
    
    @ApiModelProperty("合同名称")
    private String agmtName;
    
    @ApiModelProperty("合同编号")
    private String agmtNo;
    
    @ApiModelProperty("合同签订日期")
    private LocalDate agmtSignDate;
    
    @ApiModelProperty("协议到期日期")
    private LocalDate agmtMatuDate;
    
    @ApiModelProperty("合同类型code")
    private String agmtTypeCode;

    @ApiModelProperty("合同类型display")
    private String agmtTypeDisplay;

    @ApiModelProperty("租赁物类型")
    private String lasdType;
    
    @ApiModelProperty("项目行业分类code")
    private String projIndtClasCode;

    @ApiModelProperty("项目行业分类display")
    private String projIndtClasDisplay;
    
    @ApiModelProperty("客户姓名")
    private String custName;

    @ApiModelProperty("客户证件号码")
    private String custCertNum;

    @ApiModelProperty("客户规模code")
    private String custScalCode;

    @ApiModelProperty("客户规模display")
    private String custScalDisplay;
    
    @ApiModelProperty("融资租赁投放额")
    private BigDecimal fnlRels;

    @ApiModelProperty("收回本金")
    private BigDecimal wthdPrin;
    
    @ApiModelProperty("租金余额")
    private BigDecimal rentBal;
    
    @ApiModelProperty("综合融资成本")
    private BigDecimal cmphFinCost;
    
    @ApiModelProperty("增信情况code")
    private String udpnSituCode;

    @ApiModelProperty("增信情况display")
    private String udpnSituDisplay;
    
    @ApiModelProperty("增信方")
    private String udpn;
    
    @ApiModelProperty("逾期租金")
    private BigDecimal ovduRent;

    @ApiModelProperty("逾期天数code")
    private String ovduDaysCode;

    @ApiModelProperty("逾期天数display")
    private String ovduDaysDisplay;
    
    @ApiModelProperty("逾期处置情况")
    private String ovduDspsProg;
    
    @ApiModelProperty("是否纳入不良")
    private String npFlag;

    @ApiModelProperty("是否纳入不良display")
    private String npFlagDisplay;
    
    @ApiModelProperty("不良余额")
    private BigDecimal npBal;
    
    @ApiModelProperty("客户数量")
    private Integer custVol;
}
