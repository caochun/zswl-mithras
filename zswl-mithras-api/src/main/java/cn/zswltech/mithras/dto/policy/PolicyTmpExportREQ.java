package cn.zswltech.mithras.dto.policy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("保单台账-合同保单请求体")
public class PolicyTmpExportREQ {

    @ApiModelProperty("ids")
    private List<Long> ids;
}
