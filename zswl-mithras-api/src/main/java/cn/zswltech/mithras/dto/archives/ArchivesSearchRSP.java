package cn.zswltech.mithras.dto.archives;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @create: 2023-02-23
 **/

@Data
public class ArchivesSearchRSP {

    private Integer sort;

    @ApiModelProperty("资料类型")
    private String groupName;

    @ApiModelProperty("文件列表")
    private List<FileInfo> files;

    @Data
    public static class FileInfo{

        @ApiModelProperty("文件类型")
        private String fileType;

        @ApiModelProperty("文件id")
        private Long fileId;

        @ApiModelProperty("文件名")
        private String fileName;

        @ApiModelProperty("上传人")
        private String uploadUser;

        @ApiModelProperty("上传时间")
        private LocalDateTime uploadTime;

        @ApiModelProperty("借阅状态")
        private String status;

        @ApiModelProperty("是否可下载")
        private Boolean canDownload;
    }
}