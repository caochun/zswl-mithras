package cn.zswltech.mithras.service.mapper.model.third;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description 保融流水表
 * @author vico
 * @date 2024-06-17
 */
@Data
public class BrFlowRecord extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 序号
    */
    @TableField("rn")
    private Integer rn;

    /**
    * 保融流水id
    */
    @TableField("bruid")
    private String bruid;

    /**
    * 客户名称
    */
    @TableField("org_name")
    private String orgName;

    /**
    * 账户编码
    */
    @TableField("accountnumber")
    private String accountnumber;

    /**
    * 唯一标识
    */
    @TableField("transseq")
    private String transseq;

    /**
    * 交易日期时间
    */
    @TableField("tradedatetime")
    private LocalDateTime tradedatetime;

    /**
    * 交易日期
    */
    @TableField("tradedate")
    private LocalDateTime tradedate;

    /**
    * 交易时间
    */
    @TableField("tradetime")
    private LocalDateTime tradetime;

    /**
    * 起息日期
    */
    @TableField("qixiriqi")
    private LocalDateTime qixiriqi;

    /**
    * 交易方向1支出，2收入
    */
    @TableField("moneyway")
    private String moneyway;

    /**
    * 交易金额(毫厘)
    */
    @TableField("amount")
    private Long amount;

    /**
    * 当前余额（毫厘）
    */
    @TableField("currentbalance")
    private Long currentbalance;

    /**
    * 更新日期时间
    */
    @TableField("lastmodifiedon")
    private String lastmodifiedon;

    /**
    * 对账码
    */
    @TableField("checkcode")
    private String checkcode;

    /**
    * 用途
    */
    @TableField("purpose")
    private String purpose;

    /**
    * 备注
    */
    @TableField("comments")
    private String comments;

    /**
    * 对方账号
    */
    @TableField("oppositeaccountnumber")
    private String oppositeaccountnumber;

    /**
    * 对方户名
    */
    @TableField("oppositeaccountname")
    private String oppositeaccountname;

    /**
    * 对方银行
    */
    @TableField("oppositebank")
    private String oppositebank;

    /**
    * 票据号
    */
    @TableField("billcode")
    private String billcode;

    /**
    * 票据类型
    */
    @TableField("billtype")
    private String billtype;

    /**
    * 核对批号
    */
    @TableField("checkbatchno")
    private String checkbatchno;

    /**
    * 银行流水号
    */
    @TableField("bankserialnumber")
    private String bankserialnumber;

    /**
    * 资金系统单据号
    */
    @TableField("notecode")
    private String notecode;

    /**
    * 银行业务参考号
    */
    @TableField("bankbusref")
    private String bankbusref;

    /**
    * 电子回单编号
    */
    @TableField("receiptcode")
    private String receiptcode;

    /**
    * 业务回单类型
    */
    @TableField("receiptbustypno")
    private String receiptbustypno;

    /**
    * 回单个性化信息
    */
    @TableField("receiptinfo")
    private String receiptinfo;

    /**
    * 企业业务参考号
    */
    @TableField("busref")
    private String busref;

    /**
     * 是否删除，0：未删除，1：已删除，默认0
     */
    @TableField(value = "deleted")
    private Integer deleted;

    /**
     * 是否忽略，0：未忽略，1：已忽略，默认0
     */
    @TableField(value = "ignore_flag")
    private Integer ignoreFlag;

}
