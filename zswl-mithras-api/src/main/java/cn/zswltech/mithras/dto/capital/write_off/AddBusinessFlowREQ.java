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
 * @date 2024/9/19 19:30
 * @description
 */
@Data
@ApiModel(value = "增加流水请求参数")
@EqualsAndHashCode(callSuper = true)
public class AddBusinessFlowREQ extends WriteOffBaseREQ {

    @ApiModelProperty(value = "当前tab的ID")
    @NotNull(message = "当前tab的ID不能为空")
    private Long financeFlowTabMainInfoId;

    @ApiModelProperty(value = "应收/应付账单ID列表")
    @NotEmpty(message = "应收/应付账单ID列表不能为空")
    private List<Long> collectionIds;

}
