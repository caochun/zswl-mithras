package cn.zswltech.mithras.service.mapper.model.payment.pubInfo;

import cn.zswltech.mithras.common.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 公开信息查询结果表
 *
 * @author bigbear
 * @TableName public_info_query
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "public_info_query")
@EqualsAndHashCode(callSuper = true)
public class PublicInfoQuery extends BaseModelWithLogicDelete implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联付款申请ID
     */
    @TableField(value = "payment_id")
    private Long paymentId;

    /**
     * 关联客户ID
     */
    @TableField(value = "client_id")
    private Long clientId;

    /**
     * 客户类型 {@link cn.zswltech.mithras.service.enums.payment.pubinfo.PublicInfoClientTypeEnum}
     */
    @TableField(value = "client_type")
    private String clientType;

    /**
     * 确认人ID
     */
    @TableField(value = "confirmed_by")
    private Long confirmedBy;

    /**
     * 确认时间
     */
    @TableField(value = "confirmed_time")
    private LocalDateTime confirmedTime;

    /**
     * 流程实例ID，用来做校验使用，流程启动的时候放进去
     */
    @TableField(value = "process_instance_id")
    private String processInstanceId;

    /**
     * 查询开始时间
     */
    @TableField(value = "query_from")
    private LocalDate queryFrom;

    /**
     * 查询结束时间
     */
    @TableField(value = "query_to")
    private LocalDate queryTo;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}