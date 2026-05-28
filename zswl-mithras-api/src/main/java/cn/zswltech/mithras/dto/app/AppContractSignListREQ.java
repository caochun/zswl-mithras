package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yibin
 */
@Data
public class AppContractSignListREQ {


    @ApiModelProperty("签约状态")
    private Integer isSigned;

    @ApiModelProperty("合同编号")
    private String contractCode;

}
