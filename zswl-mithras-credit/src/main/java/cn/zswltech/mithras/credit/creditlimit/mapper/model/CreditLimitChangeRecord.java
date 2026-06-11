package cn.zswltech.mithras.credit.creditlimit.mapper.model;

import cn.zswltech.mithras.credit.creditlimit.enums.CreditLimitBizTypeEnum;
import cn.zswltech.mithras.credit.creditlimit.enums.CreditLimitChangeTypeEnum;
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
 * @date 2024/9/4
 * @description 授信额度占用变更记录表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("credit_limit_change_record")
public class CreditLimitChangeRecord extends BaseModelWithLogicDelete {
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
     * 业务目标key，能唯一标识一个占用授信的业务数据（比如资金端可以是融资id，项目端可以是合同id）
     */
    @TableField(value = "biz_target_key")
    private String bizTargetKey;

    /**
     * 查询key
     */
    @TableField(value = "query_key")
    private String queryKey;

    /**
     * 变更类型 {@link CreditLimitChangeTypeEnum#name()}
     */
    @TableField(value = "change_type")
    private String changeType;

    /**
     * 变更日期
     */
    @TableField(value = "change_date")
    private LocalDate changeDate;

    /**
     * 变更总额度
     */
    @TableField(value = "change_total_limit")
    private Long changeTotalLimit;

    /**
     * 变更担保额度
     */
    @TableField(value = "change_guarantee_limit")
    private Long changeGuaranteeLimit;

    /**
     * 变更信用额度
     */
    @TableField(value = "change_credit_limit")
    private Long changeCreditLimit;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;
}
