package cn.zswltech.mithras.dto.contract.baseinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @description 合同基本信息表
 * @author vico
 * @date 2022-08-12
 */
@Data
@ApiModel("合同基本信息详情表列表-请求体")
@AllArgsConstructor
@NoArgsConstructor
public class ContractBaseInfoDetailREQ {
    @ApiModelProperty("id")
    @NotNull(message = "合同id不能为空")
    private Long id;

    @ApiModelProperty("版本号")
    private String businessVersion;

    public ContractBaseInfoDetailREQ(Long id) {
        this.id = id;
    }
}
