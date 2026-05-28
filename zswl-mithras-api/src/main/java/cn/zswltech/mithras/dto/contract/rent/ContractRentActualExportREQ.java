package cn.zswltech.mithras.dto.contract.rent;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/8/18
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("导出实际租金表（借据）-请求体")
public class ContractRentActualExportREQ extends VersionBaseREQ {
    @NotNull(message = "借据id不能为空")
    @ApiModelProperty("借据id")
    private Long receiptId;

    @NotNull(message = "数据来源不能为空")
    @ApiModelProperty("数据来源，history-历史数据，current-当前数据")
    private String dataSource;
}
