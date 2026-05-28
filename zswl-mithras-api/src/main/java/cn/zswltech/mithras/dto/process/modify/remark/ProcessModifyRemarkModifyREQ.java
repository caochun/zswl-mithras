package cn.zswltech.mithras.dto.process.modify.remark;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yibin
 */

@Data
@ApiModel("变更流程-附加标记信息-修改-请求体")
public class ProcessModifyRemarkModifyREQ extends ProcessModifyRemarkAddREQ {

    @NotNull
    private Long id;
}
