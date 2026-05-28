package cn.zswltech.mithras.dto.contract.baseinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
@Accessors(chain = true)
@ApiModel("合同本信息表新增-返回体")
public class ContractBaseInfoAddRSP {

    /**
     * 项目评审基本id
     */
    @ApiModelProperty(value = "项目评审基本id")
    private Long id;

    /**
     * 业务类型
     */
    @ApiModelProperty(value = "业务类型")
    private String bizType;

    @ApiModelProperty("主承租人客户编号")
    private String mainClientCode;
}
