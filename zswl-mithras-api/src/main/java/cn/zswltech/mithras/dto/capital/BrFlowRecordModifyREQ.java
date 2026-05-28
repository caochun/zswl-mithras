package cn.zswltech.mithras.dto.capital;
import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 保融流水表
 * @author vico
 * @date 2024-06-17
 */
@Data
@ApiModel("保融流水表编辑-请求体")
public class BrFlowRecordModifyREQ {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
    * 序号
    */
    @ApiModelProperty(value = "序号")
    private Integer rn;

    /**
    * 保融流水id
    */
    @ApiModelProperty(value = "保融流水id")
    private String bruid;

    /**
    * 客户名称
    */
    @ApiModelProperty(value = "客户名称")
    private String orgName;

    /**
    * 账户编码
    */
    @ApiModelProperty(value = "账户编码")
    private String accountnumber;

    /**
    * 唯一标识
    */
    @ApiModelProperty(value = "唯一标识")
    private String transseq;

    /**
    * 交易日期时间
    */
    @ApiModelProperty(value = "交易日期时间")
    private LocalDateTime tradedatetime;

    /**
    * 交易日期
    */
    @ApiModelProperty(value = "交易日期")
    private LocalDateTime tradedate;

    /**
    * 交易时间
    */
    @ApiModelProperty(value = "交易时间")
    private LocalDateTime tradetime;

    /**
    * 起息日期
    */
    @ApiModelProperty(value = "起息日期")
    private LocalDateTime qixiriqi;

    /**
    * 交易方向1支出，2收入
    */
    @ApiModelProperty(value = "交易方向1支出，2收入")
    private String moneyway;

    /**
    * 交易金额(毫厘)
    */
    @ApiModelProperty(value = "交易金额(毫厘)")
    private Long amount;

    /**
    * 当前余额（毫厘）
    */
    @ApiModelProperty(value = "当前余额（毫厘）")
    private Long currentbalance;

    /**
    * 更新日期时间
    */
    @ApiModelProperty(value = "更新日期时间")
    private String lastmodifiedon;

    /**
    * 对账码
    */
    @ApiModelProperty(value = "对账码")
    private String checkcode;

    /**
    * 用途
    */
    @ApiModelProperty(value = "用途")
    private String purpose;

    /**
    * 备注
    */
    @ApiModelProperty(value = "备注")
    private String comments;

    /**
    * 对方账号
    */
    @ApiModelProperty(value = "对方账号")
    private String oppositeaccountnumber;

    /**
    * 对方户名
    */
    @ApiModelProperty(value = "对方户名")
    private String oppositeaccountname;

    /**
    * 对方银行
    */
    @ApiModelProperty(value = "对方银行")
    private String oppositebank;

    /**
    * 票据号
    */
    @ApiModelProperty(value = "票据号")
    private String billcode;

    /**
    * 票据类型
    */
    @ApiModelProperty(value = "票据类型")
    private String billtype;

    /**
    * 核对批号
    */
    @ApiModelProperty(value = "核对批号")
    private String checkbatchno;

    /**
    * 银行流水号
    */
    @ApiModelProperty(value = "银行流水号")
    private String bankserialnumber;

    /**
    * 资金系统单据号
    */
    @ApiModelProperty(value = "资金系统单据号")
    private String notecode;

    /**
    * 银行业务参考号
    */
    @ApiModelProperty(value = "银行业务参考号")
    private String bankbusref;

    /**
    * 电子回单编号
    */
    @ApiModelProperty(value = "电子回单编号")
    private String receiptcode;

    /**
    * 业务回单类型
    */
    @ApiModelProperty(value = "业务回单类型")
    private String receiptbustypno;

    /**
    * 回单个性化信息
    */
    @ApiModelProperty(value = "回单个性化信息")
    private String receiptinfo;

    /**
    * 企业业务参考号
    */
    @ApiModelProperty(value = "企业业务参考号")
    private String busref;

}
