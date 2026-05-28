package cn.zswltech.mithras.dto.assetclassify;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/9/6 15:18
 */
@Data
@ApiModel("资产分类客户拨备计提比例列表请求体")
@EqualsAndHashCode(callSuper = true)
public class AssetClassifyClientWithdrawalRatioListReq extends VersionBaseREQ {

    @ApiModelProperty("id")
    @NotNull
    private Long id;
    @ApiModelProperty("是否查辅助版本表")
    private Boolean isAuxiliary;
}
