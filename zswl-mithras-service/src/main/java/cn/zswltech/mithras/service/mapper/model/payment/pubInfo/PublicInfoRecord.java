package cn.zswltech.mithras.service.mapper.model.payment.pubInfo;

import cn.zswltech.mithras.common.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.*;

/**
 * 公开信息查询报告表
 *
 * @author bigbear
 * @TableName public_info_record
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "public_info_record")
@EqualsAndHashCode(callSuper = true)
public class PublicInfoRecord extends BaseModelWithLogicDelete implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 公开信息ID
     */
    @TableField(value = "public_info_query_id")
    private Long publicInfoQueryId;

    /**
     * 关联配置表的KEY {@link cn.zswltech.mithras.service.enums.payment.pubinfo.PublicInfoRowKeyEnum}
     */
    @TableField(value = "config_key")
    private String configKey;

    /**
     * 付款申请ID
     */
    @TableField(value = "payment_id")
    private Long paymentId;

    /**
     * 调查类型
     */
    @TableField(value = "investigation_type")
    private String investigationType;

    /**
     * 调查说明
     */
    @TableField(value = "investigation_explain")
    private String investigationExplain;

    /**
     * 项目经理说明
     */
    @TableField(value = "projectManager_explain")
    private String projectmanagerExplain;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}