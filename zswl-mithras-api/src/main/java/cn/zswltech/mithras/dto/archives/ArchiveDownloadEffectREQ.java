package cn.zswltech.mithras.dto.archives;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @create: 2023-02-23
 **/

@Data
public class ArchiveDownloadEffectREQ {
    @ApiModelProperty("归档任务ids")
    private List<Long> archivesIds;

    @ApiModelProperty("申请原因")
    private String reason;

    @ApiModelProperty("申请文件")
    private List<EffectFile> files;

    @Data
    public static class EffectFile{

        @ApiModelProperty("文件id")
        private Long fileId;

        @ApiModelProperty("到期时间：默认15年")
        private LocalDateTime expires;
    }
}
