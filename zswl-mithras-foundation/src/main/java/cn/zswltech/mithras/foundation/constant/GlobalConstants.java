package cn.zswltech.mithras.foundation.constant;

import java.math.BigDecimal;

/**
 * @author dingqi
 * @date 2022/8/2
 * @description 全局常量
 */
public class GlobalConstants {
    // 浙商租赁本级组织编码
    public static final String ZSZL_LOCAL_ORG_CODE = "10000396";
    // 浙商租赁合并组织编码
    public static final String ZSZL_MERGE_ORG_CODE = "13002609";
    // 系统生成抵扣核销记录
    public static final String AUTO_DEDUCTION_PAY_METHOD = "抵扣";
    // 日志链路参数
    public static final String LOG_TRACE_ID = "traceId";
    // 金钱相关字段，统一按照元扩大10000倍（精确到毫厘）进行存储
    public static final String MONEY_MULTIPLE = "10000";
    // 压缩包后缀
    public static final String COMPRESSED_FILE_SUFFIX = ".zip";
    // Word后缀
    public static final String OFFICE_WORD_SUFFIX = ".docx";
    // Pdf后缀
    public static final String OFFICE_PDF_SUFFIX = ".pdf";
    // HTML后缀
    public static final String OFFICE_HTML_SUFFIX = ".html";
    // Excel后缀
    public static final String OFFICE_EXCEL_SUFFIX = ".xlsx";
    // 项目评审-现金流量表（合同管理概算租金表、实际租金表复用该模板） 导入模板
    public static final String TEMPLATE_OSS_NAME_PROJ_REVIEW_CASH_FLOW = "现金流测算表模板.xlsx";
    // 合同管理-租赁物清单 导入模板
    public static final String TEMPLATE_OSS_NAME_CONTRACT_LEASE_ITEM = "租赁物清单模板.xlsx";
    // 合同管理-抵押清单 导入模板
    public static final String TEMPLATE_OSS_NAME_MORTGAGE_ITEM = "抵押清单模版.xlsx";
    // 合同管理-质押物清单 导入模板
    public static final String TEMPLATE_OSS_NAME_PLEDGE_ITEM = "质押物清单模版.xlsx";
    // 基础数据-LPR设置 导入模板
    public static final String TEMPLATE_OSS_BASE_DATA_LPR = "基础数据-LPR设置-模板.xlsx";
    // 合同管理-合同起租提醒文案 模板
    public static final String CONTRACT_TODO_TEMPLATE = "合同<%s>已放款(付款申请编号: <%s>), 请提交合同起租流程";
    // 保理-债务人-非法人类型常量
    public static final String DEBTOR_NO_CORPORATION = "NO-CORPORATION";
    // 付款申请-保单信息 导入模板
    public static final String TEMPLATE_OSS_NAME_PAYMENT_POLICY_ITEM = "保单信息模版.xlsx";

    public static final String FTP_QUARTERLY_GUIDANCE = "季度最低收益率指导模版.xlsx";
    public static final String FTP_MONTHLY_GUIDANCE = "月度FTP定价表单模版.xlsx";

    // 融资管理-还款表（概算、实际）导入模板
    public static final String TEMPLATE_OSS_NAME_FINANCING_REPAY = "融资管理_还款表模板.xlsx";

    public static final String RELATED_TRANSACTION_SUBMISSION = "关联交易报送导入模版.xlsx";

    public static final int FILE_TEMPLATE_EXPIRY = 24 * 60 * 60;

    public static final String CQ_PAYMENT_METHOD_PJ = "票据";

    // 直租税率
    public static final BigDecimal TAX_RATE_ZHI_ZU = new BigDecimal("0.13");

    // 非直租税率
    public static final BigDecimal TAX_RATE_FEI_ZHI_ZU = new BigDecimal("0.06");

    //流水核销的分布式锁，防止流水资源争夺问题
    public static final String FLOW_WRITE_OFF_LOCK = "flow_write_off_lock";

    // 匹配字符串时可忽略的字符
    public static final char[] MATCH_IGNORE_STR = {'(', ')', '（', '）'};

    //拷贝数据忽略通用字段
    public static final String[] COPY_IGNORE_COMMON_FIELD = {"id", "createBy", "createTime", "updateBy", "updateTime", "deleted"};

    //超级管理员ID
    public static final Long READONLY_ID = 3L;

    public static final String ASSOCIATION_FLC = "FLCZJ";
    // csv后缀
    public static final String OFFICE_CSV_SUFFIX = ".csv";
}
