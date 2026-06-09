package cn.zswltech.mithras.service.application.client;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.customer.application.client.api.CorpBankAccountApplicationService;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.bankaccount.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.client.ClientAddSubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientModifySubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientRemoveSubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientViewMainAuthCheckerNew;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpBankAccountMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpBankAccount;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpBankAccountLib;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.customer.application.client.CorpBankAccountService;
import cn.zswltech.mithras.customer.application.lib.client.CorpBankAccountLibService;
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
public class CorpBankAccountFacade implements CorpBankAccountApplicationService {

    @Resource
    private CorpBankAccountService bankAccountService;
    @Resource
    private CorpBankAccountLibService bankAccountLibService;

    @Override
//    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientAddSubAuthCheckerNew.class, businessModule = "CLIENT")
    public R<Void> add(CorpBankAccountAddREQ req) {
        bankAccountService.add(req);
        return R.ok();
    }

    @Override
//    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientModifySubAuthCheckerNew.class, businessModule = "CLIENT", mapperClass = CorpBankAccountMapper.class)
    public R<Void> modify(CorpBankAccountModifyREQ req) {
        bankAccountService.modify(req);
        return R.ok();
    }

    @Override
//    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientRemoveSubAuthCheckerNew.class, businessModule = "CLIENT", mapperClass =
//            CorpBankAccountMapper.class)
    public R<Void> remove(CorpBankAccountRemoveREQ req) {
        bankAccountService.remove(req.getId());
        return R.ok();
    }

    @Override
//    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientViewMainAuthCheckerNew.class, businessModule = "CLIENT")
    public R<PageR<CorpBankAccountListRSP>> list(CorpBankAccountListREQ req) {
//        if(!SpringContextHolder.getBean(ClientService.class).checkClientAuth(req.getClientId(), null)){
//            return R.ok(PageR.of(Collections.emptyList(), 0));
//        }
        if (StringUtils.isBlank(req.getVersion())) {
            Page<CorpBankAccount> data = bankAccountService.list(req);
            List<CorpBankAccountListRSP> list = BeanUtil.copyToList(data.getRecords(), CorpBankAccountListRSP.class);
            return R.ok(PageR.of(list, data.getTotal(),
                    data.getPages(),
                    data.getCurrent(),
                    data.getSize()));
        } else {
            return R.ok(bankAccountLibService.list(req));
        }

    }

    @Override
    public R<PageR<CorpBankAccountListRSP>> versionedList(CorpVersionedBankAccountListREQ req) {
        Page<CorpBankAccountLib> data = bankAccountService.versionedList(req);
        List<CorpBankAccountListRSP> list = BeanUtil.copyToList(data.getRecords(), CorpBankAccountListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }
}
