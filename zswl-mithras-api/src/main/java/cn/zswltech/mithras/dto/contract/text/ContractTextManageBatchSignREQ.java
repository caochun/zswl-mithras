package cn.zswltech.mithras.dto.contract.text;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author bigbear
 * @date 2024/12/2 11:01
 * @description
 */
@Data
@ApiModel(value = "合同文本管理-批量签约请求参数")
public class ContractTextManageBatchSignREQ {

    @ApiModelProperty(value = "相关材料ID集合")
    @NotNull(message = "相关材料ID集合不能为空")
    private List<Long> idList;

}
