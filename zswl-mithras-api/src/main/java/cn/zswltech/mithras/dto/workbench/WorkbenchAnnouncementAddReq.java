package cn.zswltech.mithras.dto.workbench;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 首页工作台-公告
 * @date 2023-03-15
 */
@Data
@ApiModel("首页工作台-公告新增-请求体")
public class WorkbenchAnnouncementAddReq {
    @ApiModelProperty(value = "标题")
    @NotBlank
    private String title;

    @ApiModelProperty(value = "有效期开始")
    private LocalDate expirationFrom;

    @ApiModelProperty(value = "有效期结束")
    private LocalDate expirationTo;
}
