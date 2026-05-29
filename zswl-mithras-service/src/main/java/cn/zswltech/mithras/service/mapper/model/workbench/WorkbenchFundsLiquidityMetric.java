package cn.zswltech.mithras.service.mapper.model.workbench;

import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 工作台-资金流动性分析指标
 * @author zhaozhengkang
 * @date 2023-05-09
 */
@Data
public class WorkbenchFundsLiquidityMetric extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 月份
     */
    @TableField("month")
    private LocalDate month;

    /**
    * 资金类型 应收租金/实收租金/应还资金/实还资金
    */
    @TableField("fund_type")
    private String fundType;

    /**
    * 值，单位亿元
    */
    @TableField("value")
    private String value;

}
