package cn.zswltech.mithras.dto.client.normal;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junke
 */
@Data
@ApiModel("自然人配偶详情-返回体")
public class NormalSpouseListRSP extends ListBaseRSP {

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty(value = "配偶姓名", required = true)
    private String spouseName;

    @ApiModelProperty(value = "证件类型", required = true)
    private String certType;


    @ApiModelProperty(value = "证件号码", required = true)
    private String certNumber;

    @ApiModelProperty(value = "配偶客户id")
    private Long spouseClientId;
}
