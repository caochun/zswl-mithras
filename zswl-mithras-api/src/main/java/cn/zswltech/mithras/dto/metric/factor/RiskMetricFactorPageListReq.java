package cn.zswltech.mithras.dto.metric.factor;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2023/8/16
 * @description
 */
@ApiModel("财务报表-明细列表-请求参数")
@EqualsAndHashCode(callSuper = true)
@Data
public class RiskMetricFactorPageListReq extends PageReq {
    @NotBlank(message = "表名不能为空")
    @ApiModelProperty("表名")
    private String factorTable;

    @NotNull(message = "日期不能为空")
    @ApiModelProperty("日期")
    private LocalDate factorDate;

    @ApiModelProperty("名称")
    private String factorName;
}
