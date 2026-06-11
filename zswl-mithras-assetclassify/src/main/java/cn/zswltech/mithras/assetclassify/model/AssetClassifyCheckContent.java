package cn.zswltech.mithras.assetclassify.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 五级分类检查报告
 * @author: jackerhe 
 * @date: 2023/1/8 10:11 上午
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("asset_classify_check_content")
public class AssetClassifyCheckContent extends BaseModel implements IEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("asset_classify_id")
    private Long assetClassifyId;

    /**
     *租后检查计划ID
     **/
    @TableField("after_lease_check_plan_project_id")
    private Long afterLeaseCheckPlanProjectId;

    /**
     * 主表id
     */
    @TableField("asset_classify_client_id")
    private Long assetClassifyClientId;

    @TableField("template_id")
    private Long templateId;

    @TableField("template_code")
    private String templateCode;

    @TableField("template_group_name")
    private String templateGroupName;

    @TableField("template_title")
    private String templateTitle;

    @TableField("template_content_input_type")
    private String templateContentInputType;

    @TableField("template_content_input_option")
    private String templateContentInputOption;

    @TableField("template_order_num")
    private Integer templateOrderNum;

    @TableField(value = "content", updateStrategy = FieldStrategy.IGNORED)
    private String content;

    @Override
    public void setMainId(Long id) {
        this.assetClassifyClientId = id;
    }

    @Override
    public Long getMainId() {
        return this.assetClassifyClientId;
    }
}
