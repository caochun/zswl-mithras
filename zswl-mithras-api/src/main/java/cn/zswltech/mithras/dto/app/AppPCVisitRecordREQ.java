package cn.zswltech.mithras.dto.app;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * 生效
 *
 * @author zhouning
 * @date 2024/10/14 11:56 PM
 */
@Data
@ApiModel("融租易访客管理拜访明细-请求体")
public class AppPCVisitRecordREQ extends PageReq {


    @ApiModelProperty("客户名字")
    private String clientName;

    @ApiModelProperty("拜访人id")
    private Long userId;

    @ApiModelProperty("打卡类型")
    private String visitWay;

    @ApiModelProperty("拜访类型")
    private String visitType;

    @ApiModelProperty("拜访阶段")
    private String visitPhase;

    @ApiModelProperty("拜访日期-从")
    private LocalDate visitTimeFrom;

    @ApiModelProperty("拜访日期-到")
    private LocalDate visitTimeTo;
}
