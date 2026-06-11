package cn.zswltech.mithras.contract.mapper.model.contract;

import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractTextTypeEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2024/7/29
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("contract_text_info")
public class ContractTextInfo extends BaseModel implements IEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 合同id
     */
    @TableField(value = "contract_id")
    private Long contractId;

    /**
     * 合同文本类型 {@link ContractTextTypeEnum#name()}
     * 多个用英文逗号分开
     */
    @TableField(value = "text_type")
    private String textType;

    /**
     * 是否确认 {@link YesOrNoNumberEnum#getCode()}
     */
    @TableField(value = "is_confirmed")
    private Integer isConfirmed;

    @Override
    public void setMainId(Long id) {
        this.contractId = id;
    }

    @Override
    public Long getMainId() {
        return this.contractId;
    }
}
