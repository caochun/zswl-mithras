package cn.zswltech.mithras.dto.workbench;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/3/15 15:15
 */
@Data
@ApiModel("首页工作台-公告详情-返回体")
public class WorkbenchAnnouncementDetailRsp {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "标题")
    private String title;
    @ApiModelProperty(value = "正文")
    private String content;
    @ApiModelProperty(value = "有效期开始")
    private LocalDate expirationFrom;
    @ApiModelProperty(value = "有效期结束")
    private LocalDate expirationTo;
    @ApiModelProperty
    private List<ImageFileRsp> images;
    @ApiModelProperty
    private LocalDateTime updateTime;

    @ApiModelProperty
    private LocalDateTime createTime;

    @ApiModelProperty(value = "置顶")
    private Integer top;

    @Data
    public static class ImageFileRsp {
        private String preUrl;
        private Long id;
    }
}
