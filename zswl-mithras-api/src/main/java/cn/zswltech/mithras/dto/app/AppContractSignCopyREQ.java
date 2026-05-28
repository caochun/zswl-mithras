package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yibin
 */
@Data
public class AppContractSignCopyREQ {


    @ApiModelProperty("选中拷贝合同id")
    private String contractId;

    @ApiModelProperty("合同id")
    private String existedContractId;

}
