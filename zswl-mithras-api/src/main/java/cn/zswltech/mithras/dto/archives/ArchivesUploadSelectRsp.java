package cn.zswltech.mithras.dto.archives;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @create: 2023-03-22
 **/
@Data
public class ArchivesUploadSelectRsp {

    @ApiModelProperty("资料类型")
    private String title;

    @ApiModelProperty("文件类型")
    private List<Select> children;

    @Data
    public static class Select {
        @ApiModelProperty("下拉展示文本")
        private String label;
        @ApiModelProperty("下拉选项值")
        private String value;
        @ApiModelProperty("是否必传,0 不必传 1 必传")
        private Integer required;

        @ApiModelProperty("已上传文件列表")
        private List<FileInfo> files;
    }

    @Data
    public static class FileInfo{

        @ApiModelProperty("文件id")
        private Long fileId;

        @ApiModelProperty("文件名")
        private String fileName;
    }
}
