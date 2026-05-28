package cn.zswltech.mithras.dto.projestablish.version;

import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceDetailRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@ApiModel("立项版本详情-出参")
@Data
@Accessors(chain = true)
public class ProjEstablishVersionDetailRSP {
    @ApiModelProperty("版本号")
    @NotNull
    private String version;

    @ApiModelProperty("基本信息")
    @NotNull
    private ProjEstablishBaseInfoListRSP baseInfo;

    @ApiModelProperty("报价方案")
    @NotNull
    private ProjEstablishPriceDetailRSP price;
}
