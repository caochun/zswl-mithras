package cn.zswltech.mithras.dto.groupcreditreview.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author luyi
 */
@Data
@ApiModel("集团授信评审资料清单下载-请求体")
public class GroupCreditReviewReportDownloadREQ {
    @NotNull
    @ApiModelProperty("记录id")
    private Long id;
}
