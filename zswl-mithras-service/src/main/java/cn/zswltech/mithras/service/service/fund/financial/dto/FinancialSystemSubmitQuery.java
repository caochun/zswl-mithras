package cn.zswltech.mithras.service.service.fund.financial.dto;

import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.service.enums.fund.financing.LprArrangeModeEnum;
import cn.zswltech.mithras.service.enums.projestablish.RateType;
import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * @author zswl
 */
@Data
@ApiModel("贷款合同推送-请求体")
public class FinancialSystemSubmitQuery {

    /**
     * 系统报文头
     */
    private MessageHead cwgsHead;

    /**
     * 报文体
     */
    private Body body;

    /**
     * 应用报文头
     */
    private AppHead cwgsApiAppUser;


    @Data
    public static class Body{
        private Object list;
    }


    /**
     * 系统报文头
     */
    @Data
    public static class MessageHead {
        /**
         * 服务代码
         */
        private String serviceCode;

        /**
         * 服务编号
         */
        private String serviceNo;

        /**
         * 消费者编码
         */
        private String consumerCode;

        /**
         * 发起方系统编号
         */
        private String consumerId;

        /**
         * 渠道流水号 服务编号+消费端编码+时间戳
         */
        private String reqSequence;

        /**
         * 服务请求系统的日期，格式为YYYYMMDD
         */
        private Long trandate;

        /**
         * 服务请求系统的时间，格式为HHMMSS
         */
        private Long trantime;

        /**
         * 渠道标志
         */
        private String channelType;
    }

    /**
     * 合同口径报文体
     */
    @Data
    @Accessors(chain = true)
    public static class ContractBody {
        /**
         * 机构id
         */
        @JSONField(name = "ORG_ID")
        private String ORG_ID;
        /**
         * 客户号
         */
        @JSONField(name = "CUST_NO")
        private String CUST_NO;
        /**
         * 客户名称
         */
        @JSONField(name = "CUST_NAME")
        private String CUST_NAME;
        /**
         * 业务类型代码
         * {@link FundFinancingBizTypeEnum#financialSystemCode()}
         */
        @JSONField(name = "BUSI_TYP_CD")
        private String BUSI_TYP_CD;
        /**
         * 业务合同号
         */
        @JSONField(name = "BUSI_CONTR_NO")
        private String BUSI_CONTR_NO;
        /**
         * 主合同号
         */
        @JSONField(name = "MAIN_CONTR_NO")
        private String MAIN_CONTR_NO;
        /**
         * 合同初始日期
         */
        @JSONField(name = "CONTR_START_DATE")
        private String CONTR_START_DATE;
        /**
         * 合同到期日期
         */
        @JSONField(name = "CONTR_END_DATE")
        private String CONTR_END_DATE;
        /**
         * 币种代码
         */
        @JSONField(name = "CCY_CD")
        private String CCY_CD;
        /**
         * 合同金额
         */
        @JSONField(name = "CONTR_AMT")
        private String CONTR_AMT;
        /**
         * 利率类型代码
         */
        @JSONField(name = "INTRT_ADJ_WAY_CD")
        private String INTRT_ADJ_WAY_CD;
        /**
         * 执行利率
         */
        @JSONField(name = "EXEC_RATE")
        private String EXEC_RATE;
        /**
         * 贷款用途
         */
        @JSONField(name = "LN_USE")
        private String LN_USE;
        /**
         * 签约日期
         */
        @JSONField(name = "SIGNING_DATE")
        private String SIGNING_DATE;
        /**
         * 对公对私标志
         */
        @JSONField(name = "CORP_PRIV_FLG")
        private String CORP_PRIV_FLG;
        /**
         * 利率浮动方式
         * {@link RateType#name()}
         */
        @JSONField(name = "LILVFDFS")
        private String LILVFDFS;
        /**
         * 利率调整方式
         * {@link LprArrangeModeEnum#name()}
         */
        @JSONField(name = "LILVTZGZ")
        private String LILVTZGZ;
        /**
         * 利率调整生效月
         */
        @JSONField(name = "LILVTZSXY")
        private String LILVTZSXY;
        /**
         * 利率调整生效日
         */
        @JSONField(name = "LILVTZSXR")
        private String LILVTZSXR;
        /**
         * 贷款债券融资机构
         */
        @JSONField(name = "DKZQRZJG")
        private String DKZQRZJG;
        /**
         * 贷款债券融资机构网点
         */
        @JSONField(name = "DKZQRZJGW")
        private String DKZQRZJGW;
        /**
         * 提款账号
         */
        @JSONField(name = "TKZHANGHAO")
        private String TKZHANGHAO;
        /**
         * 利率类型
         */
        @JSONField(name = "LILVLEIX")
        private String LILVLEIX;
        /**
         * 贷款单位类别
         */
        @JSONField(name = "DKDWLEIB")
        private String DKDWLEIB;
        /**
         * 提款方式
         */
        @JSONField(name = "TKFANGS")
        private String TKFANGS;
        /**
         * 还款日
         */
        @JSONField(name = "HUANKRI")
        private String HUANKRI;
        /**
         * 外部系统
         */
        @JSONField(name = "LAIYUAXT")
        private String LAIYUAXT;

    }

