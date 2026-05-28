package cn.zswltech.mithras.dto.afterlease;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/11/18 11:19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("外部查询详情-请求体")
public class AfterLeaseCheckExternalQueryIdReq extends VersionBaseREQ {
    @ApiModelProperty("查询id")
    @NotNull
    private Long id;
}
