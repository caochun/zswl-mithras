package cn.zswltech.mithras.third.mapper.model;

import cn.zswltech.mithras.service.annotation.NotCompareColumn;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author vico
 * @description 财资平台流水记录-临时表
 * @date 2024-05-15
 */
@Data
public class FinanceFlowTempRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long businessId;

    @TableField("batch_id")
    private String batchId;

    /**
     * id
     */
    @TableField("id")
    private Long id;

    /**
     * 交易明细编号
     */
    @TableField("billno")
    private String billno;

    /**
     * 单据状态
     */
    @TableField("billstatus")
    private String billstatus;

    /**
     * 审核日期
     */
    @TableField("auditdate")
    private String auditdate;

    /**
     * 最后更新时间
     */
    @TableField("modifytime")
    private String modifytime;

    /**
     * 创建时间
     */
    @TableField("createtime")
    private String createtime;

    /**
     * 交易日期
     */
    @TableField("bizdate")
    private String bizdate;

    /**
     * 源单id
     */
    @TableField("sourcebillid")
    private Long sourcebillid;

    /**
     * 金额
     */
    @TableField("amount")
    private Double amount;

    /**
     * 金额折本位币
     */
    @TableField("locamt")
    private Double locamt;

    /**
     * 汇率
     */
    @TableField("exchangerate")
    private Double exchangerate;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 业务参考号
     */
    @TableField("bizrefno")
    private String bizrefno;

    /**
     * 付款金额
     */
    @TableField("debitamount")
    private Double debitamount;

    /**
     * 收款金额
     */
    @TableField("creditamount")
    private Double creditamount;

    /**
     * 余额
     */
    @TableField("transbalance")
    private Double transbalance;

    /**
     * 对方户名
     */
    @TableField("oppunit")
    private String oppunit;

    /**
     * 对方开户行
     */
    @TableField("oppbank")
    private String oppbank;

    /**
     * 跟电子回单匹配
     */
    @TableField("ismatchereceipt")
    private Integer ismatchereceipt;

    /**
     * 是否导入
     */
    @TableField("isdataimport")
    private Integer isdataimport;

    /**
     * 明细流水号
     */
    @TableField("detailid")
    private String detailid;

    /**
     * 已经下载到银行对账单
     */
    @TableField("isdowntobankstate")
    private Integer isdowntobankstate;

    /**
     * 确认无回单
     */
    @TableField("isnoreceipt")
    private Integer isnoreceipt;

    /**
     * 电子回单关联标记
     */
    @TableField("receiptno")
    private String receiptno;

    /**
     * 对账标识码(银行返回)
     */
    @TableField("originalbankcheckflag")
    private String originalbankcheckflag;

    /**
     * 数据来源
     */
    @TableField("datasource")
    private String datasource;

    /**
     * 业务类型
     */
    @TableField("biztype")
    private String biztype;

    /**
     * 是否退票
     */
    @TableField("isrefund")
    private Integer isrefund;

    /**
     * 银行接口
     */
    @TableField("bankinterface")
    private String bankinterface;

    /**
     * 是否接收
     */
    @TableField("isreced")
    private Integer isreced;

    /**
     * 交易时间
     */
    @TableField("biztime")
    private LocalDateTime biztime;

    /**
     * 是否银企付标
     */
    @TableField("iskdretflag")
    private Integer iskdretflag;

    /**
     * 银行上传
     */
    @TableField("istransup")
    private Boolean istransup;

    /**
     * 银行下载
     */
    @TableField("istransdown")
    private Boolean istransdown;

    /**
     * 银行代扣
     */
    @TableField("isbankwithholding")
    private Integer isbankwithholding;

    /**
     * 接收单据类型
     */
    @TableField("recedbilltype")
    private String recedbilltype;

    /**
     * 入账状态
     */
    @TableField("receredtype")
    private String receredtype;

    /**
     * 排序号
     */
    @TableField("sortno")
    private Long sortno;

    /**
     * 适配规则
     */
    @TableField("rulename")
    private String rulename;

    /**
     * 票据号
     */
    @TableField("businessbillnum")
    private String businessbillnum;

    /**
     * 自动收付款
     */
    @TableField("autorecorpay")
    private Integer autorecorpay;

    /**
     * 智能匹配
     */
    @TableField("smartmatch")
    private String smartmatch;

    /**
     * 对方账号
     */
    @TableField("oppbanknumber")
    private String oppbanknumber;

    /**
     * 对账标识码
     */
    @TableField("bankcheckflag")
    private String bankcheckflag;

    /**
     * 接收单据编号
     */
    @TableField("recedbillnumber")
    private String recedbillnumber;

    /**
     * 收款认领通知
     */
    @TableField("claimnoticebillno")
    private String claimnoticebillno;

    /**
     * 源单类型
     */
    @TableField("sourcebilltype")
    private String sourcebilltype;

    /**
     * 手续费
     */
    @TableField("transfercharge")
    private Double transfercharge;

    /**
     * 流程序列号
     */
    @TableField("flowserialno")
    private String flowserialno;

    /**
     * 排序id
     */
    @TableField("sortid")
    private String sortid;

    /**
     * 银企请求流水号
     */
    @TableField("requestserialno")
    private String requestserialno;

    /**
     * 银行响应流水号
     */
    @TableField("responseserailno")
    private String responseserailno;

    /**
     * kd标识
     */
    @TableField("kdretflag")
    private String kdretflag;

    /**
     * 被代理账号
     */
    @TableField("agentaccno")
    private String agentaccno;

    /**
     * 被代理户名
     */
    @TableField("agentaccname")
    private String agentaccname;

    /**
     * 被代理户开户行
     */
    @TableField("agentaccbankname")
    private String agentaccbankname;

    /**
     * bustype
     */
    @TableField("bustype")
    private String bustype;

    /**
     * 银企付款提交的批次号
     */
    @TableField("batchno")
    private String batchno;

    /**
     * 票号
     */
    @TableField("billnobillno")
    private String billnobillno;

    /**
     * 记账日期
     */
    @TableField("transdate")
    private LocalDateTime transdate;

    /**
     * extdata
     */
    @TableField("extdata")
    private String extdata;

    /**
     * 最后修改时间
     */
    @TableField("lastmodifytime")
    private LocalDateTime lastmodifytime;

    /**
     * 银行流水号
     */
    @TableField("bankdetailno")
    private String bankdetailno;

    /**
     * 银行主键
     */
    @TableField("uniqueseq")
    private String uniqueseq;

    /**
     * 保融对账码
     */
    @TableField("cico_reconciliationcode")
    private String cicoReconciliationcode;

    /**
     * 付款状态
     */
    @TableField("cico_activepayment")
    private String cicoActivepayment;

    /**
     * 保融uid
     */
    @TableField("cico_bruid")
    private String cicoBruid;

    /**
     * 付款单号
     */
    @TableField("cico_billno")
    private String cicoBillno;

    /**
     * 入账方式
     */
    @TableField("receredway")
    private String receredway;

    /**
     * 是否手工关联
     */
    @TableField("ishandlink")
    private Integer ishandlink;

    /**
     * 资金组织.编码
     */
    @TableField("company_number")
    private String companyNumber;

    /**
     * 资金组织.名称
     */
    @TableField("company_name")
    private String companyName;

    /**
     * 银行账号.银行账号
     */
    @TableField("accountbank_bankaccountnumber")
    private String accountbankBankaccountnumber;

    /**
     * 银行账号.银企账户名称
     */
    @TableField("accountbank_acctname")
    private String accountbankAcctname;

    /**
     * 银行账号.银行账户名称
     */
    @TableField("accountbank_name")
    private String accountbankName;

    /**
     * 开户银行.编码
     */
    @TableField("bank_number")
    private String bankNumber;

    /**
     * 开户银行.名称
     */
    @TableField("bank_name")
    private String bankName;

    /**
     * 币别.名称
     */
    @TableField("currency_name")
    private String currencyName;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotCompareColumn
    @TableField(value = "data_create_time", updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime dataCreateTime;
    @NotCompareColumn
    @TableField(value = "data_update_time", updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime dataUpdateTime;

}
