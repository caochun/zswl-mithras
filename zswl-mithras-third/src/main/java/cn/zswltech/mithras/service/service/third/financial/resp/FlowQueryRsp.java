package cn.zswltech.mithras.service.service.third.financial.resp;

import cn.hutool.core.date.DateTime;
import lombok.Data;

import java.util.Date;
import java.math.BigDecimal;

@Data
public class FlowQueryRsp {
    private Long id; // id
    private String billno; // 交易明细编号
    private String billstatus; // 单据状态
    private String auditdate; // 审核日期
    private String modifytime; // 最后更新时间
    private String createtime; // 创建时间
    private String bizdate; // 交易日期
    private Long sourcebillid; // 源单id
    private BigDecimal amount; // 金额
    private BigDecimal locamt; // 金额折本位币
    private BigDecimal exchangerate; // 汇率
    private String description; // 描述
    private String bizrefno; // 业务参考号
    private BigDecimal debitamount; // 付款金额
    private BigDecimal creditamount; // 收款金额
    private BigDecimal transbalance; // 余额
    private String oppunit; // 对方户名
    private String oppbank; // 对方开户行
    private Boolean ismatchereceipt; // 跟电子回单匹配
    private Boolean isdataimport; // 是否导入
    private String detailid; // 明细流水号
    private Boolean isdowntobankstate; // 已经下载到银行对账单
    private Boolean isnoreceipt; // 确认无回单
    private String receiptno; // 电子回单关联标记
    private String originalbankcheckflag; // 对账标识码(银行返回)
    private String datasource; // 数据来源
    private String biztype; // 业务类型
    private Boolean isrefund; // 是否退票
    private String bankinterface; // 银行接口
    private Boolean isreced; // 是否接收
    private DateTime biztime; // 交易时间
    private Boolean iskdretflag; // 是否银企付标
    private Boolean istransup; // 银行上传
    private Boolean istransdown; // 银行下载
    private Boolean isbankwithholding; // 银行代扣
    private String recedbilltype; // 接收单据类型
    private String receredtype; // 入账状态
    private Long sortno; // 排序号
    private String rulename; // 适配规则
    private String businessbillnum; // 票据号
    private Boolean autorecorpay; // 自动收付款
    private String smartmatch; // 智能匹配
    private String oppbanknumber; // 对方账号
    private String bankcheckflag; // 对账标识码
    private String recedbillnumber; // 接收单据编号
    private String claimnoticebillno; // 收款认领通知
    private String sourcebilltype; // 源单类型
    private BigDecimal transfercharge; // 手续费
    private String flowserialno; // 流程序列号
    private String sortid; // 排序ID
    private String requestserialno; // 银企请求流水号
    private String responseserailno; // 银行响应流水号
    private String kdretflag; // KD标识
    private String agentaccno; // 被代理账号
    private String agentaccname; // 被代理户名
    private String agentaccbankname; // 被代理户开户行
    private String bustype; // busType
    private String batchno; // 银企付款提交的批次号
    private String billnobillno;//票号
    private Date transdate; // 记账日期
    private String extdata; // extData
    private DateTime lastmodifytime; // 最后修改时间
    private String bankdetailno; // 银行流水号
    private String uniqueseq; // 银行主键
    private String cico_reconciliationcode; // 保融对账码
    private String cico_activepayment; // 付款状态
    private String cico_bruid; // 保融uid
    private String cico_billno; // 付款单号
    private String receredway; // 入账方式
    private Boolean ishandlink; // 是否手工关联
    private String company_number; // 资金组织.编码
    private String company_name; // 资金组织.名称
    private String accountbank_bankaccountnumber; // 银行账号.银行账号
    private String accountbank_acctname; // 银行账号.银企账户名称
    private String accountbank_name; // 银行账号.银行账户名称
    private String bank_number; // 开户银行.编码
    private String bank_name;//开户银行.名称
    private String currency_name; // 币别.名称
}
