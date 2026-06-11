package cn.zswltech.mithras.payment.model;

import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Optional;

/**
 * FTP考核信息表
 *
 * @author yangxiong
 * @TableName ftp_assessment_info
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "ftp_assessment_info")
public class FtpAssessmentInfo extends BaseModelWithLogicDelete implements Serializable {
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
     * 基础价格
     */
    @TableField(value = "base_price")
    private Long basePrice;

    /**
     * 山区调整
     */
    @TableField(value = "mountain_adjustment")
    private Long mountainAdjustment;

    /**
     * 评级调整
     */
    @TableField(value = "grade_adjustment")
    private Long gradeAdjustment;

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
     * 票据价格
     */
    @TableField(value = "ticket_price")
    private Long ticketPrice;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 杭甬特殊调整
     */
    @TableField(value = "hangyong_special_adjustment")
    private Long hangyongSpecialAdjustment;

    /**
     * FTP计息变更申请记录id
     */
    @TableField(value = "ftp_interest_change_apply_record_id")
    private Long ftpInterestChangeApplyRecordId;

    /**
     * FTP价格生效日期
     */
    @TableField(value = "effect_date")
    private LocalDate effectDate;

    /**
     * FTP计息变更差额调整日期
     */
    @TableField(value = "ftp_interest_diff_date")
    private LocalDate ftpInterestDiffDate;

    /**
     * FTP价格是否生效 {@link YesOrNoNumberEnum#getCode()}
     */
    @TableField(value = "is_effect")
    private Integer isEffect;

    /**
     * 是否特殊事项 {@link YesOrNoNumberEnum#getCode()}
     */
    @TableField(value = "is_special_matter")
    private Integer isSpecialMatter;

    /**
     * 借据id
     */
    @TableField(value = "receipt_id")
    private Long receiptId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 设置考核价格, 切记！该方法在设置【指导价】、【质押价】、【手工调整】、【杭甬特殊调整】后调用
     */
    public void setAssessmentPrice() {
        assessmentPrice = Optional.ofNullable(guidePrice).orElse(0L) + Optional.ofNullable(pledgePrice).orElse(0L) + Optional.ofNullable(handAdjustment).orElse(0L) + Optional.ofNullable(hangyongSpecialAdjustment).orElse(0L);
    }

    /**
     * 设置指引价格, 切记！该方法在设置【基础价格】、【山区调整】、【评级调整】后调用
     */
    public void setGuidePrice() {
        guidePrice = Optional.ofNullable(basePrice).orElse(0L) + Optional.ofNullable(mountainAdjustment).orElse(0L) + Optional.ofNullable(gradeAdjustment).orElse(0L);
    }
}
