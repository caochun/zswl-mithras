package cn.zswltech.mithras.dto.creditreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("资料")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditReportFileDTO {

    @ApiModelProperty("资料子类型")
    private String materialsTypeName;
}
