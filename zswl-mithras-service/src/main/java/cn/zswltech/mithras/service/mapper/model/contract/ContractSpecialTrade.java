package cn.zswltech.mithras.service.mapper.model.contract;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 合同特定交易表（用于辅助征信报送）
 *
 * @author wangchuanhao
 * @date 2022/10/9 4:32 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContractSpecialTrade {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 合同id
     */
    @TableField("contract_id")
    private Long contractId;

    /**
     * 付款申请id
     */
    @TableField("payment_id")
    private Long paymentId;

    /**
     * 付款申请编号
     */
    @TableField("payment_code")
    private String paymentCode;

    /**
     * 类型：展期、提前结清
     */
    @TableField("type")
    private String type;

    /**
     * 交易金额
     */
    @TableField("trade_amount")
    private Long tradeAmount;

    /**
     * 交易日期
     */
    @TableField("trade_date")
    private LocalDate tradeDate;

    /**
     * 到期日变更月数
     */
    @TableField("change_month_count")
    private Integer changeMonthCount;

    /**
     *
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     *
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

}
