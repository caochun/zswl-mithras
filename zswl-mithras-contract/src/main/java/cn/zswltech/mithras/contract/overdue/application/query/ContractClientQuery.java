package cn.zswltech.mithras.contract.overdue.application.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/6 16:41
 */
@Data
@ApiModel(value = "合同客户查询参数")
public class ContractClientQuery {
    @ApiModelProperty(value = "合同id集合")
    Set<Long> contractIds;
}
