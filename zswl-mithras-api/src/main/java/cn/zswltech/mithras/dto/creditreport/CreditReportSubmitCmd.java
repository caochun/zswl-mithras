package cn.zswltech.mithras.dto.creditreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("征信报告查询-保存-请求体")
public class CreditReportSubmitCmd {

    @ApiModelProperty("征信报告id")
    private Long id;

    @ApiModelProperty("客户id")
    private List<Long> clientId;
}
