package cn.zswltech.mithras.service.overdue.infrastructure.dao.model;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import cn.zswltech.mithras.service.mapper.model.BaseModel;

/**
 * @description 诉讼登记案件进展
 * @author zhaozhengkang
 * @date 2024-10-30
 */
@Data
@TableName("oc_litigation_case_progress")
public class LitigationCaseProgress extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 所属诉讼登记id
    */
    @TableField("lr_id")
    private Long lrId;

    /**
    * 诉讼阶段
    */
    @TableField("stage")
    private String stage;

    /**
    * 诉讼状态
    */
    @TableField("status")
    private String status;

}
