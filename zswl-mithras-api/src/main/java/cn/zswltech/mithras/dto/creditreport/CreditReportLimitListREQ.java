package cn.zswltech.mithras.dto.creditreport;
import lombok.Data;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @description 征信报告-信用额度表
 * @author vico
 * @date 2025-11-14
 */
@Data
@ApiModel("征信报告-信用额度表列表-请求体")
public class CreditReportLimitListREQ extends PageReq {
    @ApiModelProperty(value = "征信报告基本表id")
    //@NotNull
    private Long creditReportId;

    @ApiModelProperty(value = "征信报告客户表id")
    private Long creditReportClientId;
}
