package cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.flow.model;

import lombok.Data;

/**
 * @author yibin
 */
@Data
public class FlowQueryExtraMissing {
    private String processInstanceId;
    private String modelKey;
    private String businessKey;
}
