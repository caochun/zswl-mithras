package cn.zswltech.mithras.basedata.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.basedata.application.BaseDataBankAccountProjectPort;
import cn.zswltech.mithras.basedata.application.BaseDataBankAccountSyncPort;
import cn.zswltech.mithras.basedata.application.BaseDataBankAccountUserPort;
import cn.zswltech.mithras.basedata.enums.BaseDataBankAccountStatusEnum;
import cn.zswltech.mithras.basedata.enums.BaseDataBankAccountTypeEnum;
import cn.zswltech.mithras.basedata.mapper.BaseDataBankAccountMapper;
import cn.zswltech.mithras.basedata.mapper.model.BaseDataBankAccount;
import cn.zswltech.mithras.dto.basedata.BaseDataBankAccountListREQ;
import cn.zswltech.mithras.dto.basedata.BaseDataBankAccountListRSP;
import cn.zswltech.mithras.dto.basedata.BaseDataBankAccountQueryREQ;
import cn.zswltech.mithras.dto.basedata.ContractAccountPayListREQ;
import cn.zswltech.mithras.dto.basedata.ContractAccountPayListRSP;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/9/15
 * @description
 */
@Service
public class BaseDataBankAccountService extends ServiceImpl<BaseDataBankAccountMapper, BaseDataBankAccount> {

    @Resource
    private BaseDataBankAccountUserPort userPort;
    @Resource
    private BaseDataBankAccountProjectPort projectPort;
    @Resource
    private BaseDataBankAccountSyncPort syncPort;

    public List<BaseDataBankAccountListRSP> list(BaseDataBankAccountListREQ req) {
        LambdaQueryWrapper<BaseDataBankAccount> query = Wrappers.lambdaQuery();
        query.like(StrUtil.isNotBlank(req.getBankName()), BaseDataBankAccount::getAccountBank, req.getBankName());
        query.ge(Objects.nonNull(req.getAccountBalanceFrom()), BaseDataBankAccount::getAccountBalance, req.getAccountBalanceFrom());
        query.le(Objects.nonNull(req.getAccountBalanceTo()), BaseDataBankAccount::getAccountBalance, req.getAccountBalanceTo());
        query.eq(Objects.nonNull(req.getIsLoan()), BaseDataBankAccount::getIsLoan, req.getIsLoan());
        if (StrUtil.isNotBlank(req.getCreateTimeFrom())) {
            query.ge(BaseModel::getCreateTime, LocalDateTimeUtil.beginOfDay(LocalDateTimeUtil.parse(req.getCreateTimeFrom(), DatePattern.NORM_DATE_PATTERN)));
        }
        if (StrUtil.isNotBlank(req.getCreateTimeTo())) {
            query.le(BaseModel::getCreateTime, LocalDateTimeUtil.endOfDay(LocalDateTimeUtil.parse(req.getCreateTimeTo(), DatePattern.NORM_DATE_PATTERN)));
        }
        if (StrUtil.isNotBlank(req.getUpdateTimeFrom())) {
            query.ge(BaseModel::getUpdateTime, LocalDateTimeUtil.beginOfDay(LocalDateTimeUtil.parse(req.getUpdateTimeFrom(), DatePattern.NORM_DATE_PATTERN)));
        }
        if (StrUtil.isNotBlank(req.getUpdateTimeTo())) {
            query.le(BaseModel::getUpdateTime, LocalDateTimeUtil.endOfDay(LocalDateTimeUtil.parse(req.getUpdateTimeTo(), DatePattern.NORM_DATE_PATTERN)));
        }
        List<BaseDataBankAccount> dbList = this.list(query);
        if (CollectionUtil.isEmpty(dbList)) {
            return Collections.emptyList();
        }
        Set<Long> userIds = dbList.stream().map(BaseModel::getCreateBy).collect(Collectors.toSet());
        Map<Long, String> userNameMap = userPort.sysUserId2Name(userIds);
        return dbList.stream().map(item -> {
            BaseDataBankAccountListRSP rsp = new BaseDataBankAccountListRSP();
            BeanUtil.copyProperties(item, rsp);
            rsp.setCreateUserId(item.getCreateBy());
            rsp.setCreateUserName(userNameMap.get(item.getCreateBy()));
            if (Objects.nonNull(item.getOpeningDate())) {
                rsp.setOpeningDate(LocalDateTimeUtil.format(item.getOpeningDate(), DatePattern.NORM_DATE_PATTERN));
            }
            if (Objects.nonNull(item.getCreateTime())) {
                rsp.setCreateTime(LocalDateTimeUtil.format(item.getCreateTime(), DatePattern.NORM_DATE_PATTERN));
            }
            if (Objects.nonNull(item.getUpdateTime())) {
                rsp.setUpdateTime(LocalDateTimeUtil.format(item.getUpdateTime(), DatePattern.NORM_DATE_PATTERN));
            }
            return rsp;
        }).collect(Collectors.toList());
    }

    public BaseDataBankAccount getDefaultAccount() {
        LambdaQueryWrapper<BaseDataBankAccount> query = Wrappers.lambdaQuery();
        query.eq(BaseDataBankAccount::getAccountNumber, "1202021219900394595");
        query.eq(BaseDataBankAccount::getAccountStatus, BaseDataBankAccountStatusEnum.NORMAL.name());
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public List<BaseDataBankAccount> accountQuery(BaseDataBankAccountQueryREQ req) {
        LambdaQueryWrapper<BaseDataBankAccount> query = Wrappers.lambdaQuery();
        query.like(StrUtil.isNotBlank(req.getAccountBank()), BaseDataBankAccount::getAccountBank, req.getAccountBank());
        query.like(StrUtil.isNotBlank(req.getAccountNumber()), BaseDataBankAccount::getAccountNumber, req.getAccountNumber());
        query.eq(StrUtil.isNotBlank(req.getAccountType()), BaseDataBankAccount::getAccountType, req.getAccountType());
        query.eq(BaseDataBankAccount::getAccountStatus, BaseDataBankAccountStatusEnum.NORMAL.name());
        List<BaseDataBankAccount> dbList = this.list(query);
        if (CollectionUtil.isEmpty(dbList)) {
            return Collections.emptyList();
        }
        return dbList;
    }

    public List<BaseDataBankAccount> listByIdsAndType(List<Long> ids, boolean isSupervision) {
        if (CollectionUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<BaseDataBankAccount> query = Wrappers.lambdaQuery();
        query.in(BaseDataBankAccount::getId, ids);
        if (isSupervision) {
            query.eq(BaseDataBankAccount::getAccountType, BaseDataBankAccountTypeEnum.SUPERVISION.name());
        } else {
            query.ne(BaseDataBankAccount::getAccountType, BaseDataBankAccountTypeEnum.SUPERVISION.name());
        }
        return this.list(query);
    }

    public List<ContractAccountPayListRSP> nameList(ContractAccountPayListREQ req) {
        return projectPort.nameList(req);
    }

    public void syncBankAccount(BaseDataBankAccount baseDataBankAccount) {
        syncPort.syncBankAccount(baseDataBankAccount);
    }

    public static String formatNumber(String number) {
        StringBuilder formatted = new StringBuilder();
        int length = number.length();
        for (int i = 0; i < length; i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append(" ");
            }
            formatted.append(number.charAt(i));
        }
        return formatted.toString();
    }
}
