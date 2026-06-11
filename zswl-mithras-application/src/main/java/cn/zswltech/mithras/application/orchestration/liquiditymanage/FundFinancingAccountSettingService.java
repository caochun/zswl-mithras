package cn.zswltech.mithras.application.orchestration.liquiditymanage;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.dto.basedata.BaseDataBankAccountQueryREQ;
import cn.zswltech.mithras.dto.liquiditymanage.base.*;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.fund.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingAccountTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.directfinancing.mapper.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.basedata.mapper.model.BaseDataBankAccount;
import cn.zswltech.mithras.fund.mapper.model.FundOrganization;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingPayAccount;
import cn.zswltech.mithras.liquidity.mapper.model.FundFinancingAccountSetting;
import cn.zswltech.mithras.liquidity.mapper.FundFinancingAccountSettingMapper;
import cn.zswltech.mithras.liquidity.mapper.model.dto.AccountSettingListQueryDTO;
import cn.zswltech.mithras.liquidity.mapper.model.dto.AccountSettingListResultDTO;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.basedata.service.BaseDataBankAccountService;
import cn.zswltech.mithras.fund.application.organization.FundOrganizationService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.fund.application.financing.FundFinancingPayAccountService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 回款账户配置表 服务实现类
 * </p>
 *
 * @author chenyifei
 * @since 2024-12-12
 */
@Service
public class FundFinancingAccountSettingService extends ServiceImpl<FundFinancingAccountSettingMapper, FundFinancingAccountSetting> {

    @Resource
    private FundFinancingAccountSettingMapper accountSettingMapper;
    @Resource
    private BaseDataBankAccountService baseDataBankAccountService;
    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundDirectFinancingBaseInfoService directFinancingBaseInfoService;
    @Resource
    private FundFinancingPayAccountService financingPayAccountService;
    @Resource
    private FundOrganizationService organizationService;

    @XxlJob("accountSettingCalculate")
    @Transactional(rollbackFor = Throwable.class)
    public void init (){
        List<FundFinancingAccountSetting> list = this.list();
        Map<Long, Map<Long, FundFinancingAccountSetting>> inDirectOriginMap = new HashMap<>();
        Map<Long, Map<Long, FundFinancingAccountSetting>> directOriginMap = new HashMap<>();
        if(CollectionUtil.isNotEmpty(list)){
           inDirectOriginMap = list.stream().filter(f -> f.getFinancingType() == null).collect(Collectors.groupingBy(FundFinancingAccountSetting::getFinancingId,
                    Collectors.toMap(FundFinancingAccountSetting::getAccountId, Function.identity(), (m1,m2) -> m1)));
           directOriginMap = list.stream().filter(f -> Objects.equals(f.getFinancingType(), FinancingTypeEnum.DIRECT.name())).collect(Collectors.groupingBy(FundFinancingAccountSetting::getFinancingId,
                    Collectors.toMap(FundFinancingAccountSetting::getAccountId, Function.identity(), (m1,m2) -> m1)));
        }

        List<FundFinancingAccountSetting> accountSettingList = new ArrayList<>();

        Map<Long, BaseDataBankAccount> bankAccountMap = baseDataBankAccountService.accountQuery(new BaseDataBankAccountQueryREQ()).stream().collect(Collectors.toMap(BaseDataBankAccount::getId, Function.identity()));
        List<FundFinancingBaseInfo> financingBaseInfoList = financingBaseInfoService.list(Wrappers.<FundFinancingBaseInfo>lambdaQuery().eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name()));
        if(CollectionUtil.isNotEmpty(financingBaseInfoList)){
            Map<Long, List<FundFinancingPayAccount>> payAccountMap = Optional.ofNullable(financingPayAccountService.listByFinancingIds(financingBaseInfoList
                    .stream().map(FundFinancingBaseInfo::getId).collect(Collectors.toList()))).map(m -> m.stream().collect(Collectors.groupingBy(FundFinancingPayAccount::getFinancingId))).orElse(Collections.emptyMap());
            Map<Long, Map<Long, FundFinancingAccountSetting>> finalInDirectOriginMap = inDirectOriginMap;
            List<FundFinancingAccountSetting> inDirectAccountSettingList = payAccountMap.values().stream().flatMap(Collection::stream).map(item -> {
                if(item.getAccountCategory() == null){
                    return null;
                }
                BaseDataBankAccount dataBankAccount = bankAccountMap.get(item.getBankAccountId());
                if (dataBankAccount == null) {
                    return null;
                }
                FundFinancingAccountSetting accountSetting = Optional.ofNullable(finalInDirectOriginMap.getOrDefault(item.getFinancingId(), new HashMap<>()).get(item.getBankAccountId()))
                        .orElse(new FundFinancingAccountSetting());
                if(!Objects.equals(accountSetting.getAccountOriginId(), accountSetting.getAccountId())){
                    return null;
                }
                accountSetting.setAccountId(dataBankAccount.getId());
                accountSetting.setAccountOriginId(dataBankAccount.getId());
                accountSetting.setAccountCategory(item.getAccountCategory());
                accountSetting.setAccountType(dataBankAccount.getAccountType());
                accountSetting.setAccountBank(dataBankAccount.getAccountBank());
                accountSetting.setAccountNumber(dataBankAccount.getAccountNumber());
                accountSetting.setFinancingId(item.getFinancingId());
                accountSetting.setFinancingType(null);
                accountSetting.setSimulateSettle(Boolean.FALSE);
                return accountSetting;
            }).filter(Objects::nonNull).collect(Collectors.toList());
            accountSettingList.addAll(inDirectAccountSettingList);
        }

