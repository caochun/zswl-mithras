package cn.zswltech.mithras.dto.contractcp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

/**
 * @create: 2022-08-15
 **/

@Data
@ApiModel("合同收付款详情导出-请求体")
public class ContractCollectionPaymentDetailExportREQ {

    @ApiModelProperty("导出的编号列表")
    @NotEmpty
    private Set<String> exportRentCodeList;

}
