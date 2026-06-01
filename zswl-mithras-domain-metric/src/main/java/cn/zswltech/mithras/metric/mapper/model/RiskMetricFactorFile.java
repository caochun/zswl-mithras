package cn.zswltech.mithras.metric.mapper.model;

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
@TableName("risk_metric_factor_file")
public class RiskMetricFactorFile {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("sheet_name")
    private String sheetName;

    @TableField("sheet_date")
    private LocalDate sheetDate;

    @TableField("file_id")
    private Long fileId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
