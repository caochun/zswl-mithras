package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author dingqi
 * @date 2025/4/22
 * @description
 */
@Data
public class AssociationDetailEntityEconomyServiceRSP extends AssociationDetailBaseRSP {

    /**
     * 自增主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty("本年累计租赁业务金额_期初数(万元)")
    private BigDecimal tyagLeasBusiAmtAbop;

    @ApiModelProperty("本年累计租赁业务金额_本期发生额(万元)")
    private BigDecimal tyagLeasBusiAmtAotc;

    @ApiModelProperty("本年累计租赁业务金额_期末数(万元)")
    private BigDecimal tyagLeasBusiAmtAeop;

    @ApiModelProperty("制造业租赁_期初数(万元)")
    private BigDecimal tyagMnftLeasAmtAbop;

    @ApiModelProperty("制造业租赁_发生额(万元)")
    private BigDecimal tyagMnftLeasAmtAotc;

    @ApiModelProperty("制造业租赁_期末数(万元)")
    private BigDecimal tyagMnftLeasAmtAeop;

    @ApiModelProperty("产业链租赁_期初数(万元)")
    private BigDecimal tyagIndtLeasAmtAbop;

    @ApiModelProperty("产业链租赁_发生额(万元)")
    private BigDecimal tyagIndtLeasAmtAotc;

    @ApiModelProperty("产业链租赁_期末数(万元)")
    private BigDecimal tyagIndtLeasAmtAeop;

    @ApiModelProperty("民生消费租赁_期初数(万元)")
    private BigDecimal tyagConsLeasAmtAbop;

    @ApiModelProperty("民生消费租赁_发生额(万元)")
    private BigDecimal tyagConsLeasAmtAotc;

    @ApiModelProperty("民生消费租赁_期末数(万元)")
    private BigDecimal tyagConsLeasAmtAeop;

    @ApiModelProperty("科技金融租赁_期初数(万元)")
    private BigDecimal tyagSatyLeasAmtAbop;

    @ApiModelProperty("科技金融租赁_发生额(万元)")
    private BigDecimal tyagSatyLeasAmtAotc;

    @ApiModelProperty("科技金融租赁_期末数(万元)")
    private BigDecimal tyagSatyLeasAmtAeop;

    @ApiModelProperty("绿色金融租赁_期初数(万元)")
    private BigDecimal tyagGrenLeasAmtAbop;

    @ApiModelProperty("绿色金融租赁_发生额(万元)")
    private BigDecimal tyagGrenLeasAmtAotc;

    @ApiModelProperty("绿色金融租赁_期末数(万元)")
    private BigDecimal tyagGrenLeasAmtAeop;

    @ApiModelProperty("普惠金融租赁_期初数(万元)")
    private BigDecimal tyagIcveLeasAmtAbop;

    @ApiModelProperty("普惠金融租赁_发生额(万元)")
    private BigDecimal tyagIcveLeasAmtAotc;

    @ApiModelProperty("普惠金融租赁_期末数(万元)")
    private BigDecimal tyagIcveLeasAmtAeop;

    @ApiModelProperty("养老金融租赁_期初数(万元)")
    private BigDecimal tyagPensLeasAmtAbop;

    @ApiModelProperty("养老金融租赁_发生额(万元)")
    private BigDecimal tyagPensLeasAmtAotc;

    @ApiModelProperty("养老金融租赁_期末数(万元)")
    private BigDecimal tyagPensLeasAmtAeop;

    @ApiModelProperty("海洋金融租赁_期初数(万元)")
    private BigDecimal tyagOceaLeasAmtAbop;

    @ApiModelProperty("海洋金融租赁_发生额(万元)")
    private BigDecimal tyagOceaLeasAmtAotc;

    @ApiModelProperty("海洋金融租赁_期末数(万元)")
    private BigDecimal tyagOceaLeasAmtAeop;

    @ApiModelProperty("开放金融租赁_期初数(万元)")
    private BigDecimal tyagOpenLeasAmtAbop;

    @ApiModelProperty("开放金融租赁_发生额(万元)")
    private BigDecimal tyagOpenLeasAmtAotc;

    @ApiModelProperty("开放金融租赁_期末数(万元)")
    private BigDecimal tyagOpenLeasAmtAeop;

    @ApiModelProperty("服务客户数_期初数")
    private Integer tyagServCustNumAbop;

    @ApiModelProperty("服务客户数_发生额")
    private Integer tyagServCustNumAotc;

    @ApiModelProperty("服务客户数_期末数")
    private Integer tyagServCustNumAeop;

    @ApiModelProperty("历年租赁金额_期初(万元)")
    private BigDecimal oyagLeasBusiAmtAbop;

    @ApiModelProperty("历年租赁金额_发生(万元)")
    private BigDecimal oyagLeasBusiAmtAotc;

    @ApiModelProperty("历年租赁金额_期末(万元)")
    private BigDecimal oyagLeasBusiAmtAeop;

    @ApiModelProperty("历年客户数_期初")
    private Integer oyagServCustNumAbop;

    @ApiModelProperty("历年客户数_发生")
    private Integer oyagServCustNumAotc;

    @ApiModelProperty("历年客户数_期末")
    private Integer oyagServCustNumAeop;

    @ApiModelProperty("实缴税金_期初(万元)")
    private BigDecimal thsyTaxpAmtAbop;

    @ApiModelProperty("实缴税金_发生(万元)")
    private BigDecimal thsyTaxpAmtAotc;

    @ApiModelProperty("实缴税金_期末(万元)")
    private BigDecimal thsyTaxpAmtAeop;

    @ApiModelProperty("增值税_期初(万元)")
    private BigDecimal incrTaxAbop;

    @ApiModelProperty("增值税_发生(万元)")
    private BigDecimal incrTaxAotc;

    @ApiModelProperty("增值税_期末(万元)")
    private BigDecimal incrTaxAeop;

    @ApiModelProperty("企业所得税_期初(万元)")
    private BigDecimal corpInctAbop;

    @ApiModelProperty("企业所得税_发生(万元)")
    private BigDecimal corpInctAotc;

    @ApiModelProperty("企业所得税_期末(万元)")
    private BigDecimal corpInctAeop;

    @ApiModelProperty("其他税金_期初(万元)")
    private BigDecimal othTaxAbop;

    @ApiModelProperty("其他税金_发生(万元)")
    private BigDecimal othTaxAotc;

    @ApiModelProperty("其他税金_期末(万元)")
    private BigDecimal othTaxAeop;

    @ApiModelProperty("历年税金_期初(万元)")
    private BigDecimal otyTaxpAmtAbop;

    @ApiModelProperty("历年税金_发生(万元)")
    private BigDecimal otyTaxpAmtAotc;

    @ApiModelProperty("历年税金_期末(万元)")
    private BigDecimal otyTaxpAmtAeop;

    @ApiModelProperty("表外业务_期初(万元)")
    private BigDecimal ofblAmtAbop;

    @ApiModelProperty("表外业务_发生(万元)")
    private BigDecimal ofblAmtAotc;

    @ApiModelProperty("表外业务_期末(万元)")
    private BigDecimal ofblAmtAeop;
}
