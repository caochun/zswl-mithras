package cn.zswltech.mithras.api.dto.onlyoffice;

import cn.zswltech.mithras.dto.MaterialsListIdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 获取文档配置的请求参数
 *
 * @author wangchuanhao
 * @date 2022/7/8 10:23 AM
 */
@Data
@ApiModel("获取文档配置-请求体")
public class GetDocDetailREQ extends MaterialsListIdType {

//    @ApiModelProperty("模块枚举")
//    @NotBlank
//    private String moduleType;

    @ApiModelProperty("文件id")
    @NotNull
    private Long id;

    @ApiModelProperty("操作类型：1预览；2编辑")
    @NotNull
    private Integer operate;

    @ApiModelProperty("窗体类型：1浏览器（默认）；2移动端；")
    private Integer windowType;

}
