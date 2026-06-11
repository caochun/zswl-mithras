package cn.zswltech.mithras.contract.overdue.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 文书用印
 * @author zhaozhengkang
 * @date 2024-11-04
 */
@Data
@TableName("oc_doc_printing")
public class DocPrinting extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 用印编号
    */
    @TableField("code")
    private String code;

    /**
    * 用印类型
    */
    @TableField("type")
    private String type;

    /**
    * 用印原因
    */
    @TableField("reason")
    private String reason;

    @TableField("process_status")
    private String processStatus;

    @TableField("process_id")
    private String processId;

    @TableField("lock_version")
    private Long lockVersion;

    @Override
    public void setMainId(Long id) {
        setId(id);
    }

    @Override
    public Long getMainId() {
        return getId();
    }
}
