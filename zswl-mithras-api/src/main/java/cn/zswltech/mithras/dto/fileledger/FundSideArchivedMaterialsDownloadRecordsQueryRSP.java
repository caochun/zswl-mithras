package cn.zswltech.mithras.dto.fileledger;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 资金端归档资料-下载记录查询
 *
 * @author gxy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FundSideArchivedMaterialsDownloadRecordsQueryRSP {

    @ApiModelProperty("文件名")
    private String fileName;

    @ApiModelProperty("下载时间")
    private LocalDateTime downloadTime;

    @ApiModelProperty("下载状态")
    private String downloadStatus;

    @ApiModelProperty("文件url")
    private String filePath;
}
