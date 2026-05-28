package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;

/**
 * @author junke
 * 担保人信息
 */
@Data
public class AppPersonInfo implements Comparable<AppPersonInfo> {

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户类型")
    private String clientType;

    @Min(0)
    @ApiModelProperty("存量风险敝口")
    private Long stockRiskExposure;

    @ApiModelProperty("客户名称")
    private String clientName;

    @Override
    public int compareTo(AppPersonInfo o) {
        if (clientId != null && null != o.getClientId()) {
            return clientId.compareTo(o.getClientId());
        }
        if (null != clientName && null != o.getClientName()) {
            return o.getClientName().compareTo(clientName);
        }
        if (clientId == null) {
            return -1;
        } else {
            return 1;
        }
    }
}
