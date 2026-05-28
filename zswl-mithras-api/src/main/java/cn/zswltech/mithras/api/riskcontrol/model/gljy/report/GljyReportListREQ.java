package cn.zswltech.mithras.api.riskcontrol.model.gljy.report;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author yibin
 */
@Data
@ApiModel("关联交易记录列表-请求体")
public class GljyReportListREQ extends PageReq {

    @ApiModelProperty("交易时间-从")
    private LocalDate tradeDateFrom;
    @ApiModelProperty("交易时间-到")
    private LocalDate tradeDateTo;

    @ApiModelProperty("交易金额-从")
    private Long amountFrom;
    
    @ApiModelProperty("交易金额-到")
    private Long amountTo;

    @ApiModelProperty("关联交易级别")
    private String level;

    @ApiModelProperty("交易对手名称")
    private String tradePartyName;

    @ApiModelProperty("报送状态")
    private String reportStatus;

}
