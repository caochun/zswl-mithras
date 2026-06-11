package cn.zswltech.mithras.ftp.newftp.model.config;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 一年期shibor利率配置表
 * @TableName new_ftp_shibor_interest_rate_config
 */
@TableName(value ="new_ftp_shibor_interest_rate_config")
@Data
public class NewFtpShiborInterestRateConfig extends BaseModel implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 日期
     */
    @TableField(value = "date")
    private LocalDate date;

    /**
     * 值
     */
    @TableField(value = "value")
    private Integer value;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}