    /**
     * 借据口径报文体
     */
    @Data
    @Accessors(chain = true)
    public static class ReceiptBody {
        /**
         * 主合同号
         */
        @JSONField(name = "MAIN_CONTR_NO")
        private String MAIN_CONTR_NO;
        /**
         * 合同号
         */
        @JSONField(name = "CONTR_NO")
        private String CONTR_NO;
        /**
         * 借据号
         */
        @JSONField(name = "RCPLN_NO")
        private String RCPLN_NO;
        /**
         * 贷款账号
         */
        @JSONField(name = "LN_ACCT_NO")
        private String LN_ACCT_NO;
        /**
         * 贷款用途
         */
        @JSONField(name = "LN_USE")
        private String LN_USE;
        /**
         * 正常本金余额
         */
        @JSONField(name = "PRINC_NORMAL_BAL")
        private String PRINC_NORMAL_BAL;
        /**
         * 放款日期
         */
        @JSONField(name = "MAKE_LN_DATE")
        private String MAKE_LN_DATE;
        /**
         * 原始到期日期
         */
        @JSONField(name = "ORGIN_DUE_DATE")
        private String ORGIN_DUE_DATE;
        /**
         * 开户日期
         */
        @JSONField(name = "OPEN_DATE")
        private String OPEN_DATE;
        /**
         * 币种代码
         */
        @JSONField(name = "CCY_CD")
        private String CCY_CD;
        /**
         * 借据金额
         */
        @JSONField(name = "RCPLN_AMT")
        private String RCPLN_AMT;
        /**
         * 当前余额
         */
        @JSONField(name = "CURR_BAL")
        private String CURR_BAL;
        /**
         * 执行利率
         */
        @JSONField(name = "EXEC_RATE")
        private String EXEC_RATE;
        /**
         * 利率浮动值
         */
        @JSONField(name = "INTR_FLOAT_VALUE")
        private String INTR_FLOAT_VALUE;
        /**
         * 贷款入账账号
         */
        @JSONField(name = "LN_IN_ACCT_NO")
        private String LN_IN_ACCT_NO;
        /**
         * 还款账号
         */
        @JSONField(name = "REPAY_ACCT_NO")
        private String REPAY_ACCT_NO;
        /**
         * 利率浮动方式
         */
        @JSONField(name = "LILVFDFS")
        private String LILVFDFS;
        /**
         * 利率调整规则
         */
        @JSONField(name = "LILVTZGZ")
        private String LILVTZGZ;
        /**
         * 利率结息方式
         */
        @JSONField(name = "LILVJXFS")
        private String LILVJXFS;
        /**
         * 利息处理方式
         */
        @JSONField(name = "lixiclfs")
        private String lixiclfs;
        /**
         * 提款债权融资机构
         */
        @JSONField(name = "TKZQRZJG")
        private String TKZQRZJG;
        /**
         * 提款债权融资机构网点
         */
        @JSONField(name = "TKZQRZJGWD")
        private String TKZQRZJGWD;
        /**
         * 利率类型
         */
        @JSONField(name = "LILVLEIX")
        private String LILVLEIX;
        /**
         * 外部系统
         */
        @JSONField(name = "LAIYUAXT")
        private String LAIYUAXT;

    }

    /**
     * 贷款还款计划报文体
     */
    @Data
    @Accessors(chain = true)
    public static class RepayBody {
        /**
         * 来源系统
         */
        @JSONField(name = "LAIYUAXT")
        private String LAIYUAXT;
        /**
         * 交易日期
         */
        @JSONField(name = "JIAOYIRQ")
        private String JIAOYIRQ;
        /**
         * 合同编号
         */
        @JSONField(name = "HETONGBH")
        private String HETONGBH;
        /**
         * 贷款借据号
         */
        @JSONField(name = "DKJIEJUH")
        private String DKJIEJUH;
        /**
         * 还款账号
         */
        @JSONField(name = "HUANKZHH")
        private String HUANKZHH;
        /**
         * 放款/提款金额
         */
        @JSONField(name = "FKJE")
        private String FKJE;
        /**
         * 计划还款金额
         */
        @JSONField(name = "JHHKJE")
        private String JHHKJE;
        /** 已还款金额
         *
         */
        @JSONField(name = "HKJE")
        private String HKJE;
        /**
         * 计划还款利息
         */
        @JSONField(name = "JHHKLX")
        private String JHHKLX;
        /**
         * 已还利息
         */
        @JSONField(name = "YHLX")
        private String YHLX;

    }


    /**
     * 应用报文头
     */
    @Data
    public static class AppHead {
        /**
         * 操作员编号
         */
        private String operator;

        /**
         * 机构
         */
        private String organ;
    }


}
