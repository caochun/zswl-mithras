package cn.zswltech.mithras.service.controller.basedata;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.api.basedata.BaseDataBankAccountApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.basedata.*;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.common.enums.RecordStatus;
import cn.zswltech.mithras.service.mapper.model.basedata.BaseDataBankAccount;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.basedata.BaseDataBankAccountService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import liquibase.pro.packaged.A;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;

/**
 * @author dingqi
 * @date 2022/9/15
 * @description
 */
@RestController
public class BaseDataBankAccountController implements BaseDataBankAccountApi {
    @Resource
    private BaseDataBankAccountService baseDataBankAccountService;

    @Override
    public R<Long> save(@Valid BaseDataBankAccountSaveREQ baseDataBankAccountSaveREQ) {
        BaseDataBankAccount baseDataBankAccount = new BaseDataBankAccount();
        BeanUtil.copyProperties(baseDataBankAccountSaveREQ, baseDataBankAccount, false);
        // 去掉空格
        baseDataBankAccount.setAccountNumber(baseDataBankAccount.getAccountNumber().replace(" ", ""));
        if (Objects.isNull(baseDataBankAccount.getId())) {
            baseDataBankAccountService.save(baseDataBankAccount);
        } else {
            baseDataBankAccountService.getBaseMapper().updateAnnotationIncludeNullById(baseDataBankAccount);
        }
        //同步到其他账户
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
        if(baseDataBankAccount == null){
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
