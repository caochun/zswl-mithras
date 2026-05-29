package cn.zswltech.mithras.service.service.newftp.model.draft;

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
 * @date 2025/7/16
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("new_ftp_quarterly_base_pricing_ext_draft")
public class NewFtpQuarterlyBasePricingExtDraft extends BaseModel implements IEntity, Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属的主数据id
     */
    @TableField(value = "ftp_id")
    private Long ftpId;

    /**
     * 3年内（含）
     */
    @TableField(value = "three_year")
    private Integer threeYear;

    /**
     * 3-5年（含）
     */
    @TableField(value = "three_to_five_year")
    private Integer threeToFiveYear;

    /**
     * 5年以上
     */
    @TableField(value = "more_than_five_year")
    private Integer moreThanFiveYear;

    @Override
    public void setMainId(Long id) {
        this.ftpId = id;
    }

    @Override
    public Long getMainId() {
        return ftpId;
    }
}
