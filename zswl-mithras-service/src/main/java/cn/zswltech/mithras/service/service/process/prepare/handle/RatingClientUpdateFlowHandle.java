package cn.zswltech.mithras.service.service.process.prepare.handle;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.factory.model.RatingClient;
import cn.zswltech.mithras.factory.service.RatingClientService;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.common.enums.ProcessStatus;
import cn.zswltech.mithras.service.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

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
    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equals(processType, ProcessModelTypeEnum.RatingClientUpdateFlow.name());
    }

    @Override
    public String commit(CommonProcessPrepare prepare) {
        String businessId = prepare.getBusinessId();
        LambdaQueryWrapper<RatingClient> ratingClientLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ratingClientLambdaQueryWrapper.eq(RatingClient::getId, Long.valueOf(businessId));
        RatingClient ratingClient = getBean(RatingClientService.class).getOne(ratingClientLambdaQueryWrapper);
        if (ratingClient == null) {
            throw new MithrasException("无客户评级记录");
        }
        if (ratingClient.getProcessStatus() == null || ProcessStatus.UN_SUBMIT.name().equals(ratingClient.getProcessStatus())) {
            throw new MithrasException("请先完成客户评级材料填写，并提交");
        }
        return businessId;
    }


}
