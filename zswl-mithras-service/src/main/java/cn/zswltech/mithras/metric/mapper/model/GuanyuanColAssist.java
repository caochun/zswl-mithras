package cn.zswltech.mithras.metric.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author yibin
 */
@Data
@TableName("guanyuan_col_assist")
public class GuanyuanColAssist {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("col_name")
    private String colName;
    @TableField("sort")
    private Integer sort;
    @TableField("month")
    private String month;
    @TableField("report_name")
    private String reportName;
}
