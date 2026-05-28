package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author yibin
 */
@Data
public class SponsorClientDetailREQ {
    /**
     * businessKey
     */
    @NotBlank
    @ApiModelProperty("审批流中的businessKey")
    private String batchNo;

}