        List<FundDirectFinancingBaseInfo> directFinancingBaseInfoList = directFinancingBaseInfoService.list(Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery()
                .eq(FundDirectFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name()));
        if(CollectionUtil.isNotEmpty(directFinancingBaseInfoList)) {
            BaseDataBankAccount defaultAccount = baseDataBankAccountService.getDefaultAccount();
            Map<Long, Map<Long, FundFinancingAccountSetting>> finalDirectOriginMap = directOriginMap;
            List<FundFinancingAccountSetting> directAccountSettingList = directFinancingBaseInfoList.stream().map(item -> {
                FundFinancingAccountSetting accountSetting = Optional.ofNullable(finalDirectOriginMap.getOrDefault(item.getId(), new HashMap<>()).get(defaultAccount.getId()))
                        .orElse(new FundFinancingAccountSetting());
                if(!Objects.equals(accountSetting.getAccountOriginId(), accountSetting.getAccountId())){
                    return null;
                }
                accountSetting.setAccountId(defaultAccount.getId());
                accountSetting.setAccountOriginId(defaultAccount.getId());
                accountSetting.setAccountCategory(FundFinancingAccountTypeEnum.REPAY_PRINCIPAL_AND_INTEREST.name());
                accountSetting.setAccountType(defaultAccount.getAccountType());
                accountSetting.setAccountBank(defaultAccount.getAccountBank());
                accountSetting.setAccountNumber(defaultAccount.getAccountNumber());
                accountSetting.setFinancingId(item.getId());
                accountSetting.setFinancingType(FinancingTypeEnum.DIRECT.name());
                accountSetting.setSimulateSettle(Boolean.FALSE);
                return accountSetting;
            }).filter(Objects::nonNull).collect(Collectors.toList());
            accountSettingList.addAll(directAccountSettingList);
        }

        Map<Long, Map<Long, FundFinancingAccountSetting>> newAccountSettingMap = accountSettingList.stream().collect(Collectors.groupingBy(FundFinancingAccountSetting::getFinancingId,
                Collectors.toMap(FundFinancingAccountSetting::getAccountId, Function.identity(), (m1, m2) -> m1)));
        List<FundFinancingAccountSetting> deleteList = new ArrayList<>();
        for (Map.Entry<Long, Map<Long, FundFinancingAccountSetting>> entry : newAccountSettingMap.entrySet()) {
            Map<Long, FundFinancingAccountSetting> settingMap = Optional.ofNullable(inDirectOriginMap.get(entry.getKey())).orElse(directOriginMap.get(entry.getKey()));
            if(CollectionUtil.isEmpty(settingMap)){
                continue;
            }
            // 筛选原数据中存在，但现数据不存在的账户 - 表示该合同还款账户已变更，要删除原账户
            List<FundFinancingAccountSetting> collect = settingMap.entrySet().stream().filter(f -> {
                return Objects.isNull(entry.getValue().get(f.getKey()));
            }).map(Map.Entry::getValue).collect(Collectors.toList());

            if(CollectionUtil.isNotEmpty(collect)) {
                deleteList.addAll(collect);
            }
        }

