package cn.zswltech.mithras.dto.contractcp;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2022-08-15
 **/

@Data
@ApiModel("合同收付款详情-请求体")
public class ContractCollectionPaymentDetailREQ extends PageReq {

    @ApiModelProperty("合同Id")
    private Long contractId;

    @ApiModelProperty("分类")
    private String cashtype;


}
