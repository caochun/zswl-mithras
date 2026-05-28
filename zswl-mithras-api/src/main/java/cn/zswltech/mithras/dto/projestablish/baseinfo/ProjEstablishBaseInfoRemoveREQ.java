package cn.zswltech.mithras.dto.projestablish.baseinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author luyi
 * @description 立项基本信息表
 * @date 2022-07-19
 */
@Data
@ApiModel("立项基本信息表删除-请求体")
public class ProjEstablishBaseInfoRemoveREQ {

    @NotEmpty
    @ApiModelProperty("ids")
    private List<Long> ids;

}
