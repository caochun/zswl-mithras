package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
@ApiModel("客户信息")
public class ClientInfo implements Comparable<ClientInfo> {
    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户类型")
    private String clientType;

    @ApiModelProperty("租赁物文件类型")
    private String leaseItemFileType;

    @ApiModelProperty("存量风险敝口")
    private Long stockRiskExposure;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("境内or境外")
    private String domesticOrAbroad;

    private Long belongSponsorId;

    private String orgType;

    @Override
    public int compareTo(ClientInfo o) {
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
