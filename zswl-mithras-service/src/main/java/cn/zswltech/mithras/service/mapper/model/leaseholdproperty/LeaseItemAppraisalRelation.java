package cn.zswltech.mithras.service.mapper.model.leaseholdproperty;

import cn.zswltech.mithras.common.model.BaseModel;
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
 * 评估机构与租赁物关联表
 * </p>
 *
 * @author chenyifei
 * @since 2024-08-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("lease_item_appraisal_relation")
public class LeaseItemAppraisalRelation extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 评估机构id
     */
    @TableField("company_id")
    private Long companyId;

    /**
     * 租赁物id
     */
    @TableField("lease_item_id")
    private Long leaseItemId;

    /**
     * 用途
     */
    @TableField("purpose")
    private String purpose;

    /**
     * 是否被选中
     */
    @TableField("select_type")
    private String selectType;

    /**
     * 是否白名单准入
     */
    @TableField("is_whitelist")
    private Integer isWhitelist;

    /**
     * 逻辑删除
     */
    @TableField("deleted")
    private Integer deleted;



}
