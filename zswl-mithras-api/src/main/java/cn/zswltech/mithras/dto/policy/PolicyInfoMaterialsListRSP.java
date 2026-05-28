package cn.zswltech.mithras.dto.policy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@ApiModel("保单维护资料清单列表-返回体")
public class PolicyInfoMaterialsListRSP {

    @ApiModelProperty(value = "文件id")
    private Long id;

    @ApiModelProperty(value = "文件名")
    private String name;

    private Long createBy;

    private String createName;

    private LocalDateTime createTime;
}
