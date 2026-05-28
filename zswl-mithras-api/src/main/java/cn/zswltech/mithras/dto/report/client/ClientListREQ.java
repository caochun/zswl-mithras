package cn.zswltech.mithras.dto.report.client;

import cn.zswltech.mithras.dto.report.ReportListBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 征信报送-客户表查询入参
 *
 * @author wangchuanhao
 * @date 2023/1/11 11:12 AM
 */
@Data
@ApiModel("征信报送-客户表查询入参")
public class ClientListREQ extends ReportListBaseREQ {

    @ApiModelProperty("客户名称")
    private String clientName;
}
