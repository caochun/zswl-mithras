package cn.zswltech.mithras.dto.file.template;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yibin
 */
@Data
public class FileTemplateHistoryListREQ extends PageReq {
    @NotNull
    @ApiModelProperty("文件模板id")
    private Long id;
}
