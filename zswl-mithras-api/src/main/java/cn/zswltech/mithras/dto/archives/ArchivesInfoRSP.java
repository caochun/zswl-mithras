package cn.zswltech.mithras.dto.archives;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @create: 2023-02-23
 **/

@Data
public class ArchivesInfoRSP {

    @ApiModelProperty("归档id")
    private Long id;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("归档状态")
    private String status;

    @ApiModelProperty("流程状态ArchivesFlowStatusEnum")
    private String flowStatus;

    @ApiModelProperty("必传文档情况")
    private String requiredSituation;

    @ApiModelProperty("任务类型：0 系统 1 手动")
    private Integer type;

    @ApiModelProperty("待补充文档")
    private List<String> fileTypes;

}