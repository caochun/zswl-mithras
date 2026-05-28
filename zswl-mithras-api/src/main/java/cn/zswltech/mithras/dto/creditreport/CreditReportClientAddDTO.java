package cn.zswltech.mithras.dto.creditreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@ApiModel("客户管理征信报告新增自显列表-返回体")
@Accessors(chain = true)
public class CreditReportClientAddDTO {

    @ApiModelProperty("客户编号")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("查询版本")
    private String selectVersion;

    @ApiModelProperty("信用报告封装格式")
    private String reportFormat;

    @ApiModelProperty("统一社会信用代码")
    private String cscCode;

    @ApiModelProperty("中征码")
    private String zhongZhengCode;

    @ApiModelProperty("关联项目列表")
    private List<CreditReportProjectInfo> projectInfos;
}
