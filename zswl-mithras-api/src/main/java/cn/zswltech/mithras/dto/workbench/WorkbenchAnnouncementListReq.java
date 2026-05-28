package cn.zswltech.mithras.dto.workbench;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 首页工作台-公告
 * @date 2023-03-15
 */
@Data
@ApiModel("首页工作台-公告列表-请求体")
public class WorkbenchAnnouncementListReq extends PageReq {

    @ApiModelProperty(value = "是否有效期内")
    private Integer exceptionFlag;
}
