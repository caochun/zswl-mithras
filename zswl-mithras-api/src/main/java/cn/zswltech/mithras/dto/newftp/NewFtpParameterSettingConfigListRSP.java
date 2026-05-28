package cn.zswltech.mithras.dto.newftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description ftp参数设定表
 * @author zhaozhengkang
 * @date 2023-05-21
 */
@Data
@ApiModel("ftp参数配置设定表列表-返回体")
public class NewFtpParameterSettingConfigListRSP {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
    * 参数类别
    */
    @ApiModelProperty(value = "参数类别")
    private String category;

    /**
    * 分类中文名
    */
    @ApiModelProperty(value = "分类中文名")
    private String categoryDisplay;

    /**
    * 参数名称
    */
    @ApiModelProperty(value = "参数名称")
    private String paramName;

    /**
    * 参数值
    */
    @ApiModelProperty(value = "参数值")
    private Integer value;

    /**
     * 参数其他名称，扩展用
     */
    @ApiModelProperty(value = "param_other_name")
    private String paramOtherName;

    /**
     * 公式
     */
    @ApiModelProperty(value = "formula")
    private List<NewFtpParameterDTO> formula;

}
