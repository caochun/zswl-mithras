package cn.zswltech.mithras.rating.application.process.prepare.handle;

import cn.zswltech.mithras.workflow.process.prepare.handle.AbstractFlowCommitHandle;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.rating.mapper.RatingClientMapper;
import cn.zswltech.mithras.rating.model.RatingClient;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.common.ProcessStatus;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.workflow.model.CommonProcessPrepare;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName RatingClientUpdateFlow
 * @Description TODO
 * @Author wandaokuan
 * @Date 2026/2/6 5:18 下午
 * @Version 1.0
 **/
@Slf4j
@Component
public class RatingClientUpdateFlowHandle extends AbstractFlowCommitHandle {

    @Resource
    private RatingClientMapper ratingClientMapper;

    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equals(processType, ProcessModelTypeEnum.RatingClientUpdateFlow.name());
    }

    @Override
    public String commit(CommonProcessPrepare prepare) {
        String businessId = prepare.getBusinessId();
        RatingClient ratingClient = ratingClientMapper.selectById(Long.valueOf(businessId));
        if (ratingClient == null) {
            throw new MithrasException("无客户评级记录");
        }
        if (ratingClient.getProcessStatus() == null || ProcessStatus.UN_SUBMIT.name().equals(ratingClient.getProcessStatus())) {
            throw new MithrasException("请先完成客户评级材料填写，并提交");
        }
        return businessId;
    }
}
