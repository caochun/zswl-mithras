package cn.zswltech.mithras.service.mapper.model;

import cn.zswltech.mithras.service.enums.CreditLimitBizTypeEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2024/9/3
 * @description 授信额度占用明细表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("credit_limit_detail")
public class CreditLimitDetail extends BaseModelWithLogicDelete {
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
     * 已占用总额度
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
}
