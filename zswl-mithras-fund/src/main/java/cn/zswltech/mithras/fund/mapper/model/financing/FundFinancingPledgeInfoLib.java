package cn.zswltech.mithras.fund.mapper.model.financing;

import cn.zswltech.mithras.foundation.persistence.tag.ILib;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("fund_financing_pledge_info_lib")
public class FundFinancingPledgeInfoLib extends FundFinancingPledgeInfo implements ILib {
    /**
     * 版本号
     */
    @TableField("version")
    private String version;
    /**
     * 临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写
     */
    @TableField("origin_id")
    private Long originId;
    /**
     * 
     */
    @TableField("data_create_time")
    private LocalDateTime dataCreateTime;
    /**
     * 
     */
    @TableField("data_create_by")
    private Long dataCreateBy;
    /**
     * 
     */
    @TableField("data_update_time")
    private LocalDateTime dataUpdateTime;
    /**
     * 
     */
    @TableField("data_update_by")
    private Long dataUpdateBy;
    /**
     * 版本标志，0无效，1有效...业务自扩展
     */
    @TableField("version_type")
    private Integer versionType;
}