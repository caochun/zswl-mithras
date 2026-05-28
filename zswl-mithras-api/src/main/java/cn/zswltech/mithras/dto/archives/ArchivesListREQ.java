package cn.zswltech.mithras.dto.archives;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @create: 2023-02-23
 **/

@EqualsAndHashCode(callSuper = true)
@Data
public class ArchivesListREQ extends PageReq {

    @ApiModelProperty("文件类型")
    private String fileType;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("业务类型")
    private String bizType;

    @ApiModelProperty("业务部门")
    private Long bizDeptId;

    @ApiModelProperty("业务主办")
    private Long projSponsorUserId;

    @ApiModelProperty("创建时间从")
    private LocalDateTime createTimeFrom;

    @ApiModelProperty("创建时间到")
    private LocalDateTime createTimeTo;

    @ApiModelProperty("更新时间从")
    private LocalDateTime updateTimeFrom;

    @ApiModelProperty("更新时间到")
    private LocalDateTime updateTimeTo;

    private List<Long> deptIdList;

    private Boolean isBizUser;

    private Long currentUserId;

    private List<Long> archivesId;
}
