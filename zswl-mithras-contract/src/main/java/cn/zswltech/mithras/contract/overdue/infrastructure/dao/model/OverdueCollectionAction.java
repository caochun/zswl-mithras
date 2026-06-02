package cn.zswltech.mithras.contract.overdue.infrastructure.dao.model;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description: 逾期催收动作
 * @author: zhaozhengkang
 * @date: 2024/10/21 16:32
 */
@Data
@TableName("oc_overdue_collection_action")
public class OverdueCollectionAction extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("code")
    private String code;

    @TableField("oc_id")
    private Long ocId;

    @TableField("date")
    private LocalDateTime date;

    @TableField("type")
    private String type;

    @TableField("`describe`")
    private String describe;

    @TableField("letter_type")
    private String letterType;

    @TableField("contract_ids")
    private String contractIds;

    @TableField("contract_codes")
    private String contractCodes;

    @TableField("process_status")
    private String processStatus;

    @TableField("process_id")
    private String processId;

    @Override
    public void setMainId(Long id) {
        setId(id);
    }

    @Override
    public Long getMainId() {
        return getId();
    }
}
