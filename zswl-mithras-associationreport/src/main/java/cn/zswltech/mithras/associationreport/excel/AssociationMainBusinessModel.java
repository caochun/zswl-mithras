package cn.zswltech.mithras.associationreport.excel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/4/22
 * @description 金融协会报送-主要业务清单表实体类
 */
@Data
public class AssociationMainBusinessModel extends AssociationReportBaseModel  {

    /**
     * 行号 | 同一批次数据从1开始递增
     */
    @ApiModelProperty("row_num")
    private Integer rowNum;

    /**
     * 企业统一社会信用代码
     */
    @ApiModelProperty("unif_soci_cred_code")
    private String unifSociCredCode;

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
    
    @ApiModelProperty("合同类型")
    private String agmtTypeCode;

    @ApiModelProperty("租赁物类型")
    private String lasdType;
    
    @ApiModelProperty("项目行业分类")
    private String projIndtClasCode;
    
    @ApiModelProperty("客户姓名")
    private String custName;

    @ApiModelProperty("客户证件号码")
    private String custCertNum;

    @ApiModelProperty("客户规模")
    private String custScalCode;
    
    @ApiModelProperty("融资租赁投放额")
    private BigDecimal fnlRels;

    @ApiModelProperty("收回本金")
    private BigDecimal wthdPrin;
    
    @ApiModelProperty("租金余额")
    private BigDecimal rentBal;
    
    @ApiModelProperty("综合融资成本")
    private BigDecimal cmphFinCost;
    
    @ApiModelProperty("增信情况")
    private String udpnSituCode;
    
    @ApiModelProperty("增信方")
    private String udpn;
    
    @ApiModelProperty("逾期租金")
    private BigDecimal ovduRent;

    @ApiModelProperty("逾期天数")
    private String ovduDaysCode;
    
    @ApiModelProperty("逾期处置情况")
    private String ovduDspsProg;
    
    @ApiModelProperty("是否纳入不良")
    private String npFlag;
    
    @ApiModelProperty("不良余额")
    private BigDecimal npBal;
    
    @ApiModelProperty("客户数量")
    private Integer custVol;

}
