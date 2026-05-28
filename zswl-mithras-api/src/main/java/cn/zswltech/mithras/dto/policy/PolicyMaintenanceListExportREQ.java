package cn.zswltech.mithras.dto.policy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @create: 2022-08-15
 **/
@Data
@ApiModel("保单台账列表-导出excel-请求体")
public class PolicyMaintenanceListExportREQ extends PolicyMaintenanceREQ {
    @ApiModelProperty("dataSource为1时ID")
    private List<Long> policyIds;
    @ApiModelProperty("dataSource为0时ID")
    private List<Long> paymentPolicyIds;
}
