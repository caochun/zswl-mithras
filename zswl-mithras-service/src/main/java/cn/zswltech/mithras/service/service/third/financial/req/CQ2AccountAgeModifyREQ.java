package cn.zswltech.mithras.service.service.third.financial.req;

import lombok.Data;

import java.util.Date;

@Data
public class CQ2AccountAgeModifyREQ extends CQ2CommonReq {

    /**
     * 单据编号 YWZQ-10000396-202407-00000001
     **/
    private String billno;
  
    /**  
     * 业务唯一id 由业务系统自己生成做为唯一识别码  
     */  
    private Long businessId;  
  
    /**  
     * 核算组织编码  “10000396”
     */  
    private String org;  
  
    /**  
     * 期末款项原值（余额）两位小数  
     */  
    private Double originalValue;  
  
    /**  
     * 币别  固定值，填写“CNY”
     */  
    private String currency;  
  
    /**  
     * 科目编码 应收账款1122、应收票据1121、长期应收款1531、合同资产1462、其他应收款1221、预付账款1123
     * 固定值，填写“1531”
     */  
    private String subject;  
  
    /**  
     * 款项内容编码  固定值，填写“KX08”
     * 费用项描述，因为此报表仅涉及租金信息，所以款项内容“租赁款”款项内容编码为“KX08”
     */  
    private String payContent;  
  
    /**  
     * 客户编码  
     */  
    private String assact;  
  
    /**  
     * 客户类型  
     * bd_customer	客户  
     * bd_supplier	供应商  
     * bos_user	    人员  
     * other	      其他
     * 租赁客户均为 bd_customer
     */  
    private String assactType;  
  
    /**  
     * 客户属性-省交通集团内外  
     * A省交通集团内  
     * B省交通集团外
     * 非必填字段，不进行传输
     */  
    private String inOrOutGroup;  
  
    /**  
     * 客户属性-二级公司内外  
     * A二级公司内  
     * B二级公司外  
     * C 空（即省交通集团外的都为空）
     * 非必填字段，不进行传输
     */  
    private String ioSecondCompany;  
  
    /**  
     * 业务日期  取借据维度的起租日期
     */  
    private Date acctAgeBegining;  
  
    /**  
     * 业务账龄（月）
     * 账龄截止日”-“业务日期”，使用每月30天转换为月，整数位最多19位数，向下取整
     */  
    private Integer businessAging;  
  
    /**  
     * 账龄截止日
     * 该笔账龄截止的日期，取报送时间，该字段在此报表生成时由财务人员选择日期；
     */  
    private Date agingDeadline;  
  
    /**  
     * 期初款项原值  
     */  
    private Double cico_original_qc;  
  
    /**  
     * 本期增加额  
     */  
    private Double cico_add;  
  
    /**  
     * 本期减少额  
     */  
    private Double cico_des;  
  
    /**  
     * 合同编号  
     */  
    private String cico_contractno;  
  
    /**  
     * 合同名称  项目名称
     */  
    private String cico_contractname;  
  
    /**  
     * 合同逾期日
     * 该行租金的应收日期
     */  
    private Date cico_overduedate;  
  
    /**  
     * 业务编号  
     */  
    private String cico_bussinessno;  
  
    /**  
     * 来源系统  
     * [GX:高信公司, SZ:数智交院, ZT:浙商中拓, ZL:浙商租赁, ZQ:浙商证券, QH:浙商期货, BX:浙商保险, GD:浙江交控-国大商管公司, JD:浙江交控-武林酒店、维嘉酒店]  
     */  
    private String cico_source;  
  
    /**  
     * 数据接入方式  
     * [A:接口, B:引入, C:其他]  A
     */  
    private String cico_access_method;  
  
}