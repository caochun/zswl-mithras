package cn.zswltech.mithras.dto.basedata;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2023/2/22
 * @description
 */
@Data
@ApiModel("基础数据-已方账户-列表-请求体")
public class ContractAccountPayListREQ {

    @ApiModelProperty("项目编号")
    private String projCode;

}
