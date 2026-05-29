package cn.zswltech.mithras.service.mapper.model.afterlease;
import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 租后检查计划-基本信息表
 * @author vico
 * @date 2024-04-22
 */
@Data
public class AfterLeaseCheckChangeRecord extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 计划id
    */
    @TableField("plan_id")
    private Long planId;

    /**
    * 计划-客户id
    */
    @TableField("check_plan_client_id")
    private Long checkPlanClientId;

    /**
    * 变更前内容
    */
    @TableField("old_content")
    private String oldContent;

    /**
    * 变更后内容
    */
    @TableField("now_content")
    private String nowContent;


}
