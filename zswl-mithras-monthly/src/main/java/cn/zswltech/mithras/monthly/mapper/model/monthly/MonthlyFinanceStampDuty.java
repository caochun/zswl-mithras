package cn.zswltech.mithras.monthly.mapper.model;

import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 月结管理资金端印花税
 * @author bigbear
 * @TableName monthly_finance_stamp_duty
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value ="monthly_finance_stamp_duty")
public class MonthlyFinanceStampDuty extends MonthlyManageBaseModel implements Serializable {

    /**
     * 融资id
     */
    @TableField(value = "financing_id")
    private Long financingId;

    /**
     * 融资类型
     */
    @TableField(value = "type")
    private String type;

    /**
     * 融资渠道
     */
    @TableField(value = "organization_name")
    private String organizationName;

    /**
     * 融资编号
     */
    @TableField(value = "financing_code")
    private String financingCode;

    /**
     * 本月计提印花税/元
     */
    @TableField(value = "stamp_duty")
    private Long stampDuty;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}