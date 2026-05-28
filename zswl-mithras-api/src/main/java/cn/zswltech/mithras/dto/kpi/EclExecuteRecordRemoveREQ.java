package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description 资产减值记录表
 * @author vico
 * @date 2025-09-28
 */
@Data
@ApiModel("资产减值记录表删除-请求体")
public class EclExecuteRecordRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

    private List<Long> ids;

}
