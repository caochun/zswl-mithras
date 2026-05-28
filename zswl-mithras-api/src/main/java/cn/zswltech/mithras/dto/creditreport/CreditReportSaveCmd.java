package cn.zswltech.mithras.dto.creditreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;


@Data
@ApiModel("征信报告查询-保存-请求体")
public class CreditReportSaveCmd {

    @ApiModelProperty("征信报告id")
    @NotNull(message = "征信报告id不能为空")
    private Long id;

    @ApiModelProperty("征信报告查询客户信息列表")
    @NotNull(message = "客户信息不能为空")
    private List<CreditReportClientInfo> clientInfos;

    @ApiModelProperty("关联项目id")
    private Long projId;

    @ApiModelProperty("关联项目名称")
    private String projName;

    @ApiModelProperty("关联项目编号")
    private String projCode;

    @ApiModelProperty("查询版本")
    private String selectVersion;

    @ApiModelProperty("信用报告封装格式")
    private String reportFormat;

    /**
     * 授信开始时间
     */
    @ApiModelProperty("授信开始时间")
    private LocalDate authorizationBeganDate;

}
