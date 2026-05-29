package cn.zswltech.mithras.service.mapper.model.afterlease;

import cn.zswltech.mithras.common.model.BaseModel;
import cn.zswltech.mithras.common.model.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author dingqi
 * @date 2022/11/17
 * @description
 */
@Deprecated
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("new_after_lease_check_report_content")
public class NewAfterLeaseCheckReportContent extends BaseModel implements Serializable, IEntity {
    private static final long serialVersionUID = 8284650362233811726L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("check_plan_client_id")
    private Long checkPlanClientId;

    @TableField("template_id")
    private Long templateId;

    @TableField("template_code")
    private String templateCode;

    @TableField("template_group_name")
    private String templateGroupName;

    @TableField("template_title")
    private String templateTitle;

    @TableField("template_content_input_label")
    private String templateContentInputLabel;

    @TableField("template_content_input_type")
    private String templateContentInputType;

    @TableField("template_content_input_option")
    private String templateContentInputOption;

    @TableField("template_order_num")
    private Integer templateOrderNum;

    @TableField("content")
    private String content;

    @Override
    public void setMainId(Long id) {
        this.checkPlanClientId = id;
    }

    @Override
    public Long getMainId() {
        return this.checkPlanClientId;
    }
}
