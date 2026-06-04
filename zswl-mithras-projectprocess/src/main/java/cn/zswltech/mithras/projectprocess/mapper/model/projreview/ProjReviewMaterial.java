package cn.zswltech.mithras.projectprocess.mapper.model.projreview;

import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewMaterialCommentsEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import cn.zswltech.mithras.service.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Description: 评审材料审核
 * @Author: heng
 * @Date: 2026/1/7 08:29
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProjReviewMaterial extends BaseModel implements Serializable, IEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 客户id
     */
    @TableField("client_id")
    private Long clientId;


    /**
     * 客户类型
     */
    @TableField("client_type")
    private String clientType;

    /**
     * 客户名称
     */
    @TableField("name")
    private String name;

    /**
     * 业务类型
     */
    @TableField("business_type")
    private String businessType;

    /**
     * 展示类型名
     */
    @TableField("client_type_name")
    private String clientTypeName;

    /**
     * 项目评审id
     */
    @TableField("proj_review_id")
    private Long projReviewId;

    /**
     * 项目编号
     */
    @TableField("proj_code")
    private String projCode;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 审核意见
     * {@link ProjReviewMaterialCommentsEnum#name()}
     */
    @TableField("review_comments")
    private String reviewComments;

    /**
     * 审核说明
     */
    @TableField("review_instructions")
    @IncludeNull
    private String reviewInstructions;

    /**
     * 文件Id,使用,分割记录
     */
    @TableField("record_id")
    private String recordId;

    /**
     * 最后版本标记
     */
    @TableField("last_version_flag")
    private Integer lastVersionFlag;

    /**
     * 实现IEntity接口的主键方法
     */
    @Override
    public void setMainId(Long id) {
        this.setId(id);
    }

    /**
     * 实现IEntity接口的主键方法
     */
    @Override
    public Long getMainId() {
        return this.getId();
    }
}
