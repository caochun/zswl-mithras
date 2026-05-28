package cn.zswltech.mithras.dto.process.prepare;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author luyi
 */
@Data
public class ProcessPrepareListREQ extends PageReq {
    @ApiModelProperty("表单名称")
    private String formName;
    @ApiModelProperty("流程类型")
    private List<String> processTypeList;
    @ApiModelProperty("发起人")
    private String startUserId;

    private String businessId;

    //流程id
    private String processInstanceId;
}
