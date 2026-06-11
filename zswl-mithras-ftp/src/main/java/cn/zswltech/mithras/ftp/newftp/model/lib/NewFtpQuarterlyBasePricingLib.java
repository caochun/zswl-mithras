package cn.zswltech.mithras.ftp.newftp.model.lib;

import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.persistence.tag.ILib;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpQuarterlyBasePricingDraft;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 季度指导基础定价版本表
 * @TableName new_ftp_quarterly_base_pricing_lib
 */
@TableName(value ="new_ftp_quarterly_base_pricing_lib")
@Data
public class NewFtpQuarterlyBasePricingLib extends NewFtpQuarterlyBasePricingDraft implements ILib, Serializable {

    /**
     * 变更编号
     * 版本号
     */
    @TableField("version")
    private String version;

    /**
     * 临时数据表id
     * 需要用来比对数据 或者 流程拒绝时全量回写
     */
    @TableField("origin_id")
    private Long originId;

    /**
     * 记录原数据更新时间、创建时间等
     */
    @TableField("data_create_time")
    private LocalDateTime dataCreateTime;
    @TableField("data_create_by")
    private Long dataCreateBy;
    @TableField("data_update_time")
    private LocalDateTime dataUpdateTime;
    @TableField("data_update_by")
    private Long dataUpdateBy;

    /**
     * 版本标志，0无效，1有效...业务自扩展
     * {@link VersionTypeConstants}
     */
    @TableField("version_type")
    private Integer versionType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}