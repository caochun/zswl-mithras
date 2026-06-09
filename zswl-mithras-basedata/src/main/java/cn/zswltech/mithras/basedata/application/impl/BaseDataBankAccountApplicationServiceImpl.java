package cn.zswltech.mithras.basedata.application.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.basedata.application.BaseDataBankAccountApplicationService;
import cn.zswltech.mithras.basedata.mapper.model.BaseDataBankAccount;
import cn.zswltech.mithras.basedata.service.BaseDataBankAccountService;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.basedata.BaseDataBankAccountDetailRSP;
import cn.zswltech.mithras.dto.basedata.BaseDataBankAccountListREQ;
import cn.zswltech.mithras.dto.basedata.BaseDataBankAccountListRSP;
import cn.zswltech.mithras.dto.basedata.BaseDataBankAccountSaveREQ;
import cn.zswltech.mithras.dto.basedata.ContractAccountPayListREQ;
import cn.zswltech.mithras.dto.basedata.ContractAccountPayListRSP;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@Service
public class BaseDataBankAccountApplicationServiceImpl implements BaseDataBankAccountApplicationService {

    @Resource
    private BaseDataBankAccountService baseDataBankAccountService;

    @Override
    public R<Long> save(@Valid BaseDataBankAccountSaveREQ baseDataBankAccountSaveREQ) {
        BaseDataBankAccount baseDataBankAccount = new BaseDataBankAccount();
        BeanUtil.copyProperties(baseDataBankAccountSaveREQ, baseDataBankAccount, false);
        baseDataBankAccount.setAccountNumber(baseDataBankAccount.getAccountNumber().replace(" ", ""));
        if (Objects.isNull(baseDataBankAccount.getId())) {
            baseDataBankAccountService.save(baseDataBankAccount);
        } else {
            baseDataBankAccountService.getBaseMapper().updateAnnotationIncludeNullById(baseDataBankAccount);
        }
        baseDataBankAccountService.syncBankAccount(baseDataBankAccount);
        return R.ok(baseDataBankAccount.getId());
    }

    @Override
    public R<List<BaseDataBankAccountListRSP>> list(@Valid BaseDataBankAccountListREQ req) {
        return R.ok(baseDataBankAccountService.list(req));
    }

    @Override
    public R<BaseDataBankAccountDetailRSP> detail(SinglePkREQ req) {
        BaseDataBankAccount baseDataBankAccount = baseDataBankAccountService.getById(req.getId());
        Assert.notNull(baseDataBankAccount, () -> MithrasException.newException("数据不存在"));
        BaseDataBankAccountDetailRSP rsp = BeanUtil.copyProperties(baseDataBankAccount, BaseDataBankAccountDetailRSP.class);
        rsp.setOpeningDate(LocalDateTimeUtil.format(baseDataBankAccount.getOpeningDate(), DatePattern.NORM_DATE_PATTERN));
        return R.ok(rsp);
    }

    @Override
    public R<Void> delete(@Valid SinglePkREQ singlePkREQ) {
        baseDataBankAccountService.removeById(singlePkREQ.getId());
        return R.ok();
    }

    @Override
    public R<List<ContractAccountPayListRSP>> list(ContractAccountPayListREQ req) {
        return R.ok(baseDataBankAccountService.nameList(req));
    }

    @Override
    public R<BaseDataBankAccountDetailRSP> init() {
        BaseDataBankAccount baseDataBankAccount = baseDataBankAccountService.getOne(Wrappers.<BaseDataBankAccount>lambdaQuery()
                .eq(BaseDataBankAccount::getAccountNumber, "1202021219900394595"));
        if (baseDataBankAccount == null) {
            baseDataBankAccount = baseDataBankAccountService.getOne(Wrappers.<BaseDataBankAccount>lambdaQuery()
                    .eq(BaseDataBankAccount::getAccountNumber, "1202 0212 1990 0394 595"));
        }
        Assert.notNull(baseDataBankAccount, () -> MithrasException.newException("数据不存在"));
        BaseDataBankAccountDetailRSP rsp = BeanUtil.copyProperties(baseDataBankAccount, BaseDataBankAccountDetailRSP.class);
        rsp.setOpeningDate(LocalDateTimeUtil.format(baseDataBankAccount.getOpeningDate(), DatePattern.NORM_DATE_PATTERN));
        rsp.setAccountNumber("1202 0212 1990 0394 595");
        return R.ok(rsp);
    }
}
