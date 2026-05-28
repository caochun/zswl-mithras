package cn.zswltech.mithras.blackgray.dto.rsp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 *
 * @author: jackerhe
 * @date: 2023/11/30 9:39 上午
 **/
@Data
@ApiModel("黑灰名单库新增-请求体")
public class BlackGrayBusinessTypeRsp {

    private Long id;

    @ApiModelProperty(value = "业务类型代码")
    private String businessName;

    @ApiModelProperty(value = "业务类型描述")
    private String businessDesc;

    @ApiModelProperty(value = "所属金控id")
    private Long mainId;

    private Long parentId;

    private Integer level;

    private List<BlackGrayBusinessTypeRsp> child;

}
