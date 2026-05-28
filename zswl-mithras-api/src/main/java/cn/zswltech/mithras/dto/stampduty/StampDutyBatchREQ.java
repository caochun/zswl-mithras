package cn.zswltech.mithras.dto.stampduty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;


@ApiModel("印花税管报批量删除/导出-请求体")
@Data
public class StampDutyBatchREQ {
    @NotNull
    @ApiModelProperty(value = "id", required = true)
    private List<Long> ids;
}
