package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yangxiong
 * @since 2023-09-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaseTextRSP {

    @ApiModelProperty(value = "租赁物清单")
    private List<FileListType> leaseInventory;

    @ApiModelProperty(value = "租赁物确认函")
    private List<FileListType> leaseEnterLetter;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    private static class FileListType{

        @ApiModelProperty(value = "文件类型")
        private String fileType;

        @ApiModelProperty(value = "上传人")
        private String uploadBy;

        @ApiModelProperty(value = "上传时间, 格式：yyyy-MM-dd HH:mm:ss")
        private String uploadTime;
    }
}
