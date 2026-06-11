package cn.zswltech.mithras.fund.directfinancing.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 直接融资-费用明细
 * @author zhaozhengkang
 * @date 2023-06-17
 */
@Data
public class FundDirectFinancingFeeDetail extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("financing_id")
    private Long financingId;

    /**
     * 中介机构
     */
    @TableField("intermediaries")
    private String intermediaries;

    /**
     * 机构名称
     */
    @TableField("institution_name")
    private String institutionName;

    /**
    * 费用类型
    */
    @TableField("expense_type")
    private String expenseType;

    /**
     * 金额
     */
    @TableField("amount")
    private Long amount;

//    /**
//     * 核销状态
//     */
//    @TableField("write_off_status")
//    private String writeOffStatus;

    /**
     * 支付时间
     */
    @TableField("pay_date")
    private LocalDate payDate;

    /**
    * 支付方式
    */
    @TableField("payment_method")
    private String paymentMethod;

    /**
    * 备注
    */
    @TableField("remark")
    private String remark;

}
