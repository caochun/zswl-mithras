package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @description 实体经济服务数据(流程节点记录版本表)
 * @author hspcadmin
 * @date 2025-09-14
 */
@Data
@ApiModel("实体经济服务数据(流程节点记录版本表)列表-返回体")
public class AssociationEntityEconomyServiceLibListRSP {

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

    /**
    * 逻辑删除标识
    */
    @ApiModelProperty(value = "逻辑删除标识")
    private Integer deleted;

    /**
    * 版本号
    */
    @ApiModelProperty(value = "版本号")
    private String version;

    /**
    * 临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写
    */
    @ApiModelProperty(value = "临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写")
    private Long originId;

    /**
    * data_create_time
    */
    @ApiModelProperty(value = "data_create_time")
    private LocalDateTime dataCreateTime;

    /**
    * data_create_by
    */
    @ApiModelProperty(value = "data_create_by")
    private Long dataCreateBy;

    /**
    * data_update_time
    */
    @ApiModelProperty(value = "data_update_time")
    private LocalDateTime dataUpdateTime;

    /**
    * data_update_by
    */
    @ApiModelProperty(value = "data_update_by")
    private Long dataUpdateBy;

    /**
    * 版本标志，0无效，1有效...业务自扩展
    */
    @ApiModelProperty(value = "版本标志，0无效，1有效...业务自扩展")
    private Integer versionType;

}
