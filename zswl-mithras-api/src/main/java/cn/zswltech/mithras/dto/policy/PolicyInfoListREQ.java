package cn.zswltech.mithras.dto.policy;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("保单维护列表-请求体")
public class PolicyInfoListREQ extends PageReq {

    @ApiModelProperty("客户id")
    private Long policyId;

    private Long paymentId;

}
