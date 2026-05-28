package cn.zswltech.mithras.dto.policy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description 保单暂存表c
 * @author vico
 * @date 2023-10-23
 */
@Data
@ApiModel("保单暂存表c删除-请求体")
public class PolicyInfoTmpRemoveREQ {

    @NotNull
    @ApiModelProperty("ids")
    private List<Long> ids;

}
