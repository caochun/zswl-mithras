package cn.zswltech.mithras.dto.finance;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/8/13/16:37
 * @description
 */
@Data
public class FinanceProfitConfirmREQ {

    @ApiModelProperty(value = "项目利润ID列表")
    @NotEmpty(message = "项目利润ID列表不能为空")
    private List<Long> projectProfitIdList;
}
