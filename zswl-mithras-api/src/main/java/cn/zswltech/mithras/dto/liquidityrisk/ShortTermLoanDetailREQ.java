package cn.zswltech.mithras.dto.liquidityrisk;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @ClassName AssetInflowDetailREQ
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/5/15 4:23 下午
 * @Version 1.0
 **/
@Data
@ApiModel("流动性风险-短期贷款明细查询-请求体")
public class ShortTermLoanDetailREQ extends PageReq {

    //开始时间
    @ApiModelProperty("开始时间")
    @NotNull(message = "开始时间不能为空")
    private LocalDate fromTime;

    //结束时间
    @ApiModelProperty("结束时间")
    @NotNull(message = "结束时间不能为空")
    private LocalDate toTime;

    //是否需要分页，ture 正常分页，false 不分页
    private Boolean needPage = true;

}
