package cn.zswltech.mithras.application.orchestration.adapter.liquidity;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.basedata.persistence.mapper.BaseDataBankAccountMapper;
import cn.zswltech.mithras.basedata.persistence.model.BaseDataBankAccount;
import cn.zswltech.mithras.basedata.service.BaseDataSpecialDateService;
import cn.zswltech.mithras.dto.liquiditymanage.fundtransfer.FundTransferBankAccountListREQ;
import cn.zswltech.mithras.dto.liquiditymanage.fundtransfer.FundTransferBankAccountListRSP;
import cn.zswltech.mithras.liquidity.enums.LiquidityBankAccountType;
import cn.zswltech.mithras.liquidity.application.port.FundTransferBaseDataPort;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FundTransferBaseDataPortAdapter implements FundTransferBaseDataPort {

    @Resource
    private BaseDataSpecialDateService baseDataSpecialDateService;
    @Resource
    private BaseDataBankAccountMapper baseDataBankAccountMapper;

    @Override
    public long calculateWorkDays(LocalDate from, LocalDate to) {
        return baseDataSpecialDateService.calculateWorkDays(from, to);
    }

    @Override
    public List<FundTransferBankAccountListRSP> listSupervisionAccounts(FundTransferBankAccountListREQ req) {
        List<BaseDataBankAccount> dbList = baseDataBankAccountMapper.selectList(Wrappers.<BaseDataBankAccount>lambdaQuery()
                .eq(BaseDataBankAccount::getAccountType, LiquidityBankAccountType.SUPERVISION.name())
                .like(StrUtil.isNotBlank(req.getBankName()), BaseDataBankAccount::getAccountBank, req.getBankName()));
        if (CollectionUtil.isEmpty(dbList)) {
            return Collections.emptyList();
        }
        List<BaseDataBankAccount> res = new ArrayList<>();
        Set<String> accountBankSet = new HashSet<>();
        for (BaseDataBankAccount bankAccount : dbList) {
            if (accountBankSet.contains(bankAccount.getAccountBank())) {
                continue;
            }
            res.add(bankAccount);
            accountBankSet.add(bankAccount.getAccountBank());
        }
        return res.stream().map(item -> {
            FundTransferBankAccountListRSP rsp = new FundTransferBankAccountListRSP();
            BeanUtil.copyProperties(item, rsp);
            return rsp;
        }).collect(Collectors.toList());
    }
}
