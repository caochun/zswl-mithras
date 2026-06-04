package cn.zswltech.mithras.service.service.basedata;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.basedata.*;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.basedata.enums.BaseDataBankAccountStatusEnum;
import cn.zswltech.mithras.basedata.enums.BaseDataBankAccountTypeEnum;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingRepayActualService;
import cn.zswltech.mithras.basedata.mapper.BaseDataBankAccountMapper;
import cn.zswltech.mithras.collection.mapper.CollectionRecordInfoMapper;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.basedata.mapper.model.BaseDataBankAccount;
import cn.zswltech.mithras.collection.mapper.model.CollectionRecordInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAccount;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPayAccount;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundRepayAccount;
import cn.zswltech.mithras.liquiditymanage.mapper.model.AccountBalanceBaseInfo;
import cn.zswltech.mithras.liquiditymanage.mapper.model.FundFinancingAccountSetting;
import cn.zswltech.mithras.margin.mapper.model.MarginRecordInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetailUnconfirmed;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.service.service.collection.CollectionRecordInfoService;
import cn.zswltech.mithras.contract.core.application.ContractAccountService;
import cn.zswltech.mithras.fund.application.financing.FundFinancingPayAccountService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundRepayAccountService;
import cn.zswltech.mithras.service.service.liquiditymanage.FundFinancingAccountSettingService;
import cn.zswltech.mithras.margin.service.MarginRecordService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailUnconfirmedService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/9/15
 * @description
 */
@Service
public class BaseDataBankAccountService extends ServiceImpl<BaseDataBankAccountMapper, BaseDataBankAccount> {
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ProjReviewBaseInfoService reviewBaseInfoService;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private FundDirectFinancingRepayActualService fundDirectFinancingRepayActualService;
    @Resource
    private FundDirectFinancingPledgeInfoService directFinancingPledgeInfoService;
    @Resource
    private FundFinancingAccountSettingService accountSettingService;
    @Resource
    private FundFinancingPayAccountService fundFinancingPayAccountService;
    @Resource
    private FundRepayAccountService fundRepayAccountService;
    @Resource
    private PaymentActualDetailUnconfirmedService actualDetailUnconfirmedService;
    @Resource
    private ContractAccountService contractAccountService;
    @Resource
    private FundFinancingPledgeInfoService fundFinancingPledgeInfoService;
    @Resource
    private CollectionRecordInfoMapper collectionRecordInfoMapper;
    @Resource
    private CollectionRecordInfoService collectionRecordInfoService;
    @Resource
    private MarginRecordService marginRecordService;

