package cn.zswltech.mithras.fund.persistence.model.receiptrepay;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import lombok.NoArgsConstructor;

/**
 * @description fund_receipt_repay_batch
 * @author zhaozhengkang
 * @date 2023-02-20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FundReceiptRepayBatch extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 流程id
    */
    @TableField("process_state")
    private String processState;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

}
