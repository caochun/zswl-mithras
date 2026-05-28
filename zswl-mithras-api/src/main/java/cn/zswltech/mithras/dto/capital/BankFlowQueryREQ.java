package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/7/17/10:50
 * @description
 */
@Data
public class BankFlowQueryREQ {

    @ApiModelProperty(value = "银行流水id列表")
    @NotEmpty(message = "银行流水id列表不能为空")
    private List<Long> bankFlowIds;
}
