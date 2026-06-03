package cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model;
import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 资产减值预测表
 * @author vico
 * @date 2025-10-14
 */
@Data
public class EclExecutePredictBaseInfo extends BaseModelWithLogicDelete implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 预算计划id
    */
    @TableField("budget_plan_id")
    private Long budgetPlanId;

    /**
    * 拨备预测计划名称
    */
    @TableField("budget_plan_name")
    private String budgetPlanName;

    /**
    * 拨备预测日期
    */
    @TableField("predict_data_began")
    private LocalDate predictDataBegan;

    @TableField("predict_data_end")
    private LocalDate predictDataEnd;

    /**
    * 拨备预测来源 0自动创建 1 手工添加
    */
    @TableField("source")
    private Integer source;


}
