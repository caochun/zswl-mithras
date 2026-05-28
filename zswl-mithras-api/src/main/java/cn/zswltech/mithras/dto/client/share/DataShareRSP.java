package cn.zswltech.mithras.dto.client.share;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

/**
 * @ClassName DataShareRSP
 * @Description
 * @Author jackerhe
 * @Date 2022/8/1 11:05 上午
 * @Version 1.0
 **/
@Data
public class DataShareRSP {

    //客商编码
    private String clientId;

    //客商名称
    private String name;

    //客商简称
    private String shortName;

    //英文名称
    private String englishName;

    //英文简称
    private String englishShortName;

    //是否为内部单位
    private String isInternalUnit;

    //客户类型
    private String customerType;

    //统一社会信用代码
    private String creditCode;

    //全国组织机构代码
    private String nationalOrgCode;

    //税务登记证号
    private String taxNo;

    //工商登记号
    private String businessRegistration;

    //邓白氏编码
    private String dunbarCode;

    //身份证号
    private String identificationNumber;

    //国家
    private String region;

    //省份
    private String province;

    //城市
    private String city;

    //法人姓名
    private String legalName;

    //注册地址
    private String registeredAddress;

    //注册资本
    private String registeredCapital;

    //联系人姓名
    private String contactName;

    //联系人电话
    private String contactTel;

    //附件
    private String enclosure;

    //是否客户
    private String isCustomer;

    //是否供应商
    private String isSupplier;

    //注册资本币种
    private String capitalCurrency;

    //业务主键
    private String naturalKey;

    //来源系统
    private String source;

    //内部单位
    private String internalUnit;

    //状态
    private String status;

    //主数据创建人名称
    private String mdmCreateNumber;

    //主数据创建人账号
    private String mdmCreateName;

    //主数据创建人组织机构
    private String mdmCreateOrg;

    //主数据创建人组织机构名称
    private String mdmCreateOrgName;

    //主数据创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date mdmCreateTime;

    //操作时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operationTime;

    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    //修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
