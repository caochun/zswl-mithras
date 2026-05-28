package cn.zswltech.mithras.dto.archives;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @create: 2023-03-17
 **/

@Data
public class ArchivesFlowRSP {

    @ApiModelProperty("模版Id")
    private Long templateId;

    @ApiModelProperty("模版名称")
    private String templateName;

    @ApiModelProperty("资料")
    private MaterialsData materials;

    @Data
    public static class MaterialsData{
        @ApiModelProperty("项目名称")
        private String projName;

        @ApiModelProperty("key项目id")
        private Long key;

        @ApiModelProperty("资料列表")
        private List<ArchivesFlowRSP.Materials> materialsData;

    }
    @Data
    public static class Materials{
        @ApiModelProperty("资料名称")
        private String materialsName;

        @ApiModelProperty("key")
        private String key;

        @ApiModelProperty("文件数量")
        private Integer size;

        @ApiModelProperty("是否置灰")
        private Boolean grey;


        @ApiModelProperty("文件类型列表")
        private List<ArchivesFlowRSP.FileTypes> fileTypes;

    }
    @Data
    public static class FileTypes{
        @ApiModelProperty("文件类型名称")
        private String fileTypeName;

        @ApiModelProperty("key")
        private String key;

        @ApiModelProperty("文件数量")
        private Integer size;

        @ApiModelProperty("是否置灰")
        private Boolean grey;

        @ApiModelProperty("文件列表")
        private List<ArchivesFlowRSP.FileInfo> files;

    }
    @Data
    public static class FileInfo{

        @ApiModelProperty("文件id")
        private Long fileId;

        @ApiModelProperty("文件名")
        private String fileName;
    }

}
