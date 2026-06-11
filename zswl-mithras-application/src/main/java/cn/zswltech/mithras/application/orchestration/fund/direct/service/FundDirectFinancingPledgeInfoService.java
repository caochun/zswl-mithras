package cn.zswltech.mithras.application.orchestration.fund.direct.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswl.notice.message.impl.WebSocketServer;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.rating.model.RatingAmount;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.directfinancing.application.convert.FundDirectFinancingPledgeInfoConverter;
import cn.zswltech.mithras.fund.directfinancing.application.FundDirectFinancingPayAccountService;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingPayAccount;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.fund.directfinancing.excel.directfinancing.FundDirectFinancingPledgeInfoExcelModel;
import cn.zswltech.mithras.fund.directfinancing.excel.directfinancing.FundDirectFinancingPledgeInfoExporter;
import cn.zswltech.mithras.fund.directfinancing.excel.directfinancing.NewFundDirectFinancingPledgeInfoExcelModel;
import cn.zswltech.mithras.fund.directfinancing.mapper.FundDirectFinancingPledgeInfoMapper;
import cn.zswltech.mithras.message.model.MessageModel;
import cn.zswltech.mithras.message.model.PopUpNotificationBody;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.fund.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.application.orchestration.collection.CollectionService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.foundation.util.BigDecimalUtil;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 直接融资-质押明细
 * @date 2023-06-17
 */
