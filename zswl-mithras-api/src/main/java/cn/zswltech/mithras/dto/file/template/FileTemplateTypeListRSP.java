package cn.zswltech.mithras.dto.file.template;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yibin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileTemplateTypeListRSP {

    @ApiModelProperty("模板类型")
    private List<String> templateTypes;


}
