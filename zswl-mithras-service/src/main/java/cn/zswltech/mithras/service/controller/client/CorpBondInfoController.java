package cn.zswltech.mithras.service.controller.client;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.client.CorpBondInfoApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.bondinfo.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.client.ClientAddSubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientModifySubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientRemoveSubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientViewMainAuthCheckerNew;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpBondInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpBondInfo;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.CorpBondInfoService;
import cn.zswltech.mithras.customer.application.lib.client.CorpBondInfoLibService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author luyi
 */
@RestController
public class CorpBondInfoController implements CorpBondInfoApi {
    @Resource
    private CorpBondInfoService bondInfoService;
    @Resource
    private CorpBondInfoLibService bondInfoLibService;

    @Override
//    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientAddSubAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT)
    public R<Void> add(CorpBondInfoAddREQ req) {
        bondInfoService.add(req);
        return R.ok();
    }

    @Override
//    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientModifySubAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT, mapperClass = CorpBondInfoMapper.class)
    public R<Void> modify(CorpBondInfoModifyREQ req) {
        bondInfoService.modify(req);
        return R.ok();
    }

    @Override
//    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientRemoveSubAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT, mapperClass = CorpBondInfoMapper.class)
    public R<Void> remove(CorpBondInfoRemoveREQ req) {
        bondInfoService.remove(req);
        return R.ok();
    }

    @Override
//    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientViewMainAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT)
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
