package cn.zswltech.mithras.contract.mapper.model.contract;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2022/8/12
 * @description 合同明细-质押物清单
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("contract_pledge_item")
public class ContractPledgeItem extends BaseModel implements IEntity {
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
     * 质押措施id
     */
    @TableField("pledge_id")
    private Long pledgeId;

    /**
     * 序号
     */
    @TableField("sequence")
    private Integer sequence;

    /**
     * 种类
     */
    @TableField("category")
    private String category;

    /**
     * 评估价值
     */
    @TableField("assessed_value")
    private Long assessedValue;

    @Override
    public void setMainId(Long id) {
        this.contractId = id;
    }

    @Override
    public Long getMainId() {
        return contractId;
    }
}
