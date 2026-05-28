package cn.zswltech.mithras.dto.liquiditymanage.financingRepay;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * AccountBalanceListREQ
 *
 * @author zhouning
 * @since 2024/12/16
 */
@Data
@ApiModel(value = "还款计划确认参数")
public class FinancingRepayPlanModifyREQ {

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 融资id
     */
    @ApiModelProperty(value = "融资id")
    private Long financingId;

    /**
     * 实际还款表id
     */
    @ApiModelProperty(value = "实际还款表id")
    private Long financingRepayActualId;

    /**
     * 融资类型：直融/间融
     */
    @ApiModelProperty("融资类型：直融/间融")
    private String financingType;

    @ApiModelProperty(value = "确认状态是否已确认")
    private Integer isConfirmed;

}
