package cn.zswltech.mithras.contract.mapper.model.contract;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 合同文本管理表
 *
 * @author bigbear
 * @TableName contract_text_manage
 */
@Data
@TableName(value = "contract_text_manage")
@EqualsAndHashCode(callSuper = true)
public class ContractTextManage extends BaseModelWithLogicDelete implements Serializable {
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联合同id
     */
    @TableField(value = "contract_id")
    private Long contractId;

    /**
     * 签约方式
     */
    @TableField(value = "sign_way")
    private String signWay;

    /**
     * 推送时间
     */
    @TableField(value = "push_time")
    private LocalDateTime pushTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}