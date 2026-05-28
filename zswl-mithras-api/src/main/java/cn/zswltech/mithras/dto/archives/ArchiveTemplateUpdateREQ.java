package cn.zswltech.mithras.dto.archives;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @create: 2023-02-23
 **/

@Data
public class ArchiveTemplateUpdateREQ {

    @ApiModelProperty("模版id")
    private Long templateId;

    @ApiModelProperty("模版状态")
    private String status;

    @ApiModelProperty("业务类型")
    private List<String> bizType;

}