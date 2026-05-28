package cn.zswltech.mithras.report.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import liquibase.pro.packaged.S;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 二代征信修改数据辅助表
 *
 * @author yangxiong
 * @TableName cr_modify_data_snap
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "cr_modify_data_snap")
public class CrModifyDataSnap implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 是否生效，1：生效，0：失效， 默认1
     */
    @TableField(value = "is_take_effect")
    private Integer isTakeEffect;

    /**
     * 是否生效，1：生效，0：失效， 默认1
     */
    @TableField(value = "is_show")
    private Integer isShow;


    /**
     * 修改的哪张表的数据
     */
    @TableField(value = "table_type")
    private String tableType;

    /**
     * 被修改记录业务标识
     */
    @TableField(value = "business_key")
    private String businessKey;

    /**
     * 修改原因
     */
    @TableField(value = "reason")
    private String reason;

    /**
     * 标签，修改类型
     * {@link cn.zswltech.mithras.report.enums.biz.DataShowTypeEnum}
     */
    @TableField(value = "label")
    private String label;

    /**
     * 流程KEY
     */
    @TableField(value = "proc_business_key")
    private Long procBusinessKey;

    /**
     * 批次号
     */
    @TableField(value = "batch_no")
    private String batchNo;

    /**
     * 修改的数据映射
     */
    @TableField(value = "data_map")
    private String dataMap;

    /**
     * 修改之前的全量数据
     */
    @TableField("old_data_map")
    private String oldDataMap;

    /**
     * 创建人id、发起人id
     */
    @TableField(value = "create_by")
    private Long createBy;

    /**
     * 创建时间。默认当前时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 最后更新人id
     */
    @TableField(value = "update_by")
    private Long updateBy;

    /**
     * 更新时间；每次记录变化，自动更新为当前时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除，0：未删除，1：已删除，默认0
     */
    @TableField(value = "deleted")
    private Integer deleted;

    /**
     * 版本号
     */
    @TableField(value = "version")
    private String version;

    /**
     * 版本类型，生效1， 失效0
     */
    @TableField(value = "version_type")
    private Integer versionType;

    /**
     * 审批状态
     */
    @TableField("approval_status")
    private String approvalStatus;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}