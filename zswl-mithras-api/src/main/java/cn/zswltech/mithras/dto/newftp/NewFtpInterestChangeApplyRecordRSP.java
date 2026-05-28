package cn.zswltech.mithras.dto.newftp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author dingqi
 * @date 2025/3/4
 * @description
 */
@Data
public class NewFtpInterestChangeApplyRecordRSP {
    @ApiModelProperty("申请记录id")
    private Long id;

    @ApiModelProperty("申请用户id")
    private Long applyUserId;

    @ApiModelProperty("申请用户名称")
    private String applyUserName;

    @ApiModelProperty("审批状态")
    private String approvalStatus;

    @ApiModelProperty("FTP考核信息")
    private List<FtpAssessInfo> ftpAssessmentInfoList = Collections.emptyList();
}
