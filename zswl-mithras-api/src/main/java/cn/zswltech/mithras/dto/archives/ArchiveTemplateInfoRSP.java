package cn.zswltech.mithras.dto.archives;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @create: 2023-02-23
 **/

@Data
public class ArchiveTemplateInfoRSP {

    @ApiModelProperty("模版id")
    private Long templateId;

    @ApiModelProperty("模版名称")
    @NotNull
    private String templateName;

    @ApiModelProperty("业务类型")
    @NotNull
    private List<String> bizType;

    @ApiModelProperty("模版状态")
    @NotNull
    private String status;

    @ApiModelProperty("一级资料类型列表")
    private List<ArchiveTemplateInfoRSP.Group> groups;

    @Data
    public static class Group{

        @ApiModelProperty("资料类型id")
        private Long groupId;

        @ApiModelProperty("资料类型")
        private String groupName;

        @ApiModelProperty("二级文件类型列表")
        private List<ArchiveTemplateInfoRSP.Item> items;
    }
    @Data
    public static class Item{

        @ApiModelProperty("文件类型id")
        private Long fileTypeId;

        @ApiModelProperty("文档类型")
        private String fileType;

        @ApiModelProperty("是否必传")
        private Integer need;
    }
}
