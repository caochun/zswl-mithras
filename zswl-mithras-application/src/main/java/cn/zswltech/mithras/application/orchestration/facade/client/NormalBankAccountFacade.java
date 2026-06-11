package cn.zswltech.mithras.application.orchestration.facade.client;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.customer.application.client.NormalBankAccountApplicationService;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.normal.*;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.customer.application.client.auth.ClientAddSubAuthCheckerNew;
import cn.zswltech.mithras.customer.application.client.auth.ClientModifySubAuthCheckerNew;
import cn.zswltech.mithras.customer.application.client.auth.ClientRemoveSubAuthCheckerNew;
import cn.zswltech.mithras.customer.application.client.auth.ClientViewMainAuthCheckerNew;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.customer.application.client.NormalBankAccountService;
import cn.zswltech.mithras.customer.mapper.model.client.NormalBankAccount;
import cn.zswltech.mithras.customer.mapper.normal.NormalBankAccountMapper;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.customer.application.lib.client.NormalBankAccountLibService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * @author junke
 */
@Service
public class NormalBankAccountFacade implements NormalBankAccountApplicationService {

    @Resource
    private NormalBankAccountService bankAccountService;
    @Resource
    private NormalBankAccountLibService bankAccountLibService;

    @Override
    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientAddSubAuthCheckerNew.class, businessModule = "CLIENT")
    public R<Void> add(NormalBankAccountAddREQ req) {
        bankAccountService.add(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientModifySubAuthCheckerNew.class, businessModule = "CLIENT", mapperClass = NormalBankAccountMapper.class)
    public R<Void> modify(NormalBankAccountModifyREQ req) {
        bankAccountService.modify(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientRemoveSubAuthCheckerNew.class, businessModule = "CLIENT", mapperClass = NormalBankAccountMapper.class)
    public R<Void> remove(NormalBankAccountRemoveREQ req) {
        bankAccountService.remove(req.getId());
        return R.ok();
    }

    @Override
//    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientViewMainAuthCheckerNew.class, businessModule = "CLIENT")
    public R<PageR<NormalBankAccountListRSP>> list(NormalBankAccountListREQ req) {
        if(!SpringContextHolder.getBean(ClientService.class).checkClientAuth(req.getClientId(), null)){
            return R.ok(PageR.of(Collections.emptyList(), 0));
        }
        if (StringUtils.isBlank(req.getVersion())) {
            Page<NormalBankAccount> data = bankAccountService.list(req);
            List<NormalBankAccountListRSP> list = BeanUtil.copyToList(data.getRecords(), NormalBankAccountListRSP.class);
            return R.ok(PageR.of(list, data.getTotal(),
                    data.getPages(),
                    data.getCurrent(),
                    data.getSize()));
        } else {
            return R.ok(bankAccountLibService.list(req));
        }
    }
}
