package cn.zswltech.mithras.liquiditymanage.service.cal.bo;

import cn.zswltech.mithras.liquiditymanage.mapper.model.AccountBalanceBaseInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * LiquidityIndexCalculatorBo
 *
 * @author chenyifei
 * @since 2024/12/18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiquidityIndexCalculatorBo extends LiquidityBaseCalculatorBo{

    /**
     * 预测区间-开始
     */
    private LocalDate queryDateStart;

    /**
     * 预测区间-结束
     */
    private LocalDate queryDateEnd;

}
