package cn.zswltech.mithras.dto.file.template;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yibin
 */
@Data
public class FileTemplateHistoryRollbackREQ {

    @NotNull
    @ApiModelProperty("历史记录id")
    private Long id;
}
