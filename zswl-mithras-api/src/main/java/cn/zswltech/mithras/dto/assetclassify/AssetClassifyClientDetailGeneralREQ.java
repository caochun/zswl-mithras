package cn.zswltech.mithras.dto.assetclassify;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2023/1/4
 * @description
 */
@Data
@ApiModel("风控管理-资产分类客户详情-请求体")
public class AssetClassifyClientDetailGeneralREQ extends VersionBaseREQ {

    @ApiModelProperty("clientId")
    private Long clientId;

}
