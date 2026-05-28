package cn.zswltech.mithras.dto.creditreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@ApiModel("征信报告查询-客户信息-保存")
public class CreditReportClientInfo {

    @ApiModelProperty("征信报告详情id")
    private Long id;

    /**
     * 征信报告基本信息表id
     */
    @ApiModelProperty("credit_report_base_info_id")
    private Long creditReportBaseInfoId;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("统一社会信用代码")
    private String cscCode;

    @ApiModelProperty("中征码")
    private String zhongZhengCode;

    @ApiModelProperty("查询目的")
    private String selectGoal;

    @ApiModelProperty("征信报告id")
    private Long reportId;
}
