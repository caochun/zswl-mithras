package cn.zswltech.mithras.contract.mapper.model.contract;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author luyujie
 * @date 2025/12/25
 * @description 合同明细-租赁物名称
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("contract_lease_item_name")
public class ContractLeaseItemName extends BaseModel implements IEntity {
    /**
     * 主键id
     */
    @TableField("id")
    private Long id;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    @Override
    public void setMainId(Long id) {

    }

    @Override
    public Long getMainId() {
        return 0L;
    }
}
