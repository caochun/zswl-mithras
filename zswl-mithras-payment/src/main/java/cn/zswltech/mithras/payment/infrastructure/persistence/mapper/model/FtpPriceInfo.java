package cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model;

import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * FTP价格表
 *
 * @author yangxiong
 * @TableName ftp_price_info
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "ftp_price_info")
public class FtpPriceInfo extends BaseModelWithLogicDelete implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 付款ID
     */
    @TableField(value = "payment_id")
    private Long paymentId;

    /**
     * 记录日期
     */
    @TableField(value = "record_date")
    private LocalDate recordDate;

    /**
     * 指引价格
     */
    @TableField(value = "guide_price")
    private Long guidePrice;

    /**
     * 是否质押
     */
    @TableField(value = "pledge_price")
    private Long pledgePrice;

    /**
     * 是否逾期
     */
    @TableField(value = "overdue_price")
    private Long overduePrice;

    /**
     * 手工调整
     */
    @TableField(value = "hand_adjustment")
    private Long handAdjustment;

    /**
     * 考核价格
     */
    @TableField(value = "assessment_price")
    private Long assessmentPrice;

    /**
     * 成本是否已确认
     */
    @TableField(value = "cost_is_confirmed")
    private Integer costIsConfirmed;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}