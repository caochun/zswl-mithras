package cn.zswltech.mithras.dto.contract;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 租赁往来方列表-请求体
 * @author vico
 * @date 2022-08-12
 */
@Data
@ApiModel("租赁往来方列表-请求体")
@NoArgsConstructor
@AllArgsConstructor
public class HighSeasCustomersREQ extends VersionBaseREQ {

    @ApiModelProperty(value = "合同id")
    private Long contractId;

    @ApiModelProperty("公海客户名称")
    private String customerName;

}
