package cn.zswltech.mithras.dto.utils;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author yibin
 */
@Data
public class CashFlowGenerationExportREQ {

    @Valid
    @NotNull
    private CashFlowGenerationExecREQ generationParams;

    //0 租赁，1保理
    private Integer dataSource;

    @NotEmpty
    private List<CashFlowGenerationIrrREQ.CashFlowGenerationIrrItem> items;
}
