package cn.zswltech.mithras.api.riskcontrol.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yibin
 */
@Data
public class JzdReportRemoveREQ {

    @NotNull
    private Long id;
}
