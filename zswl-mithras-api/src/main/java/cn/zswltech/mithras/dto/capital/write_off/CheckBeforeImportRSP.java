package cn.zswltech.mithras.dto.capital.write_off;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author bigbear
 * @date 2024/9/19 19:04
 * @description
 */
@Data
@ApiModel(value = "导入流水前的校验返回参数")
public class CheckBeforeImportRSP {

    @ApiModelProperty(value = "未选中的银行流水列表")
    private Map<String, List<BankFlowCenterListBO>> unSelectedBankFlowMap;
}
