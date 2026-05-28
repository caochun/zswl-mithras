package cn.zswltech.mithras.service.controller.contract;

import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.contract.ContractStartRentNoticeApi;
import cn.zswltech.mithras.service.enums.TimeoutTypeEnum;
import cn.zswltech.mithras.service.service.Listener.timeout.TimeoutStartEvent;
import cn.zswltech.mithras.service.service.contract.delayqueue.DelayQueueService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @ClassName ContractStartRentNoticeController
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/4/27 3:42 下午
 * @Version 1.0
 **/
@RestController
public class ContractStartRentNoticeController implements ContractStartRentNoticeApi {
    @Resource
    private DelayQueueService delayQueueService;
    @Override
    public R<Void> savePlanNormal() {
        TimeoutStartEvent timeoutStartEvent = new TimeoutStartEvent(this, String.valueOf(AccountUtil.getLoginInfo().getId()),
                System.currentTimeMillis(),
                600L,
                TimeoutTypeEnum.CONTRACT_RENT.name(), true);
        delayQueueService.addTask(timeoutStartEvent);
        return R.ok();
    }
}
