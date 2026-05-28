package cn.zswltech.mithras.dto.margin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2022-08-16
 **/
@Data
@ApiModel("收款核销下拉-请求体")
public class CollectionSelectREQ {

    @ApiModelProperty("合同id")
    private Long contractId;


}
