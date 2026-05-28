package cn.zswltech.mithras.dto.projestablish.version;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

/**
 * @description 立项信息版本表
 * @author zhaozhengkang
 * @date 2022-07-19
 */
@Data
@ApiModel("立项信息版本表列表-返回体")
public class ProjEstablishVersionListRSP extends CommonVersionListRSP {

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("操作人id")
    private Long operatorId;

    @ApiModelProperty("操作人名称")
    private String operatorName;

    @ApiModelProperty("变更时间")
    private LocalDateTime gmtModify;

}
