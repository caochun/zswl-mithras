package cn.zswltech.mithras.contract.mapper.model.contract;

import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

import lombok.*;

/**
 * 合同租赁物评估机构关联版本表
 * @author bigbear
 * @TableName contract_evaluation_agency_effect
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName(value ="contract_evaluation_agency_effect")
public class ContractEvaluationAgencyEffect extends BaseModelWithLogicDelete {
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 合同id
     */
    @TableField(value = "contract_id")
    private Long contractId;

    /**
     * 评估机构id
     */
    @TableField(value = "company_id")
    private Long companyId;

    /**
     * 租赁物id
     */
    @TableField(value = "lease_item_id")
    private Long leaseItemId;

    /**
     * 用途
     */
    @TableField(value = "purpose")
    private String purpose;

    /**
     * 是否被选中
     */
    @TableField(value = "select_type")
    private String selectType;
}