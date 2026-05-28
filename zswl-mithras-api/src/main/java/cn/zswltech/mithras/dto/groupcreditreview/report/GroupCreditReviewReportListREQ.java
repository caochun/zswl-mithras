package cn.zswltech.mithras.dto.groupcreditreview.report;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 立项报告文件列表-请求体
 *
 * @author wangchuanhao
 * @date 2022/7/22 10:43 AM
 */
@Data
@ApiModel("集团授信评审报告文件列表-请求体")
public class GroupCreditReviewReportListREQ extends PageReq {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
