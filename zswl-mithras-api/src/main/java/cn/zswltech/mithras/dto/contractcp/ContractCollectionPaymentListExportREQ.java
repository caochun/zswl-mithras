package cn.zswltech.mithras.dto.contractcp;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

/**
 * @create: 2022-08-15
 **/

@Data
@ApiModel("合同收付款列表导出-请求体")
public class ContractCollectionPaymentListExportREQ {

    @ApiModelProperty("需要导出的数据id列表")
    @NotEmpty
    private List<Long> exportIdList;

}
