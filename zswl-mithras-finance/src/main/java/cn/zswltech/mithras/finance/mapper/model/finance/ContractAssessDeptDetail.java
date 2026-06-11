package cn.zswltech.mithras.finance.mapper.model.finance;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author lizhao
 * @date 2024/6/18
 * @description 考核部门-合同配置表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("contract_assess_dept_detail")
public class ContractAssessDeptDetail extends BaseModel {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 合同id
     */
    @TableField(value = "contract_id")
    private Long contractId;

    /**
     * 考核部门id
     */
    @TableField(value = "assess_dept_id")
    private Long assessDeptId;

    /**
     * 创建人id、发起人id
     */
    @TableField(value = "create_by")
    private Long createBy;

    /**
     * 创建时间。默认当前时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 最后更新人id
     */
    @TableField(value = "update_by")
    private Long updateBy;

    /**
     * 更新时间；每次记录变化，自动更新为当前时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除，0：未删除，1：已删除，默认0
     */
    @TableField(value = "deleted")
    private Integer deleted;
}
