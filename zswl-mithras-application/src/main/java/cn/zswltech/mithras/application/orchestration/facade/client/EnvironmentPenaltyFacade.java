package cn.zswltech.mithras.application.orchestration.facade.client;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.customer.application.client.EnvironmentPenaltyApplicationService;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.external.ExternalPageREQ;
import cn.zswltech.mithras.dto.client.external.environment.EnvironmentPenaltyAddREQ;
import cn.zswltech.mithras.dto.client.external.environment.EnvironmentPenaltyModifyREQ;
import cn.zswltech.mithras.dto.client.external.environment.EnvironmentPenaltyRSP;
import cn.zswltech.mithras.dto.client.external.environment.EnvironmentPenaltyRemoveREQ;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.customer.application.client.auth.ClientAddSubAuthCheckerNew;
import cn.zswltech.mithras.customer.application.client.auth.ClientModifySubAuthCheckerNew;
import cn.zswltech.mithras.customer.application.client.auth.ClientRemoveSubAuthCheckerNew;
import cn.zswltech.mithras.customer.application.client.auth.ClientViewMainAuthCheckerNew;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.third.externaldata.environmentpenalty.persistence.mapper.EnvironmentPenaltyMapper;
import cn.zswltech.mithras.third.externaldata.environmentpenalty.persistence.model.EnvironmentPenalty;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.third.externaldata.environmentpenalty.application.EnvironmentPenaltyService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 外部信息 中登网
 *
 * @author wangchuanhao
 * @date 2022/6/21 2:58 PM
 */
@Service
public class EnvironmentPenaltyFacade implements EnvironmentPenaltyApplicationService {

    @Resource
    private EnvironmentPenaltyService environmentPenaltyService;

    @Override
    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientAddSubAuthCheckerNew.class, businessModule = "CLIENT")
    public R<Void> add(EnvironmentPenaltyAddREQ req) {
        environmentPenaltyService.add(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientModifySubAuthCheckerNew.class, businessModule = "CLIENT", mapperClass = EnvironmentPenaltyMapper.class)
    public R<Void> modify(EnvironmentPenaltyModifyREQ req) {
        environmentPenaltyService.modify(req);
        return R.ok();
    }

    @Override
//    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientViewMainAuthCheckerNew.class, businessModule = "CLIENT")
    public R<PageR<EnvironmentPenaltyRSP>> list(ExternalPageREQ req) {
        if(!SpringContextHolder.getBean(ClientService.class).checkClientAuth(req.getClientId(), null)){
            return R.ok(PageR.of(Collections.emptyList(), 0));
        }
        Page<EnvironmentPenalty> data = environmentPenaltyService.list(req);
        List<EnvironmentPenaltyRSP> list = BeanUtil.copyToList(data.getRecords(), EnvironmentPenaltyRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientRemoveSubAuthCheckerNew.class, businessModule = "CLIENT", mapperClass = EnvironmentPenaltyMapper.class)
    public R<Void> remove(EnvironmentPenaltyRemoveREQ req) {
        environmentPenaltyService.remove(req);
        return R.ok();
    }

}
