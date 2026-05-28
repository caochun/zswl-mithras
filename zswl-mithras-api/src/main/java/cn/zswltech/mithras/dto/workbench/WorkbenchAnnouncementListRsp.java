package cn.zswltech.mithras.dto.workbench;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author zhaozhengkang
 * @description 首页工作台-公告
 * @date 2023-03-15
 */
@Data
@ApiModel("首页工作台-公告列表-返回体")
public class WorkbenchAnnouncementListRsp {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "标题")
    private String title;
    @ApiModelProperty(value = "正文")
    private String content;

    @ApiModelProperty(value = "image")
    private String image;
    @ApiModelProperty(value = "置顶")
    private Integer top;
    @ApiModelProperty
    private LocalDateTime updateTime;
    @ApiModelProperty
    private LocalDateTime createTime;
    @ApiModelProperty(value = "有效期开始")
    private LocalDate expirationFrom;
    @ApiModelProperty(value = "有效期结束")
    private LocalDate expirationTo;
}
