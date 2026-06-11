package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/18/14:49
 * @description
 */
@Data
public class BankCenterSubTableFinanceListREQ {

    @ApiModelProperty(value = "银行流水id")
    @NotEmpty(message = "银行流水id不能为空")
    private List<Long> bankFlowIds;
}
