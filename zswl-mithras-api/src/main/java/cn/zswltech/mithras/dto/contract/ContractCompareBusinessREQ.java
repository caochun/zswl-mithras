package cn.zswltech.mithras.dto.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 *
 * @author: jackerhe
 * @date: 2023/9/7 10:48 上午
 **/
@Data
@ApiModel("合同管理-合同比对承租人及担保人工商信息-请求体")
public class ContractCompareBusinessREQ {

    @ApiModelProperty(value = "合同id")
    @NotNull
    private Long contractId;

    private String flowId;

}
