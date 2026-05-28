package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/18/14:49
 * @description
 */
@Data
public class BankCenterSubTableProjectListREQ {

    @ApiModelProperty(value = "流水ID列表")
    @NotEmpty(message = "流水ID列表不能为空")
    private List<Long> financingFlowIdList;
}
