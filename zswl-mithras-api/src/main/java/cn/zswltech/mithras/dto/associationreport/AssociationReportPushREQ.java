package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class AssociationReportPushREQ {


    @ApiModelProperty("上报的ids")
    @Valid
    @NotEmpty(message = "上报列表不能为空")
    private List<Long> ids;

}
