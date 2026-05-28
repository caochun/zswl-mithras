package cn.zswltech.mithras.dto.client.share;


import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * https://www.yuque.com/panjianliang1992/sptauw/bsw57103b3eef5dz?singleDoc#mnSg7
 * 主数据中心注册客户
 **/
@Data
public class DataShareRegisterCustomREQ {

    private BaseInfo baseInfo; //客商基础信息
    private BusinessInfo businessInfo; //客商工商信息
    private List<LinkmanInfo> linkmanInfo; //客商联系人信息


    @Data
    public class BaseInfo {
        private String source = "OUTER";//系统编码，固定填OUTER
        /*private String status; //
        private String isInnerUnit;
        private String innerUnitName;*/
        private String merchantType; //客商类型 企业QIYE, 个体工商户GETI_GONGSHANGHU, 个人GEREN
        private String customerType = "CUSTOMER"; //客商身份，CUSTOMER：客户，SUPPLIER：供应商，CUSTOMER_AND_SUPPLIER：客户和供应商
        private String merchantName; //客商名称
        private String englishName; //英语名称
        private String shortName; //客商简称
        private String englishShortName; //英语简称
        private String creditCode; //统一社会信用代码，企业类型为企业、个体工商户、农民专业合作社、政府机关、事业单位、社会团体、民办非企业单位、司法行政、军队时必填
        private String identificationNumber; //身份证，企业类型为个人时必填
        private String dunsCode; //邓白氏编码
        private String contactTel; //联系电话
        private List<String> areaCodeList;// 国家、身份、市编码组成的数组，如：[CN, 310000, 311200]。编码格式与主数据相同，详情见地区编码表
        private String address; //详细地址
        private String businessScope; //经营范围
        private String parentCompanyName; //母公司
        private String parentCompanyCreditCode; //母公司社会信用代码
    }

    @Data
    public class BusinessInfo {
        private String regCapital; //注册资本
        private String regCapitalCurrencyType; //注册资本币种，详见币种编码表 人民币CNY，美元USD， EUR欧元，BOB玻利维亚币
        private String actualCapital; //实缴资本
        private String actualCapitalCurrencyType;//实缴资本币种
        private String regNumber;//工商注册号
        private String legalPersonName;//法人
        private LocalDate establishTime;//成立日期,时间戳，格式为：yyyy-MM-dd
        private String taxpayerQualificationType;//纳税人资质
        private LocalDate approvalTime;//核准日期,时间戳，格式为：yyyy-MM-dd
        private String businessTime;//营业期限
        private String orgNumber;//组织机构代码
        private String companyOrgType;//企业类型
        private String industry;//行业类型
        private String regInstitute;//登记机关
        private String regLocation;//注册地址
        private String regProvince;//注册所在省
        private String regCity;//注册所在市
        private String regCountry;//注册所在国家
        private String socialStaffNum;//参保人数
        private String regStatus;//经营状态
        private String taxNumber;//纳税人识别号
        private String historyNames;//曾用名
        private String staffNumRange;//人员规模
    }

    @Data
    public class LinkmanInfo {
        private String name;//联系人姓名
        private String phoneNo;//手机号
        private String department;//部门
        private String duty;//职务
        private String email;//邮箱
        private String fax;//传真
    }


}