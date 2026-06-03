package cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yibin
 */
@Data
@TableName(value = "process_modify_remark", autoResultMap = true)
public class ProcessModifyRemark extends BaseModel implements Serializable, IEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String moduleType;

    /**
     * remark_type
     */
    private String remarkType;

    /**
     * remark_json
     */
    @TableField(value = "remark_json", typeHandler = JacksonTypeHandler.class)
    private ModifyObj remarkJson;

    /**
     * main_id
     */
    private Long mainId;

    @Data
    public static class ModifyObj {
        private String reason;
        private String originalContent;
        private String toBeContent;
    }
}
