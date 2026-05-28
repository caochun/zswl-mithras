package cn.zswltech.mithras.dto.creditreport;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("客户管理-征信报告查询列表-请求参数")
public class CreditSearchClientQuery extends PageReq {

    @ApiModelProperty("客户id")
    private Long clientId;

}
