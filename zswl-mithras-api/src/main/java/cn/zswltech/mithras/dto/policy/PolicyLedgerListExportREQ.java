package cn.zswltech.mithras.dto.policy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @create: 2022-08-15
 **/
@Data
@ApiModel("保单台账列表-导出excel-请求体")
public class PolicyLedgerListExportREQ extends PolicyLedgerListREQ {

    @ApiModelProperty("需要导出的数据id列表-dataSource=policy")
    private List<Long> policyExportIds;

    @ApiModelProperty("需要导出的数据id列表-dataSource=payment")
    private List<Long> paymentExportIds;
}