    public List<BaseDataBankAccountListRSP> list(BaseDataBankAccountListREQ req) {
        // 查询条件
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
        // 返回值
        Set<Long> userIds = dbList.stream().map(BaseModel::getCreateBy).collect(Collectors.toSet());
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(userIds);
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

    public BaseDataBankAccount getDefaultAccount(){
        LambdaQueryWrapper<BaseDataBankAccount> query = Wrappers.lambdaQuery();
        query.eq(BaseDataBankAccount::getAccountNumber, "1202021219900394595");
        query.eq(BaseDataBankAccount::getAccountStatus, BaseDataBankAccountStatusEnum.NORMAL.name());
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }


    public List<BaseDataBankAccount> accountQuery(BaseDataBankAccountQueryREQ req) {
        // 查询条件
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

    /**
     * 根据id和是否监管查询账户列表
     *
     * @param ids           账户id
     * @param isSupervision 是否监管
     * @return List<BaseDataBankAccount>
     */

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
        List<ProjReviewBaseInfo> projReviewBaseInfoList = projReviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .eq(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.TAKE_EFFECT.name())
                .eq(ProjReviewBaseInfo::getProjCode, req.getProjCode()).orderByDesc(ProjReviewBaseInfo::getId));
        if(projReviewBaseInfoList == null || projReviewBaseInfoList.isEmpty()){
            return new ArrayList<>();
        }
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoList.get(0);
        ProjReviewBaseInfoDetailRSP rsp = reviewBaseInfoService.detail(projReviewBaseInfo.getId(), null);
        if (rsp == null) {
            return new ArrayList<>();
        }
        Set<String> clientNames = new HashSet<>();
        if (rsp.getLesseeInfo() != null) {
            for (ClientInfo clientInfo : rsp.getLesseeInfo()) {
                if (StringUtils.isNotBlank(clientInfo.getClientName())) {
                    clientNames.add(clientInfo.getClientName());
                }
            }
        }
        /*if (rsp.getGuaranteeInfo() != null) {
            for (ClientInfo clientInfo : rsp.getGuaranteeInfo()) {
                if (StringUtils.isNotBlank(clientInfo.getClientName())) {
                    clientNames.add(clientInfo.getClientName());
                }
            }
        }
        if (rsp.getPledgorInfo() != null) {
            for (ClientInfo clientInfo : rsp.getPledgorInfo()) {
                if (StringUtils.isNotBlank(clientInfo.getClientName())) {
                    clientNames.add(clientInfo.getClientName());
                }
            }
        }
        if (rsp.getMortgagorInfo() != null) {
            for (ClientInfo clientInfo : rsp.getMortgagorInfo()) {
                if (StringUtils.isNotBlank(clientInfo.getClientName())) {
                    clientNames.add(clientInfo.getClientName());
                }
            }
        }*/
        List<ContractAccountPayListRSP> res = new ArrayList<>();
        for (String accountName : clientNames) {
            ContractAccountPayListRSP accountPayListRSP = new ContractAccountPayListRSP();
            accountPayListRSP.setAccountName(accountName);
            res.add(accountPayListRSP);
        }
        return res;
    }
    /**
     * 同步修改的账户
     *
     * @param baseDataBankAccount           账户信息
     * @return
     */
    public void syncBankAccount(BaseDataBankAccount baseDataBankAccount) {
        //账户名称
        String accountName = baseDataBankAccount.getAccountName();
        //银行账号
        String accountNumber = baseDataBankAccount.getAccountNumber();
        //开户银行
        String accountBank = baseDataBankAccount.getAccountBank();
        List<String> accountNumberList = new ArrayList<>();
        accountNumberList.add(accountNumber);
        String accountNumberSpace = formatNumber(accountNumber);
        accountNumberList.add(accountNumberSpace);
        //资金管理--直融管理--关联合同明细
        //资金管理--间融管理--关联合同明细
        directFinancingPledgeInfoService.update(Wrappers.<FundDirectFinancingPledgeInfo>lambdaUpdate()
                .in(FundDirectFinancingPledgeInfo::getAccountNumber, accountNumberList)
                .set(FundDirectFinancingPledgeInfo::getAccountName, accountName)
                .set(FundDirectFinancingPledgeInfo::getAccountBank, accountBank));
        //资金管理--流动性管理--预测参数配置--编辑--还款银行/还款账号
        accountSettingService.update(Wrappers.<FundFinancingAccountSetting>lambdaUpdate()
                .in(FundFinancingAccountSetting::getAccountNumber, accountNumberList)
                .set(FundFinancingAccountSetting::getAccountBank, accountBank));
        //资金管理--间融管理--新增融资--我司还款账户--新增
        fundFinancingPayAccountService.update(Wrappers.<FundFinancingPayAccount>lambdaUpdate()
                .in(FundFinancingPayAccount::getAccountNumber, accountNumberList)
                .set(FundFinancingPayAccount::getAccountBank, accountBank));
        //资金管理--还本付息--还款账户
        fundRepayAccountService.update(Wrappers.<FundRepayAccount>lambdaUpdate()
                .in(FundRepayAccount::getAccountNumber, accountNumberList)
                .set(FundRepayAccount::getAccountBank, accountBank));
        //收付款管理--合同收付款--发起租金支付通知的流程
        //收付款管理--合同收付款--发起租金支付通知的流程--点击”客户名称“超链接
        //文档不处理

        //收付款管理--付款核销--新增/编辑我方银行账户名/开户行
        actualDetailUnconfirmedService.update(Wrappers.<PaymentActualDetailUnconfirmed>lambdaUpdate()
                .in(PaymentActualDetailUnconfirmed::getOurAccountNumber, accountNumberList)
                .set(PaymentActualDetailUnconfirmed::getOurAccountName, accountName)
                .set(PaymentActualDetailUnconfirmed::getOurAccountBank, accountBank));
        //合同管理-收款账户-新增-收款方为甲方-账户名称/开户行
        contractAccountService.update(Wrappers.<ContractAccount>lambdaUpdate()
                .in(ContractAccount::getAccountNum, accountNumberList)
                .set(ContractAccount::getAccountName, accountName)
                .set(ContractAccount::getAccountAddress, accountBank));
        //登录资金管理部角色下用户--点击“资金管理”--点击“流动性管理”--点击“流动性管理”--租金流入
        //通过id从基本表获取、无需修改

        //登录资金管理部角色下用户--点击“资金管理”--点击“流动性管理”--点击“资金日报”--还本付息--点击“融资编号”超链接--我司还款账户
        //同资金管理--间融管理--新增融资--我司还款账户--新增为一张表，无需修改

        //登录资金管理部角色下用户--点击“资金管理”--点击“流动性管理”--点击“监管户待转资金”
        //从基本表获取、无需修改

        //资金管理-点击流动性管理--账户余额明细
        LambdaUpdateWrapper<AccountBalanceBaseInfo> wrapper = Wrappers.lambdaUpdate();
        wrapper.in(AccountBalanceBaseInfo::getAccountNumber, accountNumberList);
        wrapper.set(AccountBalanceBaseInfo::getAccountBank, accountBank);

        //审批流里找一笔租金支付通知的流程--进入详情页（审批状态包含所有）
        //审批流里找一笔租金支付通知的流程--进入详情页--点击“客户名称”超链接（审批状态包含所有）
        //文档不处理

        //登录资金管理部角色下用户--点击同意工作台--项目视图--项目情况--点击“项目质押/监管情况”
        fundFinancingPledgeInfoService.update(Wrappers.<FundFinancingPledgeInfo>lambdaUpdate()
                .in(FundFinancingPledgeInfo::getAccountNumber, accountNumberList)
                .set(FundFinancingPledgeInfo::getAccountName, accountName)
                .set(FundFinancingPledgeInfo::getAccountBank, accountBank));

        //收付款管理--付款核销--进入详情页--付款记录明细
        //PaymentActualDetailUnconfirmed类，已修改

        //收付款管理--收款核销--进入详情页--收款记录明细--点击“查看”
        collectionRecordInfoService.update(Wrappers.<CollectionRecordInfo>lambdaUpdate()
                .in(CollectionRecordInfo::getOurAccountNumber, accountNumberList)
                .set(CollectionRecordInfo::getOurAccountName, accountName)
                .set(CollectionRecordInfo::getOurAccountBank, accountBank));

        //收付款管理--保证金管理--进入详情页--收款记录--点击“查看”
        //collection_record_info表，已修改

        //收付款管理--保证金管理--进入详情页--退款记录--点击“查看”
        marginRecordService.update(Wrappers.<MarginRecordInfo>lambdaUpdate()
                .in(MarginRecordInfo::getOurAccountNumber, accountNumberList)
                .set(MarginRecordInfo::getOurAccountName, accountName)
                .set(MarginRecordInfo::getOurAccountBank, accountBank));
        //租后管理--租金计划--进入详情页--到期通知
        //从基本表取
    }
    //银行账号转为有空格
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
