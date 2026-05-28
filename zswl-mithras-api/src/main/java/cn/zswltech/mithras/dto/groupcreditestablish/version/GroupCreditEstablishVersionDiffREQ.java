package cn.zswltech.mithras.dto.groupcreditestablish.version;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 集团授信立项基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@ApiModel("集团授信立项版本差异比较-入参")
@Data
public class GroupCreditEstablishVersionDiffREQ {
    @ApiModelProperty("版本id")
    @NotNull
    private Long id;
}
