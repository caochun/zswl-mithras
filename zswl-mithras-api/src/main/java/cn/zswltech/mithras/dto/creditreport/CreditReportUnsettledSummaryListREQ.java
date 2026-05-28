package cn.zswltech.mithras.dto.creditreport;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 征信报告-未结清信贷及授信信息表
 * @author vico
 * @date 2025-11-14
 */
@Data
@ApiModel("征信报告-未结清信贷及授信信息表列表-请求体")
public class CreditReportUnsettledSummaryListREQ extends PageReq {

    /**
     * 征信报告基本表id
     */
    @ApiModelProperty(value = "征信报告基本表id")
    //@NotNull
    private Long creditReportId;

    @ApiModelProperty(value = "征信报告客户表id")
    private Long creditReportClientId;

}
