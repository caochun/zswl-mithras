package cn.zswltech.mithras.dto.client.external.tyc;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 失信人
 *
 * @author wangchuanhao
 * @date 2022/6/20 11:06 PM
 */
@Data
@ApiModel("天眼查-失信人")
public class TycDishonestRSP {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("立案日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime regDate;

    @ApiModelProperty("案号")
    private String caseCode;

    @ApiModelProperty("执行依据文号")
    private String gistId;

    @ApiModelProperty("执行法院")
    private String courtName;

    @ApiModelProperty("失信行为")
    private String disruptTypeName;

    @ApiModelProperty("履行情况")
    private String performance;

    @ApiModelProperty("发布日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime publishDate;

}
