package cn.zswltech.mithras.dto.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;


@Data
@ApiModel("导出-请求体")
public class FileExportREQ {

    @NotBlank(message = "文件模块不能为空")
    @ApiModelProperty("模块类型 BusinessModuleEnum")
    private String businessType;

    @ApiModelProperty("用于存放勾选的记录id")
    private List<Long> ids;

    @ApiModelProperty("用于传输各导出自定义参数")
    private Map<String, String> ext;
}
