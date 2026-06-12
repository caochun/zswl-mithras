package cn.zswltech.mithras.workflow.persistence.model.prepare;

import cn.zswltech.mithras.workflow.enums.CommonProcessPrepareStatus;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author luyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@TableName("common_process_prepare")
public class CommonProcessPrepare extends BaseModel implements Serializable, IEntity {
    private static final long serialVersionUID = -9025392687670931311L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 流程类型
     */
    private String processType;

    /**
     * 业务id
     */
    @TableField("business_id")
    private String businessId;

    /**
     * 表单名称
     */
    private String formName;

    /**
     * 项目名称
     */
    private String projName;

    /***
     * 项目编号
     */
    private String projCode;

    /**
     * 客户名称
     */
    private String clientName;

    /**
     * 当前节点
     */
    private String currentNode;

    /**
     * 当前审批人
     */
    private String currentAssignee;

    /**
     * 申请时间
     */
    private LocalDateTime applyTime;

    /**
     * {@link CommonProcessPrepareStatus#name(}
     **/
    private String status;

    /**
     * 业务数据
     */
    @TableField("business_data")
    private String businessData;

    @TableField("is_asset_confirm")
    private String isAssetConfirm;

    /**
     * 租后检查是否是首次
     */
    @TableField("first_check_flag")
    private Integer firstCheckFlag;


    @Override
    public void setMainId(Long id) {
        this.id = id;
    }

    @Override
    public Long getMainId() {
        return this.id;
    }
}
