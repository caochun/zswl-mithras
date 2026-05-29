package cn.zswltech.mithras.service.service.newftp.model.config;

import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 十年期国债收益率配置表
 * @TableName new_ftp_treasury_bond_yield_config
 */
@TableName(value ="new_ftp_treasury_bond_yield_config")
@Data
public class NewFtpTreasuryBondYieldConfig extends BaseModel implements Serializable {
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