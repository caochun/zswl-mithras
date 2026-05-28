package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 * @author yibin
 */
@Data
public class SponsorClientDetailNewModifyREQ {
    /**
     * businessKey
     */
    @NotBlank
    @ApiModelProperty("审批流中的businessKey")
    private String batchNo;

    @ApiModelProperty("正式移交日期")
    private LocalDate transferDate;

    @ApiModelProperty("说明")
    private String description;

}
