package cn.zswltech.mithras.service.constant;

/**
 * 数据缺失提示语
 * 用于数据生效时 校验数据库数据
 *
 * @author wangchuanhao
 * @date 2022/7/1 10:03 AM
 */
public interface LackDataMsg {

    String ADDRESS_REGISTRY = "地址信息——注册地址";
    String ADDRESS_WORK_ADDRESS = "地址信息——办公地址";
    String ADDRESS_COUNTRY = "地址信息——国家";
    String ADDRESS_PROVINCE = "地址信息——省";
    String ADDRESS_CITY = "地址信息——市";
    String ADDRESS_DISTRICT = "地址信息——区";
    String ADDRESS_DETAIL = "地址信息——详细地址";
    String ADDRESS_REGION_CODE = "地址信息——行政区划代码";

    String BANK_ACCOUNT_BANK = "银行账户——开户行";
    String BANK_ACCOUNT_NAME = "银行账户——账户名称";
    String BANK_ACCOUNT_NUMBER = "银行账户——银行账户";

    String RELATED_ENTERPRISE_RELATIONSHIP = "关联企业——关联关系";
    String RELATED_ENTERPRISE_ENTERPRISE_NAME = "关联企业——关联企业名称";

    String SHAREHOLDER_TYPE = "股东信息——股东类型";
    String SHAREHOLDER_NAME = "股东信息——股东名称";

    String SPOUSE_NAME = "配偶信息——配偶姓名";
    String SPOUSE_CERT_NUMBER = "配偶信息——证件号码";
    String SPOUSE_CERT_TYPE = "配偶信息——证件类型";

    String NORMAL_BASE = "基本信息";
    String NORMAL_BASE_CERT_TYPE = "基本信息——证件类型";
    String NORMAL_BASE_CERT_NUMBER = "基本信息——证件号码";
    String NORMAL_BASE_GENDER = "基本信息——性别";
    String NORMAL_BASE_MARRIAGE_TYPE = "基本信息——婚姻情况";
    String NORMAL_BASE_COUNTRY = "基本信息——国家";
    String NORMAL_BASE_MOBILE_NUMBER = "基本信息——手机号";

    String COMMERCE = "工商信息";
    String COMMERCE_TRIPLE_CERT_IN_ONE = "工商信息-三证合一";
    String COMMERCE_ORG_CODE = "工商信息-组织机构代码";
    String COMMERCE_CONTINUOUS_STATUS = "工商信息-存续状态";
    String COMMERCE_ESTABLISH_DATE = "工商信息-成立日期";
    String COMMERCE_APPROVAL_DATE = "工商信息-核准日期";
    String COMMERCE_BIZ_LICENCE_LONG_TERM = "工商信息-营业许可证是否为长期";
    String COMMERCE_BIZ_LICENSE_END_DATE = "工商信息-营业许可证到期日";
    String COMMERCE_INDUSTRY_TYPE = "工商信息-行业分类";
    String COMMERCE_ECONOMY_TYPE = "工商信息-经济类型";
    String COMMERCE_ORG_TYPE = "工商信息-组织机构类型";
    String COMMERCE_ORG_SCALE = "工商信息-企业规模";
    String COMMERCE_REGISTER_CURRENCY_TYPE = "工商信息-注册币种";
    String COMMERCE_REGISTER_CAPITAL = "工商信息-注册资本";
    String COMMERCE_REAL_CAPITAL = "工商信息-实收资本";
    String COMMERCE_REGISTER_CAPITAL_RATE = "工商信息-注册资本到位率";
    String COMMERCE_BIZ_SCOPE = "工商信息-业务范围";
    String COMMERCE_CORP_REPRESENT = "工商信息-法人代表";
    String COMMERCE_CORP_GENDER = "工商信息-法人性别";
    String COMMERCE_CORP_CERT_TYPE = "工商信息-法人证件类型";
    String COMMERCE_CORP_CERT_CODE = "工商信息-法人证件号码";
    String COMMERCE_LISTED_COMPANY = "工商信息-是否上市公司";

}
