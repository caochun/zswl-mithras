package cn.zswltech.mithras.third.datashare.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName ProjEstablishDataShare
 * @Description
 * @Author jackerhe
 * @Date 2022/8/1 2:28 下午
 * @Version 1.0
 **/
@Data
@TableName("data_share_merchants")
public class DataShareMerchants {
    //客商编码
    @TableId(value = "client_id")
    private Long clientId;

    //客商名称
    @TableField("name")
    private String name;

    //客商简称
    @TableField("short_name")
    private String shortName;

    //英文名称
    @TableField("english_name")
    private String englishName;

    //英文简称
    @TableField("english_short_name")
    private String englishShortName;

    //是否为内部单位 0否，1是
    @TableField("is_internal_unit")
    private Integer isInternalUnit;

    //客户类型 01 企业 02 个体工商户 03 农民专业合作社 04 政府机关 05 事业单位 06 社会团体 07 民办非企业单位 08 司法行政 09 外国企业 10 军队 11 个人 12 临时客商
    @TableField("customer_type")
    private Integer customerType;

    //统一社会信用代码
    @TableField("credit_code")
    private String creditCode;

    //全国组织机构代码
    @TableField("national_org_code")
    private String nationalOrgCode;

    //税务登记证号
    @TableField("tax_no")
    private String taxNo;

    //工商登记号
    @TableField("business_registration")
    private String businessRegistration;

    //邓白氏编码
    @TableField("dunbar_code")
    private String dunbarCode;

    //身份证号
    @TableField("identification_number")
    private String identificationNumber;

    //国家
    @TableField("region")
    private String region;

    //省份
    @TableField("province")
    private String province;

    //城市
    @TableField("city")
    private String city;

    //法人姓名
    @TableField("legal_name")
    private String legalName;

    //注册地址
    @TableField("registered_address")
    private String registeredAddress;

    //注册资本
    @TableField("registered_capital")
    private String registeredCapital;

    //联系人姓名
    @TableField("contact_name")
    private String contactName;

    //联系人电话
    @TableField("contact_tel")
    private String contactTel;

    //附件
    @TableField("enclosure")
    private String enclosure;

    //是否客户 0否，1是
    @TableField("is_customer")
    private Integer isCustomer;

    //是否供应商 0否，1是
    @TableField("is_supplier")
    private Integer isSupplier;

    //注册资本币种
    @TableField("capital_currency")
    private String capitalCurrency;

    //业务主键
    @TableField("natural_key")
    private String naturalKey;

    //来源系统
    @TableField("source")
    private String source;

    //内部单位
    @TableField("internal_unit")
    private String internalUnit;

    //状态 0 失效 1 有效
    @TableField("status")
    private String status;

    //主数据创建人名称
    @TableField("mdm_create_number")
    private String mdmCreateNumber;

    //主数据创建人账号
    @TableField("mdm_create_name")
    private String mdmCreateName;

    //主数据创建人组织机构
    @TableField("mdm_create_org")
    private String mdmCreateOrg;

    //主数据创建人组织机构名称
    @TableField("mdm_create_org_name")
    private String mdmCreateOrgName;

    //主数据创建时间
    @TableField("mdm_create_time")
    private Date mdmCreateTime;

    //操作时间
    @TableField("operation_time")
    private Date operationTime;

    //创建时间
    @TableField("create_time")
    private Date createTime;

    //修改时间
    @TableField("update_time")
    private Date updateTime;
}
