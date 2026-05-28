package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/14
 * @description
 */
@Data
@ApiModel("租后管理-租后检查计划检查项目（更改为客户）列表-请求体")
public class AfterLeaseCheckClientListREQ {
    @ApiModelProperty("租后检查计划id")
    @NotNull(message = "租后检查计划id")
    private Long planId;

    @ApiModelProperty("客户id列表")
    private List<Long> clientIdList;

//    @ApiModelProperty("租后检查项目id列表")
//    private List<Long> projectIdList;
}
