package cn.zswltech.mithras.kpi.mapper.model;

import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 绩效考核-投放信息记录表
 * @author vico
 * @date 2024-09-27
 */
@Data
public class KpiPaymentAmountRecord extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 生效月份
    */
    @TableField("effect_month")
    private LocalDate effectMonth;

    /**
    * 批次号
    */
    @TableField("batch_number")
    private Integer batchNumber;

    /**
    * 合同id
    */
    @TableField("contract_id")
    private Long contractId;

    @TableField("proj_review_id")
    private Long projReviewId;

    /**
    * 当月投放金额
    */
    @TableField("payment_amount")
    private Long paymentAmount;

}
