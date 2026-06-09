package cn.zswltech.mithras.creditlimit.mapper.model;

import cn.zswltech.mithras.creditlimit.enums.CreditLimitBizTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2024/9/4
 * @description 业务和授信关联表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("credit_business_ref")
public class CreditBusinessRef extends BaseModelWithLogicDelete {
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
     * 关联关系是否生效 {@link YesOrNoNumberEnum#getCode()}
     */
    @TableField(value = "effective")
    private Integer effective;
}
