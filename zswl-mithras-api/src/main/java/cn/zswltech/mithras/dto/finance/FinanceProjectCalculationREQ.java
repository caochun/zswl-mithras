package cn.zswltech.mithras.dto.finance;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author lizhao
 * @date 2023/6/18
 * @description
 */
@Data
@ApiModel("财务管理-项目利润测算-请求参数")
public class FinanceProjectCalculationREQ {

    @ApiModelProperty("测算利润月份入参")
    private LocalDate yearAndMonth;
}
