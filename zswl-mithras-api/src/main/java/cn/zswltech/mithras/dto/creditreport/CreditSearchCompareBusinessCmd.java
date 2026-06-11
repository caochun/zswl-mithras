package cn.zswltech.mithras.dto.creditreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author: jackerhe
 * @date: 2023/9/7 10:48 上午
 **/
@Data
@ApiModel("征信查询-征信查询比对承租人工商信息-请求参数")
public class CreditSearchCompareBusinessCmd {

    @ApiModelProperty("征信查询id")
    private Long creditSearchId;

    private String flowId;

}
