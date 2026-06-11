package cn.zswltech.mithras.dto.capital.write_off;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author bigbear
 * @date 2024/9/19 19:22
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "删除流水请求参数")
public class DeleteFlowREQ extends WriteOffBaseREQ{

    @ApiModelProperty(value = "流水id列表")
    private List<Long> flowIdList;

    @ApiModelProperty(value = "当前的tab的ID")
    @NotNull(message = "当前的tab的financeFlowTabMainInfoId不能为空")
    private Long financeFlowTabMainInfoId;
}
