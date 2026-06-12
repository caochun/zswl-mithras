package cn.zswltech.mithras.fund.persistence.model.financial;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author chenyifei
 * @since 2024-11-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("fund_financial_system_call_record")
public class FundFinancialSystemCallRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 数据时点
     */
    @TableField("date")
    private LocalDateTime date;

    /**
     * 批次号
     */
    @TableField("batch_number")
    private String batchNumber;

    /**
     * 操作状态
     */
    @TableField("status")
    private String status;

    /**
     * 推送类型
     */
    @TableField("type")
    private String type;

    /**
     * 推送数据数量
     */
    @TableField("count")
    private Integer count;

    /**
     * 请求参数
     */
    @TableField("query")
    private String query;

    /**
     * 返回体
     */
    @TableField("result")
    private String result;

    /**
     * 系统内校验的错误信息
     */
    @TableField("system_error_info")
    private String systemErrorInfo;


    /**
     * 创建人、发起人
     */
    @TableField("create_by")
    private Long createBy;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 最后更新人id
     */
    @TableField("update_by")
    private Long updateBy;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除，0-未删除，1-已删除
     */
    @TableField("deleted")
    private Integer deleted;


}
