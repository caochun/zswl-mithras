package cn.zswltech.mithras.report.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 征信报送-账户表报送辅助
 * 同借据多付款申请 只报送一个
 * @author wangchuanhao
 * @date 2023/4/6 3:35 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class AccountReportRecord {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 合同id
     */
    @TableField("contract_id")
    private Long contractId;

    /**
     * 借据id
     */
    @TableField("receipt_id")
    private Long receiptId;

    /**
     * 付款id
     */
    @TableField("payment_id")
    private Long paymentId;

}
