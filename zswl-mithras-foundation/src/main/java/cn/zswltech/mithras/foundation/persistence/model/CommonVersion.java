package cn.zswltech.mithras.foundation.persistence.model;

import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author yeqing
 * @description 客户信息版本表
 * @date 2022-06-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("common_version")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommonVersion extends BaseModel {

    @TableId(type = IdType.AUTO)
    /**
     * 主键
     */
    private Long id;

    /**
     * 主表id
     */
    @TableField("main_id")
    private Long mainId;

    /**
     * 版本
     */
    @TableField("version")
    private String version;

    /**
     * 版本类型（1直接生效，2审批通过生效）
     */
    @TableField("type")
    private Integer type;

    /**
     * 业务模块枚举
     */
    @TableField("module")
    private String module;

    /**
     * 流程实例id
     */
    @TableField("process_instance_id")
    private String processInstanceId;

    @TableField("flow_type")
    private String flowType;

    /**
     * 版本标志，0无效，1有效...业务自扩展
     * {@link VersionTypeConstants}
     */
    @TableField("version_type")
    private Integer versionType;

}