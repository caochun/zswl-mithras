package cn.zswltech.mithras.dto.contract;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/8/25
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("单个合同id通用请求体")
public class ContractSingleIdREQ extends VersionBaseREQ {
    @NotNull(message = "合同id不能为空")
    @ApiModelProperty("主合同id")
    private Long contractId;

    @ApiModelProperty("需要更改的合同类型")
    private List<String> generateContractType;

}
