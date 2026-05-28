package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description 实体经济服务数据表
 * @author vico
 * @date 2025-04-18
 */
@Data
@ApiModel("实体经济服务数据表新增-请求体")
public class AssociationEntityEconomyServiceAddREQ {

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
    private Double tyagLeasBusiAmtAbop;

    /**
    * 本年累计租赁业务金额_本期发生额(万元)
    */
    @ApiModelProperty(value = "本年累计租赁业务金额_本期发生额(万元)")
    private Double tyagLeasBusiAmtAotc;

    /**
    * 本年累计租赁业务金额_期末数(万元)
    */
    @ApiModelProperty(value = "本年累计租赁业务金额_期末数(万元)")
    private Double tyagLeasBusiAmtAeop;

    /**
    * 制造业租赁_期初数(万元)
    */
    @ApiModelProperty(value = "制造业租赁_期初数(万元)")
    private Double tyagMnftLeasAmtAbop;

    /**
    * 制造业租赁_发生额(万元)
    */
    @ApiModelProperty(value = "制造业租赁_发生额(万元)")
    private Double tyagMnftLeasAmtAotc;

    /**
    * 制造业租赁_期末数(万元)
    */
    @ApiModelProperty(value = "制造业租赁_期末数(万元)")
    private Double tyagMnftLeasAmtAeop;

    /**
    * 产业链租赁_期初数(万元)
    */
    @ApiModelProperty(value = "产业链租赁_期初数(万元)")
    private Double tyagIndtLeasAmtAbop;

    /**
    * 产业链租赁_发生额(万元)
    */
    @ApiModelProperty(value = "产业链租赁_发生额(万元)")
    private Double tyagIndtLeasAmtAotc;

    /**
    * 产业链租赁_期末数(万元)
    */
    @ApiModelProperty(value = "产业链租赁_期末数(万元)")
    private Double tyagIndtLeasAmtAeop;

    /**
    * 民生消费租赁_期初数(万元)
    */
    @ApiModelProperty(value = "民生消费租赁_期初数(万元)")
    private Double tyagConsLeasAmtAbop;

    /**
    * 民生消费租赁_发生额(万元)
    */
    @ApiModelProperty(value = "民生消费租赁_发生额(万元)")
    private Double tyagConsLeasAmtAotc;

    /**
    * 民生消费租赁_期末数(万元)
    */
    @ApiModelProperty(value = "民生消费租赁_期末数(万元)")
    private Double tyagConsLeasAmtAeop;

    /**
    * 科技金融租赁_期初数(万元)
    */
    @ApiModelProperty(value = "科技金融租赁_期初数(万元)")
    private Double tyagSatyLeasAmtAbop;

    /**
    * 科技金融租赁_发生额(万元)
    */
    @ApiModelProperty(value = "科技金融租赁_发生额(万元)")
    private Double tyagSatyLeasAmtAotc;

    /**
    * 科技金融租赁_期末数(万元)
    */
    @ApiModelProperty(value = "科技金融租赁_期末数(万元)")
    private Double tyagSatyLeasAmtAeop;

    /**
    * 绿色金融租赁_期初数(万元)
    */
    @ApiModelProperty(value = "绿色金融租赁_期初数(万元)")
    private Double tyagGrenLeasAmtAbop;

    /**
    * 绿色金融租赁_发生额(万元)
    */
    @ApiModelProperty(value = "绿色金融租赁_发生额(万元)")
    private Double tyagGrenLeasAmtAotc;

    /**
    * 绿色金融租赁_期末数(万元)
    */
    @ApiModelProperty(value = "绿色金融租赁_期末数(万元)")
    private Double tyagGrenLeasAmtAeop;

    /**
    * 普惠金融租赁_期初数(万元)
    */
    @ApiModelProperty(value = "普惠金融租赁_期初数(万元)")
    private Double tyagIcveLeasAmtAbop;

    /**
    * 普惠金融租赁_发生额(万元)
    */
    @ApiModelProperty(value = "普惠金融租赁_发生额(万元)")
    private Double tyagIcveLeasAmtAotc;

    /**
    * 普惠金融租赁_期末数(万元)
    */
    @ApiModelProperty(value = "普惠金融租赁_期末数(万元)")
    private Double tyagIcveLeasAmtAeop;

    /**
    * 养老金融租赁_期初数(万元)
    */
    @ApiModelProperty(value = "养老金融租赁_期初数(万元)")
    private Double tyagPensLeasAmtAbop;

    /**
    * 养老金融租赁_发生额(万元)
    */
    @ApiModelProperty(value = "养老金融租赁_发生额(万元)")
    private Double tyagPensLeasAmtAotc;

    /**
    * 养老金融租赁_期末数(万元)
    */
    @ApiModelProperty(value = "养老金融租赁_期末数(万元)")
    private Double tyagPensLeasAmtAeop;

    /**
    * 海洋金融租赁_期初数(万元)
    */
    @ApiModelProperty(value = "海洋金融租赁_期初数(万元)")
    private Double tyagOceaLeasAmtAbop;

