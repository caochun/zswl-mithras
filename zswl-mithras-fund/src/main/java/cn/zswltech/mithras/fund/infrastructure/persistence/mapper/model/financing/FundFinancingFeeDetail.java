package cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 间融-费用明细
 * @author zhaozhengkang
 * @date 2023-06-17
 */
@Data
@TableName("fund_financing_fee_detail")
public class FundFinancingFeeDetail extends BaseModel implements IEntity {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("financing_id")
    private Long financingId;

    @TableField("organization_id")
    private Long organizationId;

    @TableField("organization_name")
    private String organizationName;

    /**
    * 费用类型
    */
    @TableField("expense_type")
    private String expenseType;

    /**
     * 金额（万元）
     */
    @TableField("amount")
    private Long amount;

    /**
    * 支付方式
    */
    @TableField("payment_method")
    private String paymentMethod;

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
    * 备注
    */
    @TableField("remark")
    private String remark;

    @Override
    public void setMainId(Long id) {
        this.financingId = id;
    }

    @Override
    public Long getMainId() {
        return this.financingId;
    }
}
