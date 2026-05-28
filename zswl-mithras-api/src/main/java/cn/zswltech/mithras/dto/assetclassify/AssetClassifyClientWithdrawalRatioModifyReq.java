package cn.zswltech.mithras.dto.assetclassify;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/8/23 16:10
 */
@Data
@ApiModel("拨备计提修改请求体")
public class AssetClassifyClientWithdrawalRatioModifyReq {

    @ApiModelProperty("id")
    @NotNull
    private Long id;
    @ApiModelProperty("借据编号")
    private List<WithdrawalRatioWrapper> withdrawalRatios;
}
