package cn.zswltech.mithras.service.mapper.model.monthly;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 月结管理项目端印花税
 * @author bigbear
 * @TableName monthly_proj_stamp_duty
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value ="monthly_proj_stamp_duty")
public class MonthlyProjStampDuty extends MonthlyManageBaseModel implements Serializable {

    /**
     * 客户名称
     */
    @TableField(value = "client_name")
    private String clientName;

    /**
     * 客户ID
     */
    @TableField(value = "client_id")
    private Long clientId;

    /**
     * 合同ID
     */
    @TableField(value = "contract_id")
    private Long contractId;

    /**
     * 合同编号
     */
    @TableField(value = "contract_code")
    private String contractCode;

    /**
     * 本月计提印花税
     */
    @TableField(value = "stamp_duty")
    private Long stampDuty;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}