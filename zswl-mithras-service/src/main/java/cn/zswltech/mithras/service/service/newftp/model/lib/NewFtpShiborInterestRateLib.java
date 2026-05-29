package cn.zswltech.mithras.service.service.newftp.model.lib;

import cn.zswltech.mithras.common.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import cn.zswltech.mithras.service.service.newftp.model.draft.NewFtpShiborInterestRateDraft;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 一年期shibor利率版本表
 * @TableName new_ftp_shibor_interest_rate_lib
 */
@TableName(value ="new_ftp_shibor_interest_rate_lib")
@Data
public class NewFtpShiborInterestRateLib extends NewFtpShiborInterestRateDraft implements ILib, Serializable {

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