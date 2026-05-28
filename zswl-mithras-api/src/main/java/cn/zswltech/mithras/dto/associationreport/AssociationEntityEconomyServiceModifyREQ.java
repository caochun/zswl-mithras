package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
/**
 * @description 实体经济服务数据表
 * @author vico
 * @date 2025-04-18
 */
@Data
@ApiModel("实体经济服务数据表编辑-请求体")
public class AssociationEntityEconomyServiceModifyREQ {

    /**
    * 自增主键
    */
    @ApiModelProperty(value = "自增主键")
    private Long id;

    /**
    * 行号 | 同一批次数据从1开始递增
    */
    @ApiModelProperty(value = "行号 | 同一批次数据从1开始递增")
    private Integer rowNum;

    /**
    * 企业统一社会信用代码
    */
    @ApiModelProperty(value = "企业统一社会信用代码")
    private String unifSociCredCode;

    /**
    * 本年累计租赁业务金额_期初数(万元)
    */
    @ApiModelProperty(value = "本年累计租赁业务金额_期初数(万元)")
    private BigDecimal tyagLeasBusiAmtAbop;

    /**
    * 本年累计租赁业务金额_本期发生额(万元)
    */
    @ApiModelProperty(value = "本年累计租赁业务金额_本期发生额(万元)")
    private BigDecimal tyagLeasBusiAmtAotc;

    /**
    * 本年累计租赁业务金额_期末数(万元)
    */
    @ApiModelProperty(value = "本年累计租赁业务金额_期末数(万元)")
    private BigDecimal tyagLeasBusiAmtAeop;

    /**
    * 制造业租赁_期初数(万元)
    */
    @ApiModelProperty(value = "制造业租赁_期初数(万元)")
    private BigDecimal tyagMnftLeasAmtAbop;

    /**
    * 制造业租赁_发生额(万元)
    */
    @ApiModelProperty(value = "制造业租赁_发生额(万元)")
    private BigDecimal tyagMnftLeasAmtAotc;

    /**
    * 制造业租赁_期末数(万元)
    */
    @ApiModelProperty(value = "制造业租赁_期末数(万元)")
    private BigDecimal tyagMnftLeasAmtAeop;

    /**
    * 产业链租赁_期初数(万元)
    */
    @ApiModelProperty(value = "产业链租赁_期初数(万元)")
    private BigDecimal tyagIndtLeasAmtAbop;

    /**
    * 产业链租赁_发生额(万元)
    */
    @ApiModelProperty(value = "产业链租赁_发生额(万元)")
    private BigDecimal tyagIndtLeasAmtAotc;

    /**
    * 产业链租赁_期末数(万元)
    */
    @ApiModelProperty(value = "产业链租赁_期末数(万元)")
    private BigDecimal tyagIndtLeasAmtAeop;

    /**
    * 民生消费租赁_期初数(万元)
    */
    @ApiModelProperty(value = "民生消费租赁_期初数(万元)")
    private BigDecimal tyagConsLeasAmtAbop;

    /**
    * 民生消费租赁_发生额(万元)
    */
    @ApiModelProperty(value = "民生消费租赁_发生额(万元)")
    private BigDecimal tyagConsLeasAmtAotc;

    /**
    * 民生消费租赁_期末数(万元)
    */
    @ApiModelProperty(value = "民生消费租赁_期末数(万元)")
    private BigDecimal tyagConsLeasAmtAeop;

    /**
    * 科技金融租赁_期初数(万元)
    */
    @ApiModelProperty(value = "科技金融租赁_期初数(万元)")
    private BigDecimal tyagSatyLeasAmtAbop;

    /**
    * 科技金融租赁_发生额(万元)
    */
    @ApiModelProperty(value = "科技金融租赁_发生额(万元)")
    private BigDecimal tyagSatyLeasAmtAotc;

    /**
    * 科技金融租赁_期末数(万元)
    */
    @ApiModelProperty(value = "科技金融租赁_期末数(万元)")
    private BigDecimal tyagSatyLeasAmtAeop;

