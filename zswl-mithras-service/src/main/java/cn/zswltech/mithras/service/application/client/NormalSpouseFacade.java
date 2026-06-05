package cn.zswltech.mithras.service.application.client;

import cn.zswltech.mithras.customer.application.client.api.NormalSpouseApplicationService;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.normal.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.client.ClientAddSubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientModifySubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientRemoveSubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientViewMainAuthCheckerNew;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.NormalBaseInfo;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.normal.NormalSpouseMapper;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.NormalSpouseService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * @author junke
 */
@Service
public class NormalSpouseFacade implements NormalSpouseApplicationService {

    @Resource
    private NormalSpouseService spouseService;
    @Resource
    private ClientService clientService;

    @Override
    public R<List<NormalSpouseSelectRSP>> select(NormalSpouseSelectREQ req) {
        List<NormalBaseInfo> list = spouseService.selectList(req.getClientId(), req.getFuzzyName());
        List<NormalSpouseSelectRSP> result = new ArrayList<>(list.size());
        for (NormalBaseInfo info : list) {
            NormalSpouseSelectRSP rsp = new NormalSpouseSelectRSP();
            rsp.setCertType(info.getCertType());
            rsp.setCertNumber(info.getCertNumber());
            rsp.setClientId(info.getClientId());
            rsp.setClientName(info.getClientName());
            result.add(rsp);
        }
        return R.ok(result);
    }

    @Override
    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientAddSubAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT)
    public R<Void> add(NormalSpouseAddREQ req) {
        spouseService.add(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientModifySubAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT, mapperClass = NormalSpouseMapper.class)
    public R<Void> modify(NormalSpouseModifyREQ req) {
        spouseService.modify(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientRemoveSubAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT, mapperClass = NormalSpouseMapper.class)
    public R<Void> remove(NormalSpouseRemoveREQ req) {
        spouseService.remove(req.getId());
        return R.ok();
    }

    @Override
//    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientViewMainAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT)
    public R<PageR<NormalSpouseListRSP>> detail(NormalSpouseListREQ req) {
        if(!SpringContextHolder.getBean(ClientService.class).checkClientAuth(req.getClientId(), null)){
            return R.ok(PageR.of(Collections.emptyList(), 0));
        }
        PageR<NormalSpouseListRSP> data = spouseService.list(req);
        return R.ok(data);
    }
}
