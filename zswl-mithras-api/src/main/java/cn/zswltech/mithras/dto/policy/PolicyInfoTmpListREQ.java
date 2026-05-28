package cn.zswltech.mithras.dto.policy;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 保单暂存表c
 * @author vico
 * @date 2023-10-23
 */
@Data
@ApiModel("保单暂存表c列表-请求体")
public class PolicyInfoTmpListREQ extends PageReq {

    @NotNull
    private Long contractId;

    @ApiModelProperty(value = "已到期或5天内将到期标识")
    private Boolean adventFlag;
}
