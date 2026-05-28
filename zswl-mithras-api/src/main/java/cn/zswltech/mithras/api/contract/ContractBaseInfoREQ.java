package cn.zswltech.mithras.api.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
@ApiModel("合同基本信息查询-请求体")
public class ContractBaseInfoREQ {
    @ApiModelProperty(value = "客户Id")
    private Long clientId;
}
