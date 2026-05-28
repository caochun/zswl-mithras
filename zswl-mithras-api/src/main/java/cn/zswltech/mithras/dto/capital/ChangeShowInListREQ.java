package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/29/17:16
 * @description
 */
@Data
public class ChangeShowInListREQ {

    @NotEmpty(message = "流水ID列表不能为空")
    @ApiModelProperty(value = "流水ID列表")
    private List<Long> financingFlowIdList;
}
