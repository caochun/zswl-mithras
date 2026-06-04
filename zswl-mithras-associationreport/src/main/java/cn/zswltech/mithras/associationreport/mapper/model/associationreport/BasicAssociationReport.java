package cn.zswltech.mithras.associationreport.mapper.model;

import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2025/4/18
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class BasicAssociationReport extends BaseModelWithLogicDelete {
    /**
     * 行号
     */
    @TableField("row_num")
    private Integer rowNum;

    /**
     * 报表实例编号
     */
    @TableField("report_instance_id")
    private String reportInstanceId;

    /**
     * 报表实例周期
     */
    @TableField("report_instance_period")
    private String reportInstancePeriod;

    /**
     * 批次号
     */
    @TableField("batch_no")
    private String batchNo;

    /**
     * 版本号
     */
    @TableField("version")
    private String version;

    /**
     * 操作标识
     */
    @TableField("op")
    private String op;

    /**
     * 上报时间
     */
    @TableField("report_time")
    private LocalDateTime reportTime;

    /**
     * 写入数据库时间
     */
    @TableField("write_time")
    private LocalDateTime writeTime;

    /**
     * 企业统一社会信用代码
     */
    @TableField("unif_soci_cred_code")
    private String unifSociCredCode;
}
