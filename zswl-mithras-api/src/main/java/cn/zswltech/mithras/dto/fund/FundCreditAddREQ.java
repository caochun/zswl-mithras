package cn.zswltech.mithras.dto.fund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description fund_credit
 * @date 2022-12-13
 */
@Data
@ApiModel("新增授信-请求体")
public class FundCreditAddREQ {
    @ApiModelProperty(value = "授信机构")
    @NotNull(message = "机构id不能为空")
    private Long organizationId;
    @ApiModelProperty(value = "授信金额")
    @NotNull(message = "授信金额不能为空")
    private Long totalCreditLimit;

    /**
     * 存在老授信时，需要传入新的生效时间
     */
    @ApiModelProperty(value = "生效开始时间")
    private LocalDate effectiveDateFrom;
    @ApiModelProperty(value = "生效结束时间")
    private LocalDate effectiveDateTo;
}
