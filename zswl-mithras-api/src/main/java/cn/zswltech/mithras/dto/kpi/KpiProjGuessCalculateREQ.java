package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * @ClassName KpiProjGuessContractIndexREQ
 * @Description 项目绩效测算表-请求体
 * @Author jackerhe
 * @Date 2023/6/15 4:28 下午
 * @Version 1.0
 **/
@Data
public class KpiProjGuessCalculateREQ {

    //核算月份
    @ApiModelProperty("核算月份")
    @NotNull(message = "核算月份不能为空")
    private LocalDate calculateDate;

    private List<Long> contractIds;

}
