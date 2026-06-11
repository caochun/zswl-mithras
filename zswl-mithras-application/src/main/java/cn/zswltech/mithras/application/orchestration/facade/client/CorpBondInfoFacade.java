package cn.zswltech.mithras.application.orchestration.facade.client;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.customer.application.client.CorpBondInfoApplicationService;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.bondinfo.*;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.customer.application.client.auth.ClientAddSubAuthCheckerNew;
import cn.zswltech.mithras.customer.application.client.auth.ClientModifySubAuthCheckerNew;
import cn.zswltech.mithras.customer.application.client.auth.ClientRemoveSubAuthCheckerNew;
import cn.zswltech.mithras.customer.application.client.auth.ClientViewMainAuthCheckerNew;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.customer.mapper.corp.CorpBondInfoMapper;
import cn.zswltech.mithras.customer.model.client.CorpBondInfo;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.customer.application.client.CorpBondInfoService;
import cn.zswltech.mithras.customer.versioning.CorpBondInfoLibService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * @author luyi
 */
@Service
public class CorpBondInfoFacade implements CorpBondInfoApplicationService {
    @Resource
    private CorpBondInfoService bondInfoService;
    @Resource
    private CorpBondInfoLibService bondInfoLibService;

    @Override
//    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientAddSubAuthCheckerNew.class, businessModule = "CLIENT")
    public R<Void> add(CorpBondInfoAddREQ req) {
        bondInfoService.add(req);
        return R.ok();
    }

    @Override
//    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientModifySubAuthCheckerNew.class, businessModule = "CLIENT", mapperClass = CorpBondInfoMapper.class)
    public R<Void> modify(CorpBondInfoModifyREQ req) {
        bondInfoService.modify(req);
        return R.ok();
    }

    @Override
//    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientRemoveSubAuthCheckerNew.class, businessModule = "CLIENT", mapperClass = CorpBondInfoMapper.class)
    public R<Void> remove(CorpBondInfoRemoveREQ req) {
        bondInfoService.remove(req);
        return R.ok();
    }

    @Override
//    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientViewMainAuthCheckerNew.class, businessModule = "CLIENT")
    public R<PageR<CorpBondInfoListRSP>> list(CorpBondInfoListREQ req) {
//        if(!SpringContextHolder.getBean(ClientService.class).checkClientAuth(req.getClientId(), null)){
//            return R.ok(PageR.of(Collections.emptyList(), 0));
//        }
        if (StringUtils.isBlank(req.getVersion())) {
            Page<CorpBondInfo> data = bondInfoService.list(req);
            List<CorpBondInfoListRSP> list = BeanUtil.copyToList(data.getRecords(), CorpBondInfoListRSP.class);
            return R.ok(PageR.of(list, data.getTotal(),
                    data.getPages(),
                    data.getCurrent(),
                    data.getSize()));
        } else {
            return R.ok(bondInfoLibService.list(req));
        }
    }
}
