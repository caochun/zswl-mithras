package cn.zswltech.mithras.dto.dashboard;

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
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DashboardFundFinanceCreditInfoREQ extends DashboardFundFinanceBaseREQ {
    @ApiModelProperty("机构名称")
    private String orgName;
    @ApiModelProperty("授信编号")
    private String creditCode;
    @ApiModelProperty("授信产品")
    private String financingBizType;
    private List<Long> ids;
}
