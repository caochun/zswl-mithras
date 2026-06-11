package cn.zswltech.mithras.customer.hymx.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("client_hymx")
public class ClientHymx extends BaseModel implements IEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("client_name")
    private String clientName;
    @TableField("client_code")
    private String clientCode;
    @TableField("project_manager")
    private Long projectManager;
    @TableField("belong_dept_id")
    private Long belongDeptId;

    @Override
    public void setMainId(Long id) {
        this.id = id;
    }

    @Override
    public Long getMainId() {
        return this.id;
    }
}