    /**
    * 绿色金融租赁_期初数(万元)
    */
    @ApiModelProperty(value = "绿色金融租赁_期初数(万元)")
    private BigDecimal tyagGrenLeasAmtAbop;

    /**
    * 绿色金融租赁_发生额(万元)
    */
    @ApiModelProperty(value = "绿色金融租赁_发生额(万元)")
    private BigDecimal tyagGrenLeasAmtAotc;

    /**
    * 绿色金融租赁_期末数(万元)
    */
    @ApiModelProperty(value = "绿色金融租赁_期末数(万元)")
    private BigDecimal tyagGrenLeasAmtAeop;

    /**
    * 普惠金融租赁_期初数(万元)
    */
    @ApiModelProperty(value = "普惠金融租赁_期初数(万元)")
    private BigDecimal tyagIcveLeasAmtAbop;

    /**
    * 普惠金融租赁_发生额(万元)
    */
    @ApiModelProperty(value = "普惠金融租赁_发生额(万元)")
    private BigDecimal tyagIcveLeasAmtAotc;

    /**
    * 普惠金融租赁_期末数(万元)
    */
    @ApiModelProperty(value = "普惠金融租赁_期末数(万元)")
    private BigDecimal tyagIcveLeasAmtAeop;

    /**
    * 养老金融租赁_期初数(万元)
    */
    @ApiModelProperty(value = "养老金融租赁_期初数(万元)")
    private BigDecimal tyagPensLeasAmtAbop;

    /**
    * 养老金融租赁_发生额(万元)
    */
    @ApiModelProperty(value = "养老金融租赁_发生额(万元)")
    private BigDecimal tyagPensLeasAmtAotc;

    /**
    * 养老金融租赁_期末数(万元)
    */
    @ApiModelProperty(value = "养老金融租赁_期末数(万元)")
    private BigDecimal tyagPensLeasAmtAeop;

    /**
    * 海洋金融租赁_期初数(万元)
    */
    @ApiModelProperty(value = "海洋金融租赁_期初数(万元)")
    private BigDecimal tyagOceaLeasAmtAbop;

    /**
    * 海洋金融租赁_发生额(万元)
    */
    @ApiModelProperty(value = "海洋金融租赁_发生额(万元)")
    private BigDecimal tyagOceaLeasAmtAotc;

    /**
    * 海洋金融租赁_期末数(万元)
    */
    @ApiModelProperty(value = "海洋金融租赁_期末数(万元)")
    private BigDecimal tyagOceaLeasAmtAeop;

    /**
    * 开放金融租赁_期初数(万元)
    */
    @ApiModelProperty(value = "开放金融租赁_期初数(万元)")
    private BigDecimal tyagOpenLeasAmtAbop;

    /**
    * 开放金融租赁_发生额(万元)
    */
    @ApiModelProperty(value = "开放金融租赁_发生额(万元)")
    private BigDecimal tyagOpenLeasAmtAotc;

    /**
    * 开放金融租赁_期末数(万元)
    */
    @ApiModelProperty(value = "开放金融租赁_期末数(万元)")
    private BigDecimal tyagOpenLeasAmtAeop;

    /**
    * 服务客户数_期初数
    */
    @ApiModelProperty(value = "服务客户数_期初数")
    private Integer tyagServCustNumAbop;

    /**
    * 服务客户数_发生额
    */
    @ApiModelProperty(value = "服务客户数_发生额")
    private Integer tyagServCustNumAotc;

    /**
    * 服务客户数_期末数
    */
    @ApiModelProperty(value = "服务客户数_期末数")
    private Integer tyagServCustNumAeop;

    /**
    * 历年租赁金额_期初(万元)
    */
    @ApiModelProperty(value = "历年租赁金额_期初(万元)")
    private BigDecimal oyagLeasBusiAmtAbop;

    /**
    * 历年租赁金额_发生(万元)
    */
    @ApiModelProperty(value = "历年租赁金额_发生(万元)")
    private BigDecimal oyagLeasBusiAmtAotc;