    /**
    * 海洋金融租赁_发生额(万元)
    */
    @ApiModelProperty(value = "海洋金融租赁_发生额(万元)")
    private Double tyagOceaLeasAmtAotc;

    /**
    * 海洋金融租赁_期末数(万元)
    */
    @ApiModelProperty(value = "海洋金融租赁_期末数(万元)")
    private Double tyagOceaLeasAmtAeop;

    /**
    * 开放金融租赁_期初数(万元)
    */
    @ApiModelProperty(value = "开放金融租赁_期初数(万元)")
    private Double tyagOpenLeasAmtAbop;

    /**
    * 开放金融租赁_发生额(万元)
    */
    @ApiModelProperty(value = "开放金融租赁_发生额(万元)")
    private Double tyagOpenLeasAmtAotc;

    /**
    * 开放金融租赁_期末数(万元)
    */
    @ApiModelProperty(value = "开放金融租赁_期末数(万元)")
    private Double tyagOpenLeasAmtAeop;

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
    private Double oyagLeasBusiAmtAbop;

    /**
    * 历年租赁金额_发生(万元)
    */
    @ApiModelProperty(value = "历年租赁金额_发生(万元)")
    private Double oyagLeasBusiAmtAotc;

    /**
    * 历年租赁金额_期末(万元)
    */
    @ApiModelProperty(value = "历年租赁金额_期末(万元)")
    private Double oyagLeasBusiAmtAeop;

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
    private Double thsyTaxpAmtAbop;

    /**
    * 实缴税金_发生(万元)
    */
    @ApiModelProperty(value = "实缴税金_发生(万元)")
    private Double thsyTaxpAmtAotc;

    /**
    * 实缴税金_期末(万元)
    */
    @ApiModelProperty(value = "实缴税金_期末(万元)")
    private Double thsyTaxpAmtAeop;

    /**
    * 增值税_期初(万元)
    */
    @ApiModelProperty(value = "增值税_期初(万元)")
    private Double incrTaxAbop;

    /**
    * 增值税_发生(万元)
    */
    @ApiModelProperty(value = "增值税_发生(万元)")
    private Double incrTaxAotc;

    /**
    * 增值税_期末(万元)
    */
    @ApiModelProperty(value = "增值税_期末(万元)")
    private Double incrTaxAeop;

    /**
    * 企业所得税_期初(万元)
    */
    @ApiModelProperty(value = "企业所得税_期初(万元)")
    private Double corpInctAbop;

    /**
    * 企业所得税_发生(万元)
    */
    @ApiModelProperty(value = "企业所得税_发生(万元)")
    private Double corpInctAotc;

    /**
    * 企业所得税_期末(万元)
    */
    @ApiModelProperty(value = "企业所得税_期末(万元)")
    private Double corpInctAeop;

    /**
    * 其他税金_期初(万元)
    */
    @ApiModelProperty(value = "其他税金_期初(万元)")
    private Double othTaxAbop;

    /**
    * 其他税金_发生(万元)
    */
    @ApiModelProperty(value = "其他税金_发生(万元)")
    private Double othTaxAotc;

    /**
    * 其他税金_期末(万元)
    */
    @ApiModelProperty(value = "其他税金_期末(万元)")
    private Double othTaxAeop;

    /**
    * 历年税金_期初(万元)
    */
    @ApiModelProperty(value = "历年税金_期初(万元)")
    private Double otyTaxpAmtAbop;

    /**
    * 历年税金_发生(万元)
    */
    @ApiModelProperty(value = "历年税金_发生(万元)")
    private Double otyTaxpAmtAotc;

    /**
    * 历年税金_期末(万元)
    */
    @ApiModelProperty(value = "历年税金_期末(万元)")
    private Double otyTaxpAmtAeop;

    /**
    * 表外业务_期初(万元)
    */
    @ApiModelProperty(value = "表外业务_期初(万元)")
    private Double ofblAmtAbop;

    /**
    * 表外业务_发生(万元)
    */
    @ApiModelProperty(value = "表外业务_发生(万元)")
    private Double ofblAmtAotc;

    /**
    * 表外业务_期末(万元)
    */
    @ApiModelProperty(value = "表外业务_期末(万元)")
    private Double ofblAmtAeop;

    /**
    * 报表实例id | uuid
    */
    @ApiModelProperty(value = "报表实例id | uuid")
    private String reportInstanceId;

    /**
    * 报表周期 | yyyyqq格式
    */
    @ApiModelProperty(value = "报表周期 | yyyyqq格式")
    private String reportInstancePeriod;

    /**
    * 批次号 | 0000-9999
    */
    @ApiModelProperty(value = "批次号 | 0000-9999")
    private String batchNo;

    /**
    * 版本号 | 周期版本
    */
    @ApiModelProperty(value = "版本号 | 周期版本")
    private String version;

    /**
    * 操作标识 | insert/update
    */
    @ApiModelProperty(value = "操作标识 | insert/update")
    private String op;

    /**
    * 上报时间
    */
    @ApiModelProperty(value = "上报时间")
    private LocalDateTime reportTime;

    /**
    * 入库时间
    */
    @ApiModelProperty(value = "入库时间")
    private LocalDateTime writeTime;

}
