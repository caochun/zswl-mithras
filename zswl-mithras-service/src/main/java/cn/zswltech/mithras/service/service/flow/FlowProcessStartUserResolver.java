package cn.zswltech.mithras.service.service.flow;

import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.service.service.ProcessStartUserResolver;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class FlowProcessStartUserResolver implements ProcessStartUserResolver {

    @Resource
    private FlowTaskApiService taskApiService;

    @Override
    public Long processStartUserId(String processId) {
        ProcessResp processResp = taskApiService.queryProcessById(processId);
        if (processResp == null || processResp.getStartUserId() == null) {
            return null;
        }
        return Long.valueOf(processResp.getStartUserId());
    }
}
