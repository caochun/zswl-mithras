package cn.zswltech.mithras.dto.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 *
 * 检查当前流程是否可继续
 * @author
 * @date 2022/8/23 11:02 AM
 */
@Data
@ApiModel("合同管理-检查当前流程是否可继续-基础请求体")
public class ContractCanChangeREQ extends ContractFlowBasicREQ {

    @ApiModelProperty("变更模块 ContractProcessStatusEnum 枚举值")
    private List<String> moduleType;

}
