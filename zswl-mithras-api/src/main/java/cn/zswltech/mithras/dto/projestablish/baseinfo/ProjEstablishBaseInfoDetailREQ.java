package cn.zswltech.mithras.dto.projestablish.baseinfo;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junke
 */
@Data
@ApiModel("立项基本信息表详情-请求体")
public class ProjEstablishBaseInfoDetailREQ extends VersionBaseREQ {

    @ApiModelProperty("id")
    private Long id;
}
