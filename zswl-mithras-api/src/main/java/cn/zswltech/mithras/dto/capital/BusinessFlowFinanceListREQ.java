package cn.zswltech.mithras.dto.capital;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/20/18:32
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "业务流水资金端请求体")
public class BusinessFlowFinanceListREQ extends PageReq {

    @ApiModelProperty(value = "核销状态")
    private List<String> writeOffStatusList;

    @ApiModelProperty(value = "日期开始")
    private String dateFrom;

    @ApiModelProperty(value = "日期结束")
    private String dateTo;

    @ApiModelProperty(value = "融资渠道")
    private String financingRoute;

    @ApiModelProperty(value = "业务流水类型，付款-PAY，收款-COLLECT")
    @NotBlank(message = "业务流水类型不能为空")
    private String flowType;

    @ApiModelProperty(value = "现金流类型")
    private String cashFlowItem;

    @ApiModelProperty(value = "融资编号")
    private String financingCode;

    public static final String FLOW_TYPE_PAY = "PAY";
    public static final String FLOW_TYPE_COLLECT = "COLLECT";
}
