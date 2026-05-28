package cn.zswltech.mithras.dto.file.template;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author yibin
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class FileTemplateListREQ extends PageReq {
    @NotNull
    @ApiModelProperty("模板类型")
    private String templateType;

    @ApiModelProperty(value = "文件名称", notes = "支持模糊搜索")
    private String filename;

}
