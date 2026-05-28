package cn.zswltech.mithras.dto.client.normal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junke
 */
@Data
@ApiModel("配偶下拉-返回体")
public class NormalSpouseSelectRSP {

    @ApiModelProperty(value = "客户id", required = true)
    private Long clientId;

    @ApiModelProperty(value = "客户姓名", required = true)
    private String clientName;

    @ApiModelProperty(value = "证件类型", required = true)
    private String certType;

    @ApiModelProperty(value = "证件号码", required = true)
    private String certNumber;

    //前端需要
    public String getLabel() {
        return clientName + "-" + certNumber;
    }

    public Long getValue() {
        return clientId;
    }
}
