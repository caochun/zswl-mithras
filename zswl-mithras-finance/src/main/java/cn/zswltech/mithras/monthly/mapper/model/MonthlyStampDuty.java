package cn.zswltech.mithras.monthly.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author chenyifei
 * @since 2024-05-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("monthly_stamp_duty")
public class MonthlyStampDuty extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 类型-项目端/资金端
     */
    @TableField("type")
    private String type;

    /**
     * 合同id
     */
    @TableField("contract_id")
    private Long contractId;

    /**
     * 借据id
     */
    @TableField("receipt_id")
    private Long receiptId;

    /**
     * 客户名称
     */
    @TableField("client_name")
    private String clientName;

    @TableField("client_id")
    private Long clientId;

    /**
     * 合同编号
     */
    @TableField("contract_code")
    private String contractCode;

    /**
     * 融资id
     */
    @TableField("financing_id")
    private Long financingId;

    /**
     * 融资渠道
     */
    @TableField("organization_name")
    private String organizationName;

    /**
     * 融资编号
     */
    @TableField("financing_code")
    private String financingCode;

    /**
     * 印花税
     */
    @TableField("stamp_duty")
    private Long stampDuty;

    /**
     * 日期
     */
    @TableField("date")
    private LocalDate date;

    /**
     * 收入是否确认，0-否，1-是
     */
    @TableField("is_confirmed")
    private Integer isConfirmed;

    /**
     * 逻辑删除
     */
    @TableField("deleted")
    private Integer deleted;


    @TableField("confirm_time")
    private LocalDateTime confirmTime;

    @TableField("confirm_batch")
    private String confirmBatch;


}
