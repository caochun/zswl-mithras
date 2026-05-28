package cn.zswltech.mithras.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel
@Data
public class OrgUserRSP {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("类型[org=机构,user=用户]")
    private String type;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("机构代码")
    private String orgCode;

    @ApiModelProperty("账号")
    private String account;

    private List<OrgUserRSP> children;

}
