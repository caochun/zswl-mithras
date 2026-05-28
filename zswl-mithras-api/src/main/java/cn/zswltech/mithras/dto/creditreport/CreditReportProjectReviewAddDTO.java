package cn.zswltech.mithras.dto.creditreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@ApiModel("项目评审征信报告新增自显列表-返回体")
@Accessors(chain = true)
public class CreditReportProjectReviewAddDTO {

    @ApiModelProperty("关联项目id")
    private Long projId;

    @ApiModelProperty("关联项目编号")
    private String projCode;

    @ApiModelProperty("关联项目名称")
    private String projectName;

    @ApiModelProperty("查询版本")
    private String selectVersion;

    @ApiModelProperty("信用报告封装格式")
    private String reportFormat;

    @ApiModelProperty("客户列表")
    private List<CreditReportClientInfo> clientInfos;
}
