package cn.zswltech.mithras.service.mapper.model.fund.financing;

import cn.zswltech.mithras.common.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description 直接融资-费用明细
 * @author zhaozhengkang
 * @date 2023-06-17
 */
@Data
@TableName("fund_financing_fee_detail_lib")
public class FundFinancingFeeDetailLib extends FundFinancingFeeDetail implements ILib {
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
