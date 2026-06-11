package cn.zswltech.mithras.ftp.newftp.model.lib;

import cn.zswltech.mithras.foundation.persistence.tag.ILib;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpFinancingCostPricingDraft;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 融资成本版本表
 * @TableName new_ftp_financing_cost_pricing_lib
 */
@TableName(value ="new_ftp_financing_cost_pricing_lib")
@Data
public class NewFtpFinancingCostPricingLib extends NewFtpFinancingCostPricingDraft implements ILib, Serializable {

    /**
     * 版本号
     */
    @TableField(value = "version")
    private String version;

    /**
     * 临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写
     */
    @TableField(value = "origin_id")
    private Long originId;

    /**
     * 历史数据创建时间
     */
    @TableField(value = "data_create_time")
    private LocalDateTime dataCreateTime;

    /**
     * 历史数据创建人ID
     */
    @TableField(value = "data_create_by")
    private Long dataCreateBy;

    /**
     * 历史数据更新时间
     */
    @TableField(value = "data_update_time")
    private LocalDateTime dataUpdateTime;

    /**
     * 历史数据更新人ID
     */
    @TableField(value = "data_update_by")
    private Long dataUpdateBy;

    /**
     * 版本标志，0无效，1有效...业务自扩展
     */
    @TableField(value = "version_type")
    private Integer versionType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}