        if(CollectionUtil.isNotEmpty(deleteList)){
            removeByIds(deleteList.stream().map(FundFinancingAccountSetting::getId).collect(Collectors.toList()));
        }
        List<FundFinancingAccountSetting> addList = accountSettingList.stream().filter(f -> Objects.isNull(f.getId())).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(addList)) {
            this.saveBatch(addList);
        }
        List<FundFinancingAccountSetting> updateList = accountSettingList.stream().filter(f -> Objects.nonNull(f.getId())).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(updateList)) {
            this.updateBatchById(updateList);
        }
    }

    public List<AccountSettingListRSP> accountSettingList(AccountSettingListREQ req) {
        AccountSettingListQueryDTO query = BeanUtil.copyProperties(req, AccountSettingListQueryDTO.class);
        List<AccountSettingListResultDTO> resultList = accountSettingMapper.queryList(query);
        List<AccountSettingListRSP> rspList = null;
        if(CollectionUtil.isNotEmpty(resultList)){
            Map<Long, List<FundOrganization>> orgMap = organizationService.getBatchByFinancingId(resultList.stream().map(AccountSettingListResultDTO::getFinancingId).collect(Collectors.toList()));
            rspList = resultList.stream().map(item -> {
                AccountSettingListRSP rsp = BeanUtil.copyProperties(item, AccountSettingListRSP.class);
                if(Objects.equals(FinancingTypeEnum.INDIRECT.name(), item.getFinancingType())) {
                    List<FundOrganization> organizationList = orgMap.get(item.getFinancingId());
                    if(CollectionUtil.isNotEmpty(organizationList)) {
                        rsp.setOrganizationId(organizationList.stream().map(FundOrganization::getId).collect(Collectors.toList()));
                        rsp.setOrganizationName(organizationList.stream().map(FundOrganization::getOrganizationName).collect(Collectors.toList()));
                    }
                }else {
                    rsp.setOrganizationName(Collections.singletonList(item.getProductName()));
                }
                rsp.setIsEdit(!Objects.equals(item.getAccountId(), item.getAccountOriginId()));
                return rsp;
            }).collect(Collectors.toList());
        }
        return rspList;
    }


    public AccountSettingModifyRSP accountSettingModify(AccountSettingModifyREQ req) {
        if(req != null && CollectionUtil.isNotEmpty(req.getList())) {
            for (AccountSettingModifyREQ.AccountSettingDetail detailReq : req.getList()) {
                if (detailReq.getSimulateSettle() != null && detailReq.getSimulateSettle()) {
                    Assert.notNull(detailReq.getSettleTime(), () -> MithrasException.newException("模拟结清时，模拟结清日期必填"));
                    Assert.notNull(detailReq.getSettleAmount(), () -> MithrasException.newException("模拟结清时，模拟结清金额必填"));
                }
            }
            List<FundFinancingAccountSetting> fundFinancingAccountSettingList = req.getList().stream().map(item -> {
                return BeanUtil.copyProperties(item, FundFinancingAccountSetting.class);
            }).collect(Collectors.toList());
            this.updateBatchById(fundFinancingAccountSettingList);
        }
        return null;
    }

    public AccountSettingRestoreRSP accountSettingRestore(AccountSettingRestoreREQ req) {
        List<FundFinancingAccountSetting> settingOriginList = this.list(Wrappers.<FundFinancingAccountSetting>lambdaQuery()
                .in(CollectionUtil.isNotEmpty(req.getIdList()), FundFinancingAccountSetting::getId, req.getIdList()));
        if(CollectionUtil.isEmpty(settingOriginList)){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        Map<Long, BaseDataBankAccount> accountMap = baseDataBankAccountService.listByIds(settingOriginList.stream().map(FundFinancingAccountSetting::getAccountOriginId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(BaseDataBankAccount::getId, Function.identity()));
        List<FundFinancingAccountSetting> needUpdateAccountList = settingOriginList.stream().filter(f -> !Objects.equals(f.getAccountId(), f.getAccountOriginId()))
                .peek(item -> {
                    BaseDataBankAccount dataBankAccount = accountMap.get(item.getAccountOriginId());
                    item.setAccountId(dataBankAccount.getId());
                    item.setAccountBank(dataBankAccount.getAccountBank());
                    item.setAccountNumber(dataBankAccount.getAccountNumber());
                }).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(needUpdateAccountList)){
            this.updateBatchById(needUpdateAccountList);
        }
        return null;
    }
}
