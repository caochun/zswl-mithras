package cn.zswltech.mithras.dto.contract.baseinfo;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @description 合同基本信息表
 * @author vico
 * @date 2022-08-12
 */
@Data
@ApiModel("合同基本信息表新增-请求体")
public class ContractBaseInfoAddREQ {

    /**
     * 立项ID
     */
    @ApiModelProperty(value = "立项ID")
    @NotNull(message = "评审ID不能为空")
    private Long projReviewId;

    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称")
    @NotNull(message = "项目名称不能为空")
    private String projName;

    /**
     * 客户Name
     */
    @ApiModelProperty(value = "客户Name")
    @NotNull(message = "客户名称不能为空")
    private String clientName;

    /**
     * 业务类型
     */
    @ApiModelProperty(value = "业务类型")
    @NotNull(message = "业务类型不能为空")
    private String bizType;

    @ApiModelProperty(value = "租赁类型。直租、回租、经营性租赁")
    private String leaseType;

}
