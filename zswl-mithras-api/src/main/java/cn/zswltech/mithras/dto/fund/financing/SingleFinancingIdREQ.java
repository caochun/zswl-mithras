package cn.zswltech.mithras.dto.fund.financing;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/2/20
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SingleFinancingIdREQ extends VersionBaseREQ {
    @ApiModelProperty("融资id")
    @NotNull(message = "融资id不能为空")
    private Long financingId;
}
