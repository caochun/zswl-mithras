package cn.zswltech.mithras.service.mapper.model.contract;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/8/12
 * @description 合同退抵信息
 */
@Data
@NoArgsConstructor
@TableName("contract_retreat_info")
public class ContractRetreatInfo extends BaseModel implements IEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /*保证金余额*/
    @TableField(exist = false)
    private Long collectionAmount;

    /**
     * 关联合同id
     */
    @TableField("contract_id")
    private String contractId;

    /**
     * 关联合同编号
     */
    @TableField("contract_code")
    private String contractCode;

    /**
     * 客户名称
     */
    @TableField("client_name")
    private String clientName;

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    /**
     * 流程状态
     */
    @TableField("process_status")
    private String processStatus;

    public Long getDeductionAmount() {
        return deductionAmount == null ? 0L : deductionAmount;
    }

    /**
     * 保证金内扣金额
     */
    @TableField("deduction_amount")
    private Long deductionAmount;

    public Long getReturnedAmount() {
        return returnedAmount == null ? 0L : returnedAmount;
    }

    /**
     * 保证金退还金额
     */
    @TableField("returned_amount")
    private Long returnedAmount;

    /**
     * 是否回收保证金
     */
    @TableField("recycling_flag")
    private String recyclingFlag;

    /**
     * 已推送通知标记
     */
    @TableField("sended_flag")
    private String sendedFlag;



    @Override
    public void setMainId(Long id) {
        this.id = id;
    }

    @Override
    public Long getMainId() {
        return id;
    }
}