@Service
public class FundDirectFinancingPledgeInfoService
        extends ServiceImpl<FundDirectFinancingPledgeInfoMapper, FundDirectFinancingPledgeInfo> {

    @Resource
    private FundDirectFinancingPledgeInfoConverter baseConverter;
    @Resource
    private FundFinancingPledgeInfoService fundFinancingPledgeInfoService;
    @Resource
    private FundDirectFinancingRepayActualService fundDirectFinancingRepayActualService;
    @Resource
    private FundDirectFinancingBaseInfoService fundDirectFinancingBaseInfoService;
    @Resource
    private FundFinancingBaseInfoService fundFinancingBaseInfoService;
    @Resource
    private CollectionService collectionService;
    @Resource
    private FundDirectFinancingPledgeInfoExporter pledgeInfoExporter;
    @Resource
    private FundDirectFinancingPayAccountService directFinancingPayAccountService;
    @Resource
    private UserService userService;
    @Resource
    private MessageConver messageConver;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private FundDirectFinancingRepayActualService directFinancingRepayActualService;

    public List<FundDirectFinancingPledgeInfo> listByFinancingId(Long financingId) {
        LambdaQueryWrapper<FundDirectFinancingPledgeInfo> query = Wrappers.lambdaQuery();
        query.eq(FundDirectFinancingPledgeInfo::getFinancingId, financingId);
        return this.list(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void add(FundDirectFinancingPledgeInfoAddREQ req) {
        FundDirectFinancingBaseInfo baseInfo = fundDirectFinancingBaseInfoService.getById(req.getFinancingId());
        if (ObjectUtil.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }

        // 单个合同只能被同一个融资，质押一次
        List<FundDirectFinancingPledgeInfo> list = SpringContextHolder.getBean(FundDirectFinancingPledgeInfoService.class).list(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery()
                .eq(FundDirectFinancingPledgeInfo::getContractId, req.getContractId())
                .eq(FundDirectFinancingPledgeInfo::getFinancingId, req.getFinancingId())
        );
        if (CollectionUtil.isNotEmpty(list)) {
            // 说明已经存在了，不能二次添加
            throw MithrasException.newException("该合同已经存在于当前融资，不能重复添加");
        }

        checkReq(req.getFinancingId(), req.getContractId(), req.getIsPledge(), req.getIsSupervise());

        FundDirectFinancingPledgeInfo info = baseConverter.addReq2Entity(req);
        if(info.getIsPledge() == null || !info.getIsPledge()){ //质押的情况才标记
            info.setLockContract("0");
        }else{
            info.setLockContract("1");
        }
        if (ObjectUtil.isNull(req.getRemainingUnpaidPrincipal())) {
            Map<Long, Long> longLongMap = collectionService.listContractAmountByContractIds(Collections.singletonList(req.getContractId()), Boolean.FALSE, Boolean.FALSE);
            info.setRemainingUnpaidPrincipal(LongUtil.null2zero(longLongMap.get(req.getContractId())));
        }

        Integer oldPledgeCount = baseMapper.selectCount(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery()
                .eq(FundDirectFinancingPledgeInfo::getFinancingId, req.getFinancingId()));
        String pledgeCode = baseInfo.getFinancingCode() + "ZY" + String.format("%02d", oldPledgeCount + 1);
        info.setPledgeCode(pledgeCode);

        if (ObjectUtil.isEmpty(req.getContractEndDate())) {
            Map<Long, LocalDateTime> lastRentDateByContractIds = collectionService.getLastRentDateByContractIds(Collections.singletonList(req.getContractId()));
            info.setContractEndDate(ObjectUtil.isEmpty(lastRentDateByContractIds.get(req.getContractId())) ? null : LocalDate.from(lastRentDateByContractIds.get(req.getContractId())));
        }
        baseMapper.insert(info);
        directFinancingRepayActualService.saveCashFlowAgain(info.getFinancingId());
    }

    /**
     * 质押时弹窗提醒
     *
     * @param financingId
     */
    public void sendPopUpMsg(Long financingId) {
        List<FundDirectFinancingPledgeInfo> pledgeInfos = list(
                Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery()
                        .eq(FundDirectFinancingPledgeInfo::getFinancingId, financingId));

        for (FundDirectFinancingPledgeInfo pledgeInfo : pledgeInfos) {
            // 查询还款账户
            List<FundDirectFinancingPayAccount> accounts = directFinancingPayAccountService.list(Wrappers.<FundDirectFinancingPayAccount>lambdaQuery()
                    .eq(FundDirectFinancingPayAccount::getFinancingId, pledgeInfo.getFinancingId()));
            String accountInfo;
            if (CollectionUtil.isEmpty(accounts)) {
                accountInfo = "融资中未维护收款账户";
            } else {
                FundDirectFinancingPayAccount account = accounts.get(0);
                accountInfo = account.getAccountBank() + account.getAccountNumber();
            }
            // 查询合同最近一期的租金收款id
            CollectionBaseInfo currentIssue = collectionService.getCurrentIssue(pledgeInfo.getContractId());
            ContractBaseInfo contract = contractBaseInfoService.getById(pledgeInfo.getContractId());
            Long toUserId = contract.getProjSponsorUserId();
            MessageModel messageModel = new MessageModel();
            messageModel.setNeedOa(false);
            messageModel.setFrom("系统通知");
            messageModel.setTo(ListUtil.toList(toUserId));
            PopUpNotificationBody notificationBody = new PopUpNotificationBody();
            notificationBody.setContent(String.format("%s项目已被质押，自%s起还款账户为：%s",
                    contract.getProjName(),
                    DateUtil.format(LocalDateTime.now(), DatePattern.NORM_DATETIME_PATTERN),
                    accountInfo));
            notificationBody.setTitle("融资质押提醒");
            Map<String, Object> attach = new HashMap<>();
            attach.put("popUpType", "FINANCE_PLEDGE_ADD");
            if (ObjectUtil.isNotEmpty(currentIssue)) {
                attach.put("collectionId", currentIssue.getId());
            }
            notificationBody.setAttachment(attach);
            messageModel.setMsgId(pledgeInfo.getContractId());
            messageModel.setBodie(notificationBody);
            messageModel.setToTel(ListUtil.toList(userService.getRealPhone(toUserId)));
            //直接使用
            WebSocketServer.sendAsyncInfo(messageConver.buildPopUpNotification(messageModel));
        }
    }


    @Transactional(rollbackFor = Throwable.class)
    public void modify(FundDirectFinancingPledgeInfoModifyREQ req) {
        FundDirectFinancingPledgeInfo originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        checkReq(req.getFinancingId(), req.getContractId(), req.getIsPledge(), req.getIsSupervise());
        FundDirectFinancingPledgeInfo info = baseConverter.modifyReq2Entity(req);
        baseMapper.updateById(info);
    }

    public PageR<FundDirectFinancingPledgeInfoListRSP> list(FundDirectFinancingPledgeInfoListREQ req) {
        Page<FundDirectFinancingPledgeInfo> pledgeInfos = baseMapper.selectPage(
                new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery()
                        .eq(FundDirectFinancingPledgeInfo::getFinancingId, req.getFinancingId()));
        if (ObjectUtil.isEmpty(pledgeInfos.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<Long> contractIds = pledgeInfos.getRecords().stream().map(FundDirectFinancingPledgeInfo::getContractId).collect(Collectors.toList());
        Map<Long, Long> remainingPrincipal = collectionService.listContractAmountByContractIds(contractIds, Boolean.FALSE, Boolean.FALSE);
        Map<Long, Long> contractId2ClientId = contractBaseInfoService.listByIds(contractIds).stream().collect(Collectors.toMap(ContractBaseInfo::getId, ContractBaseInfo::getClientId, (a, b) -> a));
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(contractId2ClientId.values());
        List<FundDirectFinancingPledgeInfoListRSP> rspList = new ArrayList<>();
        for (FundDirectFinancingPledgeInfo pledgeInfo : pledgeInfos.getRecords()) {
            FundDirectFinancingPledgeInfoListRSP rsp = baseConverter.entity2ListRsp(pledgeInfo);
            rsp.setRemainingUnpaidPrincipal(remainingPrincipal.getOrDefault(rsp.getContractId(), 0L));
            rsp.setClientId(contractId2ClientId.get(pledgeInfo.getContractId()));
            rsp.setClientName(clientId2Name.get(rsp.getClientId()));
            rspList.add(rsp);
        }
        return PageR.of(pledgeInfos, rspList);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(Long id) {
        FundDirectFinancingPledgeInfo originalInfo = baseMapper.selectById(id);
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        baseMapper.deleteById(id);
        directFinancingRepayActualService.saveCashFlowAgain(originalInfo.getFinancingId());
    }

    public FundDirectFinancingPledgeInfoDetailRSP detail(Long id) {
        FundDirectFinancingPledgeInfo pledgeInfo = baseMapper.selectById(id);
        if (ObjectUtil.isNull(pledgeInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FundDirectFinancingPledgeInfoDetailRSP rsp = baseConverter.entity2DetailRsp(pledgeInfo);
        Map<Long, Long> remainingPrincipal = collectionService.listContractAmountByContractIds(Collections.singletonList(pledgeInfo.getContractId()), Boolean.FALSE, Boolean.FALSE);
        rsp.setRemainingUnpaidPrincipal(LongUtil.null2zero(remainingPrincipal.get(pledgeInfo.getContractId())));
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(pledgeInfo.getContractId());
        if (ObjectUtil.isNotEmpty(contractBaseInfo)) {
            rsp.setClientId(contractBaseInfo.getClientId());
            rsp.setClientName(id2NameService.clientId2NameSingle(contractBaseInfo.getClientId()));
        }
        rsp.setBizDeptName(id2NameService.deptId2NameSingle(rsp.getBizDeptId()));
        return rsp;
    }

    public List<FundDirectFinancingPledgeInfo> findContractPledgeList(Long contractId) {
        List<FundDirectFinancingPledgeInfo> directFinancingPledgeInfoList = this.list(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery().eq(FundDirectFinancingPledgeInfo::getContractId, contractId));
        if (CollectionUtil.isEmpty(directFinancingPledgeInfoList)) {
            return null;
        }
        // 批量查询融资的实际还款计划

       /* Set<Long> financingIds = directFinancingPledgeInfoList.stream().map(FundDirectFinancingPledgeInfo::getFinancingId).collect(Collectors.toSet());
        LambdaQueryWrapper<FundDirectFinancingRepayActual> query = Wrappers.lambdaQuery();
        query.in(FundDirectFinancingRepayActual::getFinancingId, financingIds);
        List<FundDirectFinancingRepayActual> repayActualList = fundDirectFinancingRepayActualService.list(query);
        if (CollectionUtil.isEmpty(repayActualList)) {
            return directFinancingPledgeInfoList;
        }
        // 分组聚合
        Map<Long, List<FundDirectFinancingRepayActual>> repayActualMap = repayActualList.stream().collect(Collectors.groupingBy(FundDirectFinancingRepayActual::getFinancingId));
        */
        // 找到最后一期的时间，如果早于当前时间认为融资已经到期，剔除到期融资
//        LocalDate now = LocalDate.now();
//        for (Map.Entry<Long, List<FundDirectFinancingRepayActual>> entry : repayActualMap.entrySet()) {
//            List<FundDirectFinancingRepayActual> list = entry.getValue();
//            list.sort(Comparator.comparing(FundDirectFinancingRepayActual::getPhase));
//            FundDirectFinancingRepayActual last = list.get(list.size() - 1);
//            if (now.isAfter(last.getRepayDate())) {
//                directFinancingPledgeInfoList.removeIf(item -> Objects.equals(item.getFinancingId(), entry.getKey()));
//            }
//        }
        if(CollectionUtil.isNotEmpty(directFinancingPledgeInfoList)){
            // 过滤状态
            List<FundDirectFinancingBaseInfo> financingBaseInfoList = fundDirectFinancingBaseInfoService.listByIds(directFinancingPledgeInfoList.stream().map(FundDirectFinancingPledgeInfo::getFinancingId).collect(Collectors.toList()));
            Map<Long, FundDirectFinancingBaseInfo> financingBaseInfoMap = financingBaseInfoList.stream().collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, Function.identity()));
            List<String> pledgeCondition = Arrays.asList(FundFinancingStatusEnum.NEW.name(), FundFinancingStatusEnum.EFFECT.name(), FundFinancingStatusEnum.CARRY_INTEREST.name());
            directFinancingPledgeInfoList.removeIf(item -> !pledgeCondition.contains(Optional.ofNullable(financingBaseInfoMap.get(item.getFinancingId())).map(FundDirectFinancingBaseInfo::getFinancingStatus).orElse(null)));
        }
        return directFinancingPledgeInfoList;
    }

    public String buildTip(Long financingId) {
        FundDirectFinancingBaseInfo financingBaseInfo = fundDirectFinancingBaseInfoService.getById(financingId);
        return String.format("该合同已在编号为%s的融资中被质押", financingBaseInfo.getFinancingCode());
    }

    public String buildSuperviseTip(Long financingId) {
        FundDirectFinancingBaseInfo financingBaseInfo = fundDirectFinancingBaseInfoService.getById(financingId);
        return String.format("该合同已在编号为%s的融资中被监管", financingBaseInfo.getFinancingCode());
    }

    public void checkReq(Long financingId, Long contractId, boolean isPledge, boolean isSupervise) {
        //  校验是否质押
        if (isPledge) {
            // 校验是否已经质押给其他融资
            List<FundFinancingPledgeInfo> fundFinancingPledgeInfoList = fundFinancingPledgeInfoService.findContractPledgeList(contractId);
            if (!ObjectUtils.isEmpty(fundFinancingPledgeInfoList)) {
                Optional<FundFinancingPledgeInfo> any = fundFinancingPledgeInfoList.stream().filter(item -> !item.getFinancingId().equals(financingId))
                        .filter(item -> Objects.equals(item.getIsPledge(), Boolean.TRUE)).findAny();
                if(any.isPresent()){
                    throw MithrasException.newException(fundFinancingPledgeInfoService.buildTip(any.get().getFinancingId()));
                }
            }
            // 校验是否已经质押给直接融资
            List<FundDirectFinancingPledgeInfo> directFinancingPledgeInfoList = findContractPledgeList(contractId);
            if (!ObjectUtils.isEmpty(directFinancingPledgeInfoList)) {
                Optional<FundDirectFinancingPledgeInfo> any = directFinancingPledgeInfoList.stream().filter(item -> !item.getFinancingId().equals(financingId))
                        .filter(item -> Objects.equals(item.getIsPledge(), Boolean.TRUE)).findAny();
                if(any.isPresent()){
                    throw MithrasException.newException(buildTip(any.get().getFinancingId()));
                }
            }
        }

        //  校验是否监管
        if (isSupervise) {
            // 校验是否已经质押给直接融资
            List<FundDirectFinancingPledgeInfo> directFinancingPledgeInfoList = findContractPledgeList(contractId);
            if (!ObjectUtils.isEmpty(directFinancingPledgeInfoList)) {
                Optional<FundDirectFinancingPledgeInfo> any = directFinancingPledgeInfoList.stream().filter(item -> !item.getFinancingId().equals(financingId))
                        .filter(item -> Objects.equals(item.getIsSupervise(), Boolean.TRUE)).findAny();
                if(any.isPresent()){
                    throw MithrasException.newException(buildSuperviseTip(any.get().getFinancingId()));
                }
            }

            // 还要看见融
            List<FundFinancingPledgeInfo> fundFinancingPledgeInfoList = fundFinancingPledgeInfoService.findContractPledgeList(contractId);
            if (!ObjectUtils.isEmpty(fundFinancingPledgeInfoList)) {
                Optional<FundFinancingPledgeInfo> any = fundFinancingPledgeInfoList.stream().filter(item -> !item.getFinancingId().equals(financingId))
                        .filter(item -> Objects.equals(item.getIsSupervise(), Boolean.TRUE)).findAny();
                if(any.isPresent()){
                    throw MithrasException.newException(fundFinancingPledgeInfoService.buildSuperviseTip(any.get().getFinancingId()));
                }
            }
        }
    }

    public void exportExcel(ServletOutputStream outputStream, Long financingId) {
        List<FundDirectFinancingPledgeInfo> pledgeInfos = list(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery().eq(FundDirectFinancingPledgeInfo::getFinancingId, financingId));
        if (CollectionUtils.isEmpty(pledgeInfos)){
            pledgeInfoExporter.exportExcel(Collections.emptyList(), outputStream);
            return;
        }
        List<Long> contractIds =pledgeInfos.stream().map(FundDirectFinancingPledgeInfo::getContractId).collect(Collectors.toList());
        Map<Long, Long> remainingPrincipal = collectionService.listContractAmountByContractIds(contractIds, Boolean.FALSE, Boolean.FALSE);
        Map<Long, Long> contractId2ClientId = contractBaseInfoService.listByIds(contractIds).stream().collect(Collectors.toMap(ContractBaseInfo::getId, ContractBaseInfo::getClientId, (a, b) -> a));
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(contractId2ClientId.values());
        List<FundDirectFinancingPledgeInfoListRSP> rspList = new ArrayList<>();
        for (FundDirectFinancingPledgeInfo pledgeInfo : pledgeInfos) {
            FundDirectFinancingPledgeInfoListRSP rsp = baseConverter.entity2ListRsp(pledgeInfo);
            rsp.setRemainingUnpaidPrincipal(remainingPrincipal.getOrDefault(rsp.getContractId(), 0L));
            rsp.setClientId(contractId2ClientId.get(pledgeInfo.getContractId()));
            rsp.setClientName(clientId2Name.get(rsp.getClientId()));
            rspList.add(rsp);
        }
        List<NewFundDirectFinancingPledgeInfoExcelModel> excelModels = getNewExcleModels(rspList);
        pledgeInfoExporter.exportExcel(excelModels, outputStream);
    }

    public String getSuperviseAccountNum(String contractCode) {
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getContractCode, contractCode));
        if (contractBaseInfoList.isEmpty()) {
            return null;
        }
        List<FundDirectFinancingPledgeInfo> directFinancingPledgeInfoList = list(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery()
                .eq(FundDirectFinancingPledgeInfo::getContractId, contractBaseInfoList.get(0).getId())
                .eq(FundDirectFinancingPledgeInfo::getIsSupervise, 1));
        if (!directFinancingPledgeInfoList.isEmpty()) {
            return directFinancingPledgeInfoList.get(0).getAccountNumber();
        }
        return null;
    }

    public Map<Long, List<FundDirectFinancingPledgeInfo>> getMapByFinancings(Set<Long> financings) {
        if (CollectionUtil.isEmpty(financings)) {
            return MapUtil.empty();
        }
        return list(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery()
                .in(FundDirectFinancingPledgeInfo::getFinancingId, financings)).stream().collect(Collectors.groupingBy(FundDirectFinancingPledgeInfo::getFinancingId));
    }

    //这里特殊处理
    public List<NewFundDirectFinancingPledgeInfoExcelModel> getNewExcleModels(List<FundDirectFinancingPledgeInfoListRSP> rspList) {
        if (CollectionUtils.isEmpty(rspList)) {
            return Collections.emptyList();
        }
        List<NewFundDirectFinancingPledgeInfoExcelModel> excelModelList = new ArrayList<>();
        for (FundDirectFinancingPledgeInfoListRSP rsp : rspList) {
            NewFundDirectFinancingPledgeInfoExcelModel excelModel = new NewFundDirectFinancingPledgeInfoExcelModel();
            excelModel.setPledgeCode(rsp.getPledgeCode());
            excelModel.setClientName(rsp.getClientName());
            excelModel.setProjName(rsp.getProjName());
            excelModel.setContractCode(rsp.getContractCode());
            ProjectBizType bizType = ProjectBizType.of(rsp.getBizType());
            if (Objects.nonNull(bizType)) {
                excelModel.setBizTypeName(bizType.display());
            }
            if (Objects.nonNull(rsp.getIsPledge())) {
                excelModel.setIsPledgeName(rsp.getIsPledge() ?
                        YesOrNoNumberEnum.YES.getChinese() : YesOrNoNumberEnum.NO.getChinese());
            }
            if (Objects.nonNull(rsp.getIsSupervise())) {
                excelModel.setIsSuperviseName(rsp.getIsSupervise() ?
                        YesOrNoNumberEnum.YES.getChinese() : YesOrNoNumberEnum.NO.getChinese());
            }
            excelModel.setAccountNumber(rsp.getAccountNumber());
            excelModel.setAccountBank(rsp.getAccountBank());
            excelModel.setAccountName(rsp.getAccountName());
            if (Objects.nonNull(rsp.getContractAmount())) {
                //金额装换
                excelModel.setContractAmount(BigDecimalUtil.li2Yuan(rsp.getContractAmount()));
            }
            //
            excelModel.setContractTrem(formatContractDateRange(rsp.getContractStartDate(), rsp.getContractEndDate()));
            if (Objects.nonNull(rsp.getRemainingUnpaidPrincipal())) {
                excelModel.setRemainingUnpaidPrincipal(BigDecimalUtil.li2Yuan(rsp.getRemainingUnpaidPrincipal()));
            }
            excelModelList.add(excelModel);
        }
        return excelModelList;

    }

    private String formatContractDateRange(LocalDate contractStartDate, LocalDate contractEndDate) {
        DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 处理开始日期：null → 「无」，非null → 格式化年月日
        String startStr = contractStartDate != null
                ? contractStartDate.format(yyyyMMdd) + ""
                : "";
        String endStr = contractEndDate != null
                ? contractEndDate.format(yyyyMMdd) + ""
                : "";

        // 拼接「开始~结束」格式
        return startStr + "~" + endStr;
    }

    public void unLockContract(Long financingId) {
        this.update(Wrappers.<FundDirectFinancingPledgeInfo>lambdaUpdate()
                .eq(FundDirectFinancingPledgeInfo::getFinancingId,financingId)
                .set(FundDirectFinancingPledgeInfo::getLockContract, "0"));
    }
}
