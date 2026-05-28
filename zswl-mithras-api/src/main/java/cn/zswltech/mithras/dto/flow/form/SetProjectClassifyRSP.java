package cn.zswltech.mithras.dto.flow.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 项目评审流程，评审会秘书汇票节点，设置项目分类
 *
 * @author wangchuanhao
 * @date 2022/9/8 2:39 PM
 */
@Data
public class SetProjectClassifyRSP {

    @ApiModelProperty("项目分类")
    private String projectClassify;

}
