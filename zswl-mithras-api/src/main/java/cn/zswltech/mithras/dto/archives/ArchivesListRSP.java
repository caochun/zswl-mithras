package cn.zswltech.mithras.dto.archives;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @create: 2023-02-23
 **/
@Data
public class ArchivesListRSP {
    @ApiModelProperty("归档id")
    private Long id;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("业务类型")
    private String bizType;

    @ApiModelProperty("业务部门")
    private String bizDept;

    @ApiModelProperty("业务主办")
    private String projSponsorUserName;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("归档状态")
    private String status;

    @ApiModelProperty("流程状态ArchivesFlowStatusEnum")
    private String flowStatus;

    @ApiModelProperty("任务编号")
    private String archivesCode;

    @ApiModelProperty("必传文档情况")
    private String requiredSituation;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("归档资料")
    private List<Archives> archives;


    @Data
    public static class Archives{

        @ApiModelProperty("key")
        private String groupKey;

        @ApiModelProperty("资料类型id")
        private Long groupId;

        @ApiModelProperty("资料类型")
        private String groupName;

        @ApiModelProperty("排序")
        private Integer sort;

        @ApiModelProperty("文件")
        private List<Files> files;
    }
    @Data
    public static class Files{
        @ApiModelProperty("文件id")
        private Long fileId;

        @ApiModelProperty("文件名")
        private String fileName;

        @ApiModelProperty("借阅状态")
        private String status;

        @ApiModelProperty("是否可下载")
        private Boolean canDownload;

    }
}
