package cn.zswltech.mithras.dto.groupcreditestablish.baseinfo;
import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotNull;

/**
 * @description 集团授信立项基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@Data
@ApiModel("集团授信立项基本信息详情-请求体")
public class GroupCreditEstablishBaseInfoDetailREQ extends VersionBaseREQ {

    @NotNull
    @ApiModelProperty(value = "集团授信立项id")
    private Long groupCreditEstablishId;

}
