package cn.zswltech.mithras.api.riskcontrol.model;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author yibin
 */
@Data
@ApiModel("集中度报送列表-请求体")
public class JzdReportListREQ extends PageReq {

    @ApiModelProperty("数据时点；年月")
    private LocalDate dataMonth;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("业务类型")
    private String bizType;

    @ApiModelProperty("创建类型")
    private String createType;


}
