package cn.zswltech.mithras.dto.riskcontrol.opinion;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author vico
 * @description 风控舆情监控
 * @date 2023-03-09
 */
@Data
@ApiModel("风控舆情监控列表-请求体")
public class RiskWarnMonitorOpinionListREQ extends PageReq {

    @ApiModelProperty("id")
    private Long id;

    private List<Long> ids;

    /**
     * 客户名称
     */
    @ApiModelProperty(value = "客户名称，支持模糊")
    private String chiName;

    @ApiModelProperty("处理状态")
    private String handleStatus;

}
