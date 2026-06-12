package cn.zswltech.mithras.workflow.persistence.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author yibin
 */
@EqualsAndHashCode(callSuper = false)
@Data
@Accessors(chain = true)
public class FlowQueryExtra extends BaseModel {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 流程类型
     */
    private String flowKey;

    /**
     * 流程实例id
     */
    private String instanceId;


    /**
     * 业务表id；流程实例中的BUSINESS_KEY_
     */
    private Long bizId;

    /**
     * 客户名称
     */
    private String clientName;

    /**
     * 项目名称
     */
    private String projName;

    /**
     * 关联项目名称
     */
    private String projNameInfo;

    /**
     * 项目编号
     */
    private String projCode;

    /**
     * 合同编号
     */
    private String contractCode;
}
