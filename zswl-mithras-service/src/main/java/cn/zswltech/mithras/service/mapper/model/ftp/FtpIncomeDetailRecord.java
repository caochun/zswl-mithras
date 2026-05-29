package cn.zswltech.mithras.service.mapper.model.ftp;

import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @description 资金管理-融资管理-ftp收益记录表
 * @author vico
 * @date 2025-07-15
 */
@Data
public class FtpIncomeDetailRecord extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * ftp收益表id
    */
    @TableField("ftp_income_id")
    private Long ftpIncomeId;

    /**
    * 计息日期
    */
    @TableField("interest_date")
    private LocalDate interestDate;

    /**
    * 剩余本金
    */
    @TableField("remaining_principal")
    private Long remainingPrincipal;

    /**
    * ftp收益率
    */
    @TableField("ftp_yield_rate")
    private Integer ftpYieldRate;

    /**
    * ftp日收益率 ftp收益率（%）/360
     * 显示两位小数，百分号
    */
    @TableField("ftp_yield_rate_day")
    private BigDecimal ftpYieldRateDay;

    /**
    * ftp收益 本日剩余本金（元）*FTP收益日利率（%）
     * 显示两位小数，千分位符
    */
    @TableField("ftp_income")
    private Long ftpIncome;

    /**
    * ftp收益
    */
    @TableField("ftp_income_current_year")
    private Long ftpIncomeCurrentYear;

}
