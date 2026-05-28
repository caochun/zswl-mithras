package cn.zswltech.mithras.dto.creditreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("征信报告查询-提交-响应体")
public class CreditReportSubmitDTO {

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("资料")
    private List<CreditReportFileDTO> creditReportFiles;
}
