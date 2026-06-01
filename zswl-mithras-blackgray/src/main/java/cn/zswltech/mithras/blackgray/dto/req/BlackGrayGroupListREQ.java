package cn.zswltech.mithras.blackgray.dto.req;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@ApiModel
@Data
@EqualsAndHashCode(callSuper = true)
public class BlackGrayGroupListREQ extends PageReq {

    @ApiModelProperty(value = "导出时勾选的集团名称数组")
    private Set<String> exportGroupNames;

    @ApiModelProperty(value = "集团名称或信用代码")
    private String groupNameCreditCode;

    @ApiModelProperty(value = "businessType")
    private String businessType;
}
