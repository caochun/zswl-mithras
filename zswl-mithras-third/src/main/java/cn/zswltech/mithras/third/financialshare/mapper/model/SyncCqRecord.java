package cn.zswltech.mithras.third.financialshare.mapper.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author yibin
 */
@Accessors(chain = true)
@Data
@TableName("sync_cq_record")
public class SyncCqRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    @TableField("record_id")
    private String recordId;

    @TableField("contract_code")
    private String contractCode;

    //来源系统单号，租金为去除后四位现金流编号
    @TableField("sourcebillno")
    private String sourcebillno;

    @TableField("rent_actual_code")
    private String rentActualCode;

    //应收金额
    @TableField("lease_rate")
    private String leaseRate;

    //是否开票
    @TableField("cico_isinvoice")
    private String cico_isinvoice;

    //日期
    @TableField("date")
    private String date;

    //期项
    @TableField("phase")
    private Integer phase;

    //租金
    @TableField("rent")
    private String rent;

    //本金
    @TableField("principal")
    private String principal;

    //利息
    @TableField("interest")
    private String interest;

    //剩余本金
    @TableField("lastAmount")
    private String lastAmount;

    /**
     * 变更类型
     * LPR调整:A
     * 提前还款:B
     * 调整还款计划:C
     * 展期:D
     * E 提前结清
     * F 提前结清抵扣
     * G 抵扣
     **/
    @TableField("change_state")
    private String changeState;

    @TableField(value = "create_time", updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createTime;

    @TableField(value = "update_time", updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime updateTime;

    public SyncCqRecord() {
    }

}
