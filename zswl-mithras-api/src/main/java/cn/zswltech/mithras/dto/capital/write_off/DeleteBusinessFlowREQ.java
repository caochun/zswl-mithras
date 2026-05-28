package cn.zswltech.mithras.dto.capital.write_off;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author bigbear
 * @date 2024/9/19 19:54
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "删除流水请求参数")
public class DeleteBusinessFlowREQ extends WriteOffBaseREQ{

    @ApiModelProperty(value = "业务流水ID")
    @NotEmpty(message = "业务流水ID不能为空")
    private List<Long> businessFlowIdList;

    @ApiModelProperty(value = "当前tab的ID")
    @NotNull(message = "当前tab的ID不能为空")
    private Long financeFlowTabMainInfoId;
}
