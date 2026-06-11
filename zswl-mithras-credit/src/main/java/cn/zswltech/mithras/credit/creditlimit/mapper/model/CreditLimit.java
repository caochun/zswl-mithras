package cn.zswltech.mithras.credit.creditlimit.mapper.model;

import cn.zswltech.mithras.credit.creditlimit.enums.CreditLimitBizTypeEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/9/3
 * @description 授信额度表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("credit_limit")
public class CreditLimit extends BaseModelWithLogicDelete {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 业务类型 {@link CreditLimitBizTypeEnum#name()}
     */
    @TableField(value = "biz_type")
    private String bizType;

    /**
     * 授信主体key，能唯一标识一个授信主体
     */
    @TableField(value = "granting_subject_key")
    private String grantingSubjectKey;

    /**
     * 业务源key，能唯一标识一个授信（比如资金端可以是授信id，项目端可以是评审id）
     */
    @TableField(value = "biz_source_key")
    private String bizSourceKey;

    /**
     * 查询key
     */
    @TableField(value = "query_key")
    private String queryKey;

    /**
     * 授信总额度
     */
    @TableField(value = "total_limit")
    private Long totalLimit;

    /**
     * 担保额度
     */
    @TableField(value = "guarantee_limit")
    private Long guaranteeLimit;

    /**
     * 信用额度
     */
    @TableField(value = "credit_limit")
    private Long creditLimit;

    /**
     * 已占用授信总额度
     */
    @TableField(value = "occupy_total_limit")
    private Long occupyTotalLimit;

    /**
     * 已占用担保额度
     */
    @TableField(value = "occupy_guarantee_limit")
    private Long occupyGuaranteeLimit;

    /**
     * 已占用信用额度
     */
    @TableField(value = "occupy_credit_limit")
    private Long occupyCreditLimit;

    /**
     * 有效期-起
     */
    @TableField(value = "effective_date_from")
    private LocalDate effectiveDateFrom;

    /**
     * 有效期-止
     */
    @TableField(value = "effective_date_to")
    private LocalDate effectiveDateTo;

    /**
     * 额度是否可循环，0-否，1-是
     */
    @TableField(value = "recyclable")
    private Integer recyclable;

    /**
     * 状态
     */
    @TableField(value = "status")
    private String status;
}
