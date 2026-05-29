package cn.zswltech.mithras.service.mapper.model.fund.financing;

import cn.zswltech.mithras.common.model.BaseModel;
import cn.zswltech.mithras.common.model.IEntity;
import cn.zswltech.mithras.service.service.fund.financing.fms.IFundFinancingStateMachineEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 融资授信关联表
 * </p>
 *
 * @author chenyifei
 * @since 2024-10-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fund_financing_credit_ref")
public class FundFinancingCreditRef extends BaseModel implements IEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 间融合同id
     */
    @TableField("financing_id")
    private Long financingId;

    /**
     * 授信id
     */
    @TableField("credit_id")
    private Long creditId;

    /**
     * 机构id
     */
    @TableField("organization_id")
    private Long organizationId;

    /**
     * 最后更新人id
     */
    @TableField("update_by")
    private Long updateBy;

    /**
     * 创建人、发起人
     */
    @TableField("create_by")
    private Long createBy;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;


    @Override
    public void setMainId(Long id) {
        this.id = id;
    }

    @Override
    public Long getMainId() {
        return this.id;
    }
}
