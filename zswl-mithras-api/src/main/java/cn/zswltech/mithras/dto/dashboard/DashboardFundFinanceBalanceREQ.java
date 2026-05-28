package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/24
 * @description
 */
@Data
@ApiModel(value = "融资余额请求体")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DashboardFundFinanceBalanceREQ extends DashboardFundFinanceBaseREQ {

    @ApiModelProperty("机构名称")
    private String orgName;

    @ApiModelProperty("融资类别")
    private String financingTypeCode;

    @ApiModelProperty("融资编号")
    private String financingCode;

    private List<Long> ids;
}
