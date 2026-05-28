package cn.zswltech.mithras.dto.monthly;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bigbear
 */
@Data
public class MonthlyCloseREQ {

    @NotNull(message = "主数据ID不能为空")
    private Long mainId;
}