    /**
    * 历年租赁金额_期末(万元)
    */
    @ApiModelProperty(value = "历年租赁金额_期末(万元)")
    private BigDecimal oyagLeasBusiAmtAeop;

    /**
    * 历年客户数_期初
    */
    @ApiModelProperty(value = "历年客户数_期初")
    private Integer oyagServCustNumAbop;

    /**
    * 历年客户数_发生
    */
    @ApiModelProperty(value = "历年客户数_发生")
    private Integer oyagServCustNumAotc;

    /**
    * 历年客户数_期末
    */
    @ApiModelProperty(value = "历年客户数_期末")
    private Integer oyagServCustNumAeop;

    /**
    * 实缴税金_期初(万元)
    */
    @ApiModelProperty(value = "实缴税金_期初(万元)")
    private BigDecimal thsyTaxpAmtAbop;

    /**
    * 实缴税金_发生(万元)
    */
    @ApiModelProperty(value = "实缴税金_发生(万元)")
    private BigDecimal thsyTaxpAmtAotc;

    /**
    * 实缴税金_期末(万元)
    */
    @ApiModelProperty(value = "实缴税金_期末(万元)")
    private BigDecimal thsyTaxpAmtAeop;

    /**
    * 增值税_期初(万元)
    */
    @ApiModelProperty(value = "增值税_期初(万元)")
    private BigDecimal incrTaxAbop;

    /**
    * 增值税_发生(万元)
    */
    @ApiModelProperty(value = "增值税_发生(万元)")
    private BigDecimal incrTaxAotc;

    /**
    * 增值税_期末(万元)
    */
    @ApiModelProperty(value = "增值税_期末(万元)")
    private BigDecimal incrTaxAeop;

    /**
    * 企业所得税_期初(万元)
    */
    @ApiModelProperty(value = "企业所得税_期初(万元)")
    private BigDecimal corpInctAbop;

    /**
    * 企业所得税_发生(万元)
    */
    @ApiModelProperty(value = "企业所得税_发生(万元)")
    private BigDecimal corpInctAotc;

    /**
    * 企业所得税_期末(万元)
    */
    @ApiModelProperty(value = "企业所得税_期末(万元)")
    private BigDecimal corpInctAeop;

    /**
    * 其他税金_期初(万元)
    */
    @ApiModelProperty(value = "其他税金_期初(万元)")
    private BigDecimal othTaxAbop;

    /**
    * 其他税金_发生(万元)
    */
    @ApiModelProperty(value = "其他税金_发生(万元)")
    private BigDecimal othTaxAotc;

    /**
    * 其他税金_期末(万元)
    */
    @ApiModelProperty(value = "其他税金_期末(万元)")
    private BigDecimal othTaxAeop;

    /**
    * 历年税金_期初(万元)
    */
    @ApiModelProperty(value = "历年税金_期初(万元)")
    private BigDecimal otyTaxpAmtAbop;

    /**
    * 历年税金_发生(万元)
    */
    @ApiModelProperty(value = "历年税金_发生(万元)")
    private BigDecimal otyTaxpAmtAotc;

    /**
    * 历年税金_期末(万元)
    */
    @ApiModelProperty(value = "历年税金_期末(万元)")
    private BigDecimal otyTaxpAmtAeop;

    /**
    * 表外业务_期初(万元)
    */
    @ApiModelProperty(value = "表外业务_期初(万元)")
    private BigDecimal ofblAmtAbop;

    /**
    * 表外业务_发生(万元)
    */
    @ApiModelProperty(value = "表外业务_发生(万元)")
    private BigDecimal ofblAmtAotc;

    /**
    * 表外业务_期末(万元)
    */
    @ApiModelProperty(value = "表外业务_期末(万元)")
    private BigDecimal ofblAmtAeop;

    /**
    * 报表实例id | uuid
    */
    @ApiModelProperty(value = "报表实例id | uuid")
    private String reportInstanceId;


}
