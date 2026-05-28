package cn.zswltech.mithras.dto.contract.file;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/7/29
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContractTextInfoRSP extends ListBaseRSP {
    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty("合同文本类型")
    private List<String> textTypeList;
}
