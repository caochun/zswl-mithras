package cn.zswltech.mithras.metric.emit.model.rsp.required;

import lombok.Data;

import java.util.List;

/**
 * @author yibin
 */
@Data
public class RequireMetricRsp {
    /**
     * 报送任务id
     */
    private Long id;

    /**
     * 数据时点
     */
    private String dataTime;

    /**
     * 机构编码
     */
    private String orgCode;

    /**
     * 指标列表
     */
    private List<RequireMetricSingleBody> indexList;
}
