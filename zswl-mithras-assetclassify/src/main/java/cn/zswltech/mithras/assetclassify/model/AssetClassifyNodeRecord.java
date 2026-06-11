package cn.zswltech.mithras.assetclassify.model;

import cn.zswltech.mithras.assetclassify.enums.AssetClassifyBizNodeEnum;
import cn.zswltech.mithras.assetclassify.enums.AssetClassifyStatusEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2023/1/3
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("asset_classify_node_record")
public class AssetClassifyNodeRecord extends BaseModel implements IEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 主表id
     */
    @TableField("asset_classify_id")
    private Long assetClassifyId;

    /**
     * 节点名称 {@link AssetClassifyBizNodeEnum#name()}
     */
    @TableField("node_name")
    private String nodeName;

    /**
     * 节点开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 节点结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     *节点所处状态 {@link AssetClassifyStatusEnum#name()}
     **/
    @TableField("node_statue")
    private String nodeStatue;

    @Override
    public void setMainId(Long id) {
        this.assetClassifyId = id;
    }

    @Override
    public Long getMainId() {
        return this.assetClassifyId;
    }
}
