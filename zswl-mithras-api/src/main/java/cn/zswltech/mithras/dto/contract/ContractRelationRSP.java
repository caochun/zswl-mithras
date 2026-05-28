package cn.zswltech.mithras.dto.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 合同-担保措施
 * @author vico
 * @date 2022-08-12
 */
@Data
@ApiModel("合同-担保措施列表-返回体")
public class ContractRelationRSP {


    /**
     * 所属合同编号
     */
    @ApiModelProperty(value = "所属合同编号")
    private String contractCode;

}
