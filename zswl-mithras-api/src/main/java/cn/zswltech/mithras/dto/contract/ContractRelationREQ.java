package cn.zswltech.mithras.dto.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description 合同-担保措施
 * @author vico
 * @date 2022-08-12
 */
@Data
@ApiModel("合同编号-担保人关联-请求体")
public class ContractRelationREQ {

    @ApiModelProperty(value = "id")
    private List<Long> clientIds;

    @ApiModelProperty(value = "合同编号关键字")
    private String clientCode;

    @ApiModelProperty(value = "当前合同编号-过滤此编号")
    private Long contractId;

}
