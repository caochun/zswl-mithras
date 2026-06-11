package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author junke
 */
@ApiModel("融租易App端功能权限-返回体")
@Data
public class AppAuthorityDetailRSP {

    @ApiModelProperty(value = "功能入口")
    private String title;

    @ApiModelProperty(value = "模块名称")
    private String name;

    @ApiModelProperty(value = "是否展示")
    private boolean show;

    public AppAuthorityDetailRSP(String title, String name, boolean show) {
        this.title = title;
        this.name = name;
        this.show = show;
    }
}
