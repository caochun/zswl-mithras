package cn.zswltech.mithras.afterlease.application.projfms;

import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.AfterLeaseAdjustInfo;
import cn.zswltech.mithras.service.service.projfms.ProjStateMachine;
import org.springframework.stereotype.Service;

/**
 * @ClassName AfterLeaseAdjustMachine
 * @Description
 * @Author jackerhe
 * @Date 2022/11/8 2:51 下午
 * @Version 1.0
 **/
@Service
public class AfterLeaseAdjustMachine extends ProjStateMachine<AfterLeaseAdjustInfo> {
}
