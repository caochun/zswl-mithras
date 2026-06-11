package cn.zswltech.mithras.leaseholdproperty.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/9/2
 * @description 评估机构白名单
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("appraisal_company_whitelist")
public class AppraisalCompanyWhitelist extends BaseModel implements IEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 部门id
     */
    @TableField(value = "dept_id")
    private Long deptId;

    /**
     * 最后操作人id
     */
    @TableField(value = "last_operator_id")
    private Long lastOperatorId;

    /**
     * 评估机构名称
     */
    @TableField(value = "company_name")
    private String companyName;

    /**
     * 统一社会信用代码
     */
    @TableField(value = "usc_code")
    private String uscCode;

    /**
     * 成立日期
     */
    @TableField(value = "establish_date")
    private LocalDate establishDate;

    /**
     * 营业许可证到期日期
     */
    @TableField(value = "license_expire_date")
    private LocalDate licenseExpireDate;

    /**
     * 营业许可证是否长期
     */
    @TableField(value = "license_is_long_term")
    private Integer licenseIsLongTerm;

    /**
     * 业务范围
     */
    @TableField(value = "business_scope")
    private String businessScope;

    /**
     * 状态
     */
    @TableField(value = "record_status")
    private String recordStatus;

    /**
     * 流程状态
     */
    @TableField(value = "process_status")
    private String processStatus;

    /**
     * 评估机构白名单生效日期
     */
    @TableField(value = "record_effect_date")
    private LocalDate recordEffectDate;

    /**
     * 评估机构白名单到期日期
     */
    @TableField(value = "record_expire_date")
    private LocalDate recordExpireDate;

    /**
     * 出库原因
     */
    @TableField(value = "out_reason")
    private String outReason;

    @Override
    public void setMainId(Long id) {
        this.id = id;
    }

    @Override
    public Long getMainId() {
        return id;
    }
}
