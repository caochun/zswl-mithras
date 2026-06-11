package cn.zswltech.mithras.contract.model.contract;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.foundation.persistence.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 合同-质押措施
 * @author vico
 * @date 2022-08-12
 */
@Data
public class ContractPledge extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 质押合同编号
     */
    @TableField("pledge_contract_code")
    private String pledgeContractCode;

    /**
     * 关联合同code
     */
    @TableField("relat_contracts")
    @IncludeNull
    private String relatContracts;

    /**
     * 所属合同id
     */
    @TableField("contract_id")
    private Long contractId;

    /**
     * 质押合同类型
     */
    @TableField("contract_pledge_type")
    private String contractPledgeType;

    /**
     * 质押人 类型
     */
    @TableField("pledge_Type")
    private String pledgeType;

    /**
    * 质押人id 类型
    */
    @TableField("pledge_ids")
    private String pledgeIds;

    /**
    * 质押物描述
    */
    @TableField("pledge_describe")
    private String pledgeDescribe;

    /**
     * 是否最高额担保，0-否，1-是
     */
    @TableField("highest")
    private Integer highest;

    @Override
    public void setMainId(Long id) {
        this.contractId = id;
    }

    @Override
    public Long getMainId() {
        return contractId;
    }

}
