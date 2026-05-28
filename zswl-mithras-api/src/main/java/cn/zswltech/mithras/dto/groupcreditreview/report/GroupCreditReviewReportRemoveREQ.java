package cn.zswltech.mithras.dto.groupcreditreview.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author luyi
 */
@ApiModel("集团授信评审报告删除删除-请求体")
@Data
public class GroupCreditReviewReportRemoveREQ {

    @NotNull
    @ApiModelProperty(value = "id", required = true)
    private Long id;
}
