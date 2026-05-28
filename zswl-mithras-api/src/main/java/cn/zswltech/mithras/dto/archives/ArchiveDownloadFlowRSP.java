package cn.zswltech.mithras.dto.archives;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @create: 2023-03-17
 **/

@Data
public class ArchiveDownloadFlowRSP {

    @ApiModelProperty("申请原因")
    private String reason;

    @ApiModelProperty("审批材料")
    private List<ArchiveDownloadFlowRSP.MaterialsData> materials;

    @Data
    public static class MaterialsData{
        @ApiModelProperty("项目名称")
        private String projName;

        @ApiModelProperty("key项目id")
        private Long key;

        @ApiModelProperty("资料列表")
        private List<ArchiveDownloadFlowRSP.Materials> materialsData;

    }
    @Data
    public static class Materials{
        @ApiModelProperty("资料名称")
        private String materialsName;

        @ApiModelProperty("key")
        private String key;

        @ApiModelProperty("文件列表")
        private List<ArchiveDownloadFlowRSP.FileInfo> files;

    }
    @Data
    public static class FileInfo{

        @ApiModelProperty("文件id")
        private Long fileId;

        @ApiModelProperty("文件名")
        private String fileName;
    }

}
