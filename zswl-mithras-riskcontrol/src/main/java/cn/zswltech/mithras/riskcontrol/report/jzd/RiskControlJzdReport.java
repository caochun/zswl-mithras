package cn.zswltech.mithras.riskcontrol.report.jzd;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author yibin
 */
@Data
@TableName("risk_control_jzd_report")
public class RiskControlJzdReport {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("data_month")
    private LocalDate dataMonth;

    @TableField("biz_type")
    private String bizType;

    @TableField("target_subject")
    private String targetSubject;

    @TableField("biz_amount_total")
    private Long bizAmountTotal;

    @TableField("biz_amount_left")
    private Long bizAmountLeft;

    @TableField("client_name")
    private String clientName;

    @TableField("client_same_trade")
    private String clientSameTrade;

    private String economicComposition;

    private String sponsorOrgName;

    private LocalDate bizStartDate;

    private LocalDate bizEndDate;

    private Long ensureValue;

    private String guaranteeName;

    private Long yjtjzValue;

    private Integer overdueDays;

    private Long overdueValue;

    private String assetsCategory;

    private String createType;

    private String reportStatus;

    private Long projReviewId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
