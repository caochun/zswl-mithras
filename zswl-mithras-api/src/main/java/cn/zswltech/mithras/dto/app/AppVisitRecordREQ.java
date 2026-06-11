package cn.zswltech.mithras.dto.app;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 生效
 *
 * @author zhouning
 * @date 2024/10/14 11:56 PM
 */
@Data
@ApiModel("融租易APP拜访记录-请求体")
public class AppVisitRecordREQ extends PageReq {

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名字")
    private String clientName;

    @ApiModelProperty("拜访方式")
    private List<String> visitWays;

    @ApiModelProperty("拜访类型")
    private List<String> visitTypes;

    @ApiModelProperty("拜访阶段")
    private List<String> visitPhases;

    @ApiModelProperty("状态标签")
    private List<String> statuses;

    @ApiModelProperty("拜访日期-从")
    private LocalDate visitTimeFrom;

    @ApiModelProperty("拜访日期-到")
    private LocalDate visitTimeTo;
}
