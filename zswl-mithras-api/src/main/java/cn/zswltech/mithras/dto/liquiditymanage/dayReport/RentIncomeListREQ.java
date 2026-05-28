package cn.zswltech.mithras.dto.liquiditymanage.dayReport;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author bigbear
 * @date 2024/12/13 16:43
 * @className RentIncomeListREQ
 * @description
 */
@Data
@ApiModel(value = "租金收入列表请求参数")
@EqualsAndHashCode(callSuper = true)
public class RentIncomeListREQ extends PageReq {
}
