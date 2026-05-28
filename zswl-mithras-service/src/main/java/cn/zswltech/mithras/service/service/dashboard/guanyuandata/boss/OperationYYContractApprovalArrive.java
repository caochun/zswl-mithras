package cn.zswltech.mithras.service.service.dashboard.guanyuandata.boss;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.service.service.dashboard.guanyuandata.GuanYuanColumnPopulate;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2024/5/21
 * @description
 */
@Data
public class OperationYYContractApprovalArrive implements GuanYuanColumnPopulate {
    private Long contractId;
    private Long processId;

    /**
     * 运营经办到达时间
     */
    private LocalDateTime yunYingJbStartTime;

    /**
     * 运营经办结束时间
     */
    private LocalDateTime yunYingJbEndTime;

    /**
     * 运营复核到达时间
     */
    private LocalDateTime yunYingFhStartTime;

    /**
     * 运营复核结束时间
     */
    private LocalDateTime yunYingFhEndTime;

    @Override
    public void populate(Map<String, String> map) {
        String nodeName = map.get("节点");
        if(Arrays.asList("运营管理（经办）","运营管理（复核）").contains(nodeName)){
            this.contractId = Optional.ofNullable(map.get("合同id")).map(Long::valueOf).orElse(null);
            this.processId = Optional.ofNullable(map.get("流程id")).map(Long::valueOf).orElse(null);
            LocalDateTime nodeStartTime = Optional.ofNullable(map.get("开始")).map(d -> LocalDateTimeUtil.parse(d, "yyyy-MM-dd HH:mm:ss")).orElse(null);
            LocalDateTime nodeEndTime = Optional.ofNullable(map.get("结束")).map(d -> LocalDateTimeUtil.parse(d, "yyyy-MM-dd HH:mm:ss")).orElse(null);
            if(Objects.equals("运营管理（经办）", nodeName)){
                this.yunYingJbStartTime = nodeStartTime;
                this.yunYingJbEndTime = nodeEndTime;
            }else if(Objects.equals("运营管理（复核）", nodeName)){
                this.yunYingFhStartTime = nodeStartTime;
                this.yunYingFhEndTime = nodeEndTime;
            }

        }
    }
}
















