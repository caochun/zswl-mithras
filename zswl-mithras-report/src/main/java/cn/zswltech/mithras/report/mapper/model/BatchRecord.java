package cn.zswltech.mithras.report.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 批次记录
 *
 * @author wangchuanhao
 * @date 2023/1/11 2:27 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class BatchRecord {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;


    /**
     * 批次号
     */
    @TableField("batch_no")
    private String batchNo;

    /**
     * 报送序号 每日重置01开始
     */
    @TableField("batch_seq")
    private Integer batchSeq;

    /**
     * 批次类型，审批批次、全量批次
     */
    @TableField("type")
    private String type;

    /**
     * 报送时间
     */
    @TableField("report_time")
    private LocalDateTime reportTime;

    /**
     * 报送员id
     */
    @TableField("reportor_id")
    private Long reportorId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 流程id
     */
    @TableField("process_instance_id")
    private String processInstanceId;

    /**
     * 报送说明
     */
    @TableField("remark")
    private String remark;

}
