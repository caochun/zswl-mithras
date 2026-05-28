package cn.zswltech.mithras.dto.report.overduerecord;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 征信报送-逾期表编辑入参
 *
 * @author wangchuanhao
 * @date 2023/1/11 11:12 AM
 */
@Data
@ApiModel("征信报送-逾期表编辑入参")
public class OverdueRecordModifyREQ {

    @ApiModelProperty("id")
    @NotNull
    private Long id;

    @NotNull(message = "businessKey不能为空")
    @ApiModelProperty(value = "业务标识")
    private String businessKey;

    @NotNull(message = "修改原因不能为空")
    @ApiModelProperty("修改原因")
    private String reason;

    @ApiModelProperty("逾期本金")
    private Long overduePrincipal;

    @ApiModelProperty("逾期天数")
    private Integer overdueDay;

    @ApiModelProperty("逾期总额")
    private Long overdueTotal;

    @ApiModelProperty("逾期改变日期")
    private LocalDate overdueChangeDate;

}
