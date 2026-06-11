package cn.zswltech.mithras.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;


@ApiModel
@Data
@AllArgsConstructor
public class UserRSP {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("用户名称")
    private String name;

    @ApiModelProperty("所属机构")
    private String orgs;

}
