package cn.zswltech.mithras.application.orchestration.job.fund;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingAccountTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingRepayActual;
import cn.zswltech.mithras.fund.directfinancing.persistence.mapper.FundDirectFinancingBaseInfoMapper;
import cn.zswltech.mithras.fund.directfinancing.persistence.mapper.FundDirectFinancingRepayActualMapper;
import cn.zswltech.mithras.fund.persistence.mapper.financing.FundFinancingBaseInfoMapper;
import cn.zswltech.mithras.fund.persistence.mapper.financing.FundFinancingPayAccountMapper;
import cn.zswltech.mithras.fund.persistence.mapper.financing.FundFinancingRepayActualMapper;
import cn.zswltech.mithras.fund.persistence.mapper.lib.financing.FundFinancingBaseInfoLibMapper;
import cn.zswltech.mithras.fund.persistence.mapper.lib.financing.FundFinancingRepayActualLibMapper;
import cn.zswltech.mithras.fund.persistence.model.organization.FundOrganization;
import cn.zswltech.mithras.fund.persistence.model.financing.*;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.workflow.persistence.model.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.workflow.persistence.model.prepare.FinancingRepayActualProcessDetail;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.fund.application.organization.FundOrganizationService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptFlowPlanService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.workflow.process.prepare.CommonProcessPrepareService;
import cn.zswltech.mithras.workflow.process.prepare.FinancingRepayActualProcessDetailService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.workflow.enums.CommonProcessPrepareStatus.PEND_COMMIT;

/**
 * @author zhouning
 * @date 2024/12/26
 * @description
 */
@Slf4j
@Component
public class FinancingRepayInfoJob {

    @Resource
    private FundDirectFinancingRepayActualMapper directRepayActualMapper;
    @Resource
    private FundFinancingRepayActualMapper repayActualMapper;
    @Resource
    private FundDirectFinancingBaseInfoMapper directFinancingBaseInfoMapper;
    @Resource
    private FundFinancingBaseInfoMapper financingBaseInfoMapper;
    @Resource
    private FundFinancingPayAccountMapper fundFinancingPayAccountMapper;
    @Resource
    private FundOrganizationService fundOrganizationService;
    @Resource
    private FundReceiptRepayBaseInfoService fundReceiptRepayBaseInfoService;
    @Resource
    private FundFinancingBaseInfoService fundFinancingBaseInfoService;
    @Resource
    private FundFinancingBaseInfoLibMapper fundFinancingBaseInfoLibMapper;
    @Resource
    private FundFinancingRepayActualLibMapper fundFinancingRepayActualLibMapper;
    @Resource
    private FundReceiptFlowPlanService fundReceiptFlowPlanService;


    /**
     * 融资成本定价
     */
    @XxlJob("financingRepayInfoInAdvance")
    public void financingRepayInfoInAdvance() {
        try {
            LocalDate currentDate = LocalDate.now();
            LocalDate repayDate = currentDate.plusDays(10);
            //LocalDate preTenDaysDate = currentDate.minusDays(10);
            //直融实际还款
            List<FundDirectFinancingRepayActual> directRepayActualList = directRepayActualMapper.selectList(Wrappers.<FundDirectFinancingRepayActual>lambdaQuery()
                    .eq(FundDirectFinancingRepayActual::getRepayDate, repayDate)
                    .eq(FundDirectFinancingRepayActual::getIsConfirmed, 0)
            );
            List<FundDirectFinancingRepayActual> directPreRepayActualList = directRepayActualMapper.selectList(Wrappers.<FundDirectFinancingRepayActual>lambdaQuery()
                    .lt(FundDirectFinancingRepayActual::getRepayDate, repayDate)
                    .ge(FundDirectFinancingRepayActual::getRepayDate, currentDate)
                    .eq(FundDirectFinancingRepayActual::getIsConfirmed, 0)
            );
            List<FundDirectFinancingRepayActual> directRepayRes = new ArrayList<>();
            if (!directRepayActualList.isEmpty()) {
                directRepayRes.addAll(directRepayActualList);
            }
            if (!directPreRepayActualList.isEmpty()) {
                directRepayRes.addAll(directPreRepayActualList);
            }
            //间融实际还款
            List<FundFinancingRepayActual> repayRes = new ArrayList<>();
            List<FundFinancingRepayActual> repayActualList = repayActualMapper.selectList(Wrappers.<FundFinancingRepayActual>lambdaQuery()
                    .eq(FundFinancingRepayActual::getRepayDate, repayDate)
                    .eq(FundFinancingRepayActual::getIsConfirmed, 0)
            );
            List<FundFinancingRepayActual> preRepayActualList = repayActualMapper.selectList(Wrappers.<FundFinancingRepayActual>lambdaQuery()
                    .lt(FundFinancingRepayActual::getRepayDate, repayDate)
                    .ge(FundFinancingRepayActual::getRepayDate, currentDate)
                    .eq(FundFinancingRepayActual::getIsConfirmed, 0)
            );
            if (!repayActualList.isEmpty()) {
                repayRes.addAll(repayActualList);
            }
            if (!preRepayActualList.isEmpty()) {
                repayRes.addAll(preRepayActualList);
            }
            Map<Long, List<FinancingRepayActualProcessDetail>> map = new HashMap<>();
            //直融
            if (!directRepayRes.isEmpty()) {
                Set<Long> directFinancingIds = directRepayRes.stream().map(FundDirectFinancingRepayActual::getFinancingId).collect(Collectors.toSet());
                List<FundDirectFinancingBaseInfo> fundDirectFinancingBaseInfoList = directFinancingBaseInfoMapper.selectList(Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery()
                        .in(FundDirectFinancingBaseInfo::getId, directFinancingIds));
                Map<Long, FundDirectFinancingBaseInfo> directFinancingMap = new HashMap<>();
                for (FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo : fundDirectFinancingBaseInfoList) {
                    directFinancingMap.putIfAbsent(fundDirectFinancingBaseInfo.getId(), fundDirectFinancingBaseInfo);
                }
                for (FundDirectFinancingRepayActual directRepay : directRepayRes) {
                    FinancingRepayActualProcessDetail actualDetail = new FinancingRepayActualProcessDetail();
                    actualDetail.setFinancingId(directRepay.getFinancingId());
                    Long fundManagerId = 0L;
                    if (directFinancingMap.containsKey(actualDetail.getFinancingId())) {
                        actualDetail.setFinancingCode(directFinancingMap.get(actualDetail.getFinancingId()).getFinancingCode());
                        actualDetail.setFinancingAmount(directFinancingMap.get(actualDetail.getFinancingId()).getFinancingAmount());
                        actualDetail.setOrganizationName(directFinancingMap.get(actualDetail.getFinancingId()).getProductName());
                        fundManagerId = directFinancingMap.get(actualDetail.getFinancingId()).getFundManagerId();
                    }
                    //过滤应还金额=0
                    if (LongUtil.null2zero(directRepay.getRepayAmount()).equals(0L)) {
                        continue;
                    }
                    actualDetail.setFinancingType("ZR");
                    actualDetail.setFundFinancingAccountType(FundFinancingAccountTypeEnum.REPAY_PRINCIPAL_AND_INTEREST.name());
                    actualDetail.setFinancingRepayActualId(directRepay.getId());
                    actualDetail.setRepayDate(directRepay.getRepayDate());
                    actualDetail.setPrincipleAmount(directRepay.getPrincipleAmount());
                    actualDetail.setRepayAmount(directRepay.getRepayAmount());
                    actualDetail.setInterestAmount(directRepay.getInterestAmount());
                    actualDetail.setAccountNumber("1202 0212 1990 0394 595");
                    actualDetail.setAccountBank("中国工商银行杭州市武林支行");
                    map.putIfAbsent(fundManagerId, new ArrayList<>());
                    map.get(fundManagerId).add(actualDetail);
                }
            }
            //间融
            if (!repayRes.isEmpty()) {
                Set<Long> financingIds = repayRes.stream().map(FundFinancingRepayActual::getFinancingId).collect(Collectors.toSet());
                List<FundFinancingBaseInfo> fundFinancingBaseInfoList = financingBaseInfoMapper.selectList(Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                        .in(FundFinancingBaseInfo::getId, financingIds));
                Map<Long, FundFinancingBaseInfo> financingMap = new HashMap<>();
                for (FundFinancingBaseInfo fundFinancingBaseInfo : fundFinancingBaseInfoList) {
                    financingMap.putIfAbsent(fundFinancingBaseInfo.getId(), fundFinancingBaseInfo);
                }
                for (FundFinancingRepayActual repay : repayRes) {
                    List<FundFinancingPayAccount> res = fundFinancingPayAccountMapper.selectList(Wrappers.<FundFinancingPayAccount>lambdaQuery()
                            .eq(FundFinancingPayAccount::getFinancingId, repay.getFinancingId()));
                    List<FundFinancingPayAccount> fundFinancingPayAccountList = filterPayAccount(res);
                    for (FundFinancingPayAccount financingPayAccount : fundFinancingPayAccountList) {
                        FinancingRepayActualProcessDetail actualDetail = new FinancingRepayActualProcessDetail();
                        actualDetail.setFinancingId(repay.getFinancingId());
                        Long fundManagerId = 0L;
                        if (financingMap.containsKey(actualDetail.getFinancingId())) {
                            actualDetail.setFinancingCode(financingMap.get(actualDetail.getFinancingId()).getFinancingCode());
                            actualDetail.setFinancingAmount(financingMap.get(actualDetail.getFinancingId()).getFinancingAmount());
                            if (financingMap.get(actualDetail.getFinancingId()) != null) {
                                List<FundOrganization> organizationList = fundOrganizationService.getByFinancingId(actualDetail.getFinancingId());
                                if (!organizationList.isEmpty()) {
                                    List<String> organizationNameList = organizationList.stream().map(FundOrganization::getOrganizationName).collect(Collectors.toList());
                                    if (!organizationNameList.isEmpty()) {
                                        actualDetail.setOrganizationName(organizationNameList.get(0));
                                    }
                                }
                            }
                            fundManagerId = financingMap.get(actualDetail.getFinancingId()).getFundManagerId();
                        }
                        actualDetail.setFinancingType("DK");
                        actualDetail.setFundFinancingAccountType(financingPayAccount.getAccountCategory());
                        actualDetail.setFinancingRepayActualId(repay.getId());
                        actualDetail.setRepayDate(repay.getRepayDate());
                        if (FundFinancingAccountTypeEnum.REPAY_PRINCIPAL_AND_INTEREST.name().equalsIgnoreCase(financingPayAccount.getAccountCategory())) {
                            actualDetail.setPrincipleAmount(repay.getPrincipleAmount());
                            actualDetail.setRepayAmount(repay.getRepayAmount());
                            actualDetail.setInterestAmount(repay.getInterestAmount());
                        } else if (FundFinancingAccountTypeEnum.REPAY_PRINCIPAL.name().equalsIgnoreCase(financingPayAccount.getAccountCategory())) {
                            actualDetail.setPrincipleAmount(repay.getPrincipleAmount());
                            actualDetail.setRepayAmount(repay.getPrincipleAmount());
                            actualDetail.setInterestAmount(0L);
                        } else if (FundFinancingAccountTypeEnum.REPAY_INTEREST.name().equalsIgnoreCase(financingPayAccount.getAccountCategory())) {
                            actualDetail.setPrincipleAmount(0L);
                            actualDetail.setRepayAmount(repay.getInterestAmount());
                            actualDetail.setInterestAmount(repay.getInterestAmount());
                        }
                        //过滤应还金额=0
                        if (LongUtil.null2zero(actualDetail.getRepayAmount()).equals(0L)) {
                            continue;
                        }
                        actualDetail.setAccountNumber(financingPayAccount.getAccountNumber());
                        actualDetail.setAccountBank(financingPayAccount.getAccountBank());
                        map.putIfAbsent(fundManagerId, new ArrayList<>());
                        map.get(fundManagerId).add(actualDetail);
                    }
                }
            }
            //发送待办
            for (Long fundManagerId : map.keySet()) {
                List<FinancingRepayActualProcessDetail> processDetailList = map.get(fundManagerId);
                if (processDetailList.isEmpty()) {
                    continue;
                }
                //prepare表
                CommonProcessPrepare prepare = CommonProcessPrepare.builder()
                        .processType(ProcessModelTypeEnum.FinancingRepayPlanConfirmFlow.name())
                        .status(PEND_COMMIT.name())
                        .applyTime(LocalDateTime.now())
                        .formName("融资还款计划确认")
                        .currentNode("财务经理确认")
                        .currentAssignee(JSONUtil.toJsonStr(Collections.singletonList(fundManagerId))).build();
                getBean(CommonProcessPrepareService.class).save(prepare);
                for (FinancingRepayActualProcessDetail detail : processDetailList) {
                    detail.setPrepareId(prepare.getId());
                }
                getBean(FinancingRepayActualProcessDetailService.class).saveBatch(processDetailList);
            }
        } catch (Exception e) {
            log.error("生成融资还款计划确认表记录失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    /**
     * 担保成本定价
     */
    @XxlJob("financingRepayInfoDaily")
    public void financingRepayInfoDaily() {
        try {
            LocalDate currentDate = LocalDate.now();
            //直融实际还款
            List<FundDirectFinancingRepayActual> directRepayActualList = directRepayActualMapper.selectList(Wrappers.<FundDirectFinancingRepayActual>lambdaQuery()
                    .eq(FundDirectFinancingRepayActual::getRepayDate, currentDate)
                    .eq(FundDirectFinancingRepayActual::getIsPaid, 0)
            );
            List<FundDirectFinancingRepayActual> directPreRepayActualList = directRepayActualMapper.selectList(Wrappers.<FundDirectFinancingRepayActual>lambdaQuery()
                    .lt(FundDirectFinancingRepayActual::getRepayDate, currentDate)
                    .eq(FundDirectFinancingRepayActual::getIsPaid, 0)
            );
            List<FundDirectFinancingRepayActual> directRepayRes = new ArrayList<>();
            if (!directRepayActualList.isEmpty()) {
                directRepayRes.addAll(directRepayActualList);
            }
            if (!directPreRepayActualList.isEmpty()) {
                directRepayRes.addAll(directPreRepayActualList);
            }
            //间融实际还款
            List<FundFinancingRepayActual> repayRes = new ArrayList<>();
            List<FundFinancingRepayActual> repayActualList = repayActualMapper.selectList(Wrappers.<FundFinancingRepayActual>lambdaQuery()
                    .eq(FundFinancingRepayActual::getRepayDate, currentDate)
                    .eq(FundFinancingRepayActual::getIsPaid, 0)
            );
            List<FundFinancingRepayActual> preRepayActualList = repayActualMapper.selectList(Wrappers.<FundFinancingRepayActual>lambdaQuery()
                    .lt(FundFinancingRepayActual::getRepayDate, currentDate)
                    .eq(FundFinancingRepayActual::getIsPaid, 0)
            );
            if (!repayActualList.isEmpty()) {
                repayRes.addAll(repayActualList);
            }
            if (!preRepayActualList.isEmpty()) {
                repayRes.addAll(preRepayActualList);
            }
            Map<Long, List<FinancingRepayActualProcessDetail>> map = new HashMap<>();
            //直融
            if (!directRepayRes.isEmpty()) {
                Set<Long> directFinancingIds = directRepayRes.stream().map(FundDirectFinancingRepayActual::getFinancingId).collect(Collectors.toSet());
                List<FundDirectFinancingBaseInfo> fundDirectFinancingBaseInfoList = directFinancingBaseInfoMapper.selectList(Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery()
                        .in(FundDirectFinancingBaseInfo::getId, directFinancingIds));
                Map<Long, FundDirectFinancingBaseInfo> directFinancingMap = new HashMap<>();
                for (FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo : fundDirectFinancingBaseInfoList) {
                    directFinancingMap.putIfAbsent(fundDirectFinancingBaseInfo.getId(), fundDirectFinancingBaseInfo);
                }
                for (FundDirectFinancingRepayActual directRepay : directRepayRes) {
                    FinancingRepayActualProcessDetail actualDetail = new FinancingRepayActualProcessDetail();
                    actualDetail.setFinancingId(directRepay.getFinancingId());
                    Long fundManagerId = 0L;
                    if (directFinancingMap.containsKey(actualDetail.getFinancingId())) {
                        actualDetail.setFinancingCode(directFinancingMap.get(actualDetail.getFinancingId()).getFinancingCode());
                        actualDetail.setFinancingAmount(directFinancingMap.get(actualDetail.getFinancingId()).getFinancingAmount());
                        actualDetail.setOrganizationName(directFinancingMap.get(actualDetail.getFinancingId()).getProductName());
                        fundManagerId = directFinancingMap.get(actualDetail.getFinancingId()).getFundManagerId();
                    }
                    //过滤应还金额=0
                    if (LongUtil.null2zero(directRepay.getRepayAmount()).equals(0L)) {
                        continue;
                    }
                    actualDetail.setFinancingType("ZR");
                    actualDetail.setFundFinancingAccountType(FundFinancingAccountTypeEnum.REPAY_PRINCIPAL_AND_INTEREST.name());
                    actualDetail.setFinancingRepayActualId(directRepay.getId());
                    actualDetail.setRepayDate(directRepay.getRepayDate());
                    actualDetail.setPrincipleAmount(directRepay.getPrincipleAmount());
                    actualDetail.setRepayAmount(directRepay.getRepayAmount());
                    actualDetail.setInterestAmount(directRepay.getInterestAmount());
                    actualDetail.setAccountNumber("1202 0212 1990 0394 595");
                    actualDetail.setAccountBank("中国工商银行杭州市武林支行");
                    map.putIfAbsent(fundManagerId, new ArrayList<>());
                    map.get(fundManagerId).add(actualDetail);
                }
            }
            //间融
            if (!repayRes.isEmpty()) {
                Set<Long> financingIds = repayRes.stream().map(FundFinancingRepayActual::getFinancingId).collect(Collectors.toSet());
                List<FundFinancingBaseInfo> fundFinancingBaseInfoList = financingBaseInfoMapper.selectList(Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                        .in(FundFinancingBaseInfo::getId, financingIds));
                Map<Long, FundFinancingBaseInfo> financingMap = new HashMap<>();
                for (FundFinancingBaseInfo fundFinancingBaseInfo : fundFinancingBaseInfoList) {
                    financingMap.putIfAbsent(fundFinancingBaseInfo.getId(), fundFinancingBaseInfo);
                }
                for (FundFinancingRepayActual repay : repayRes) {
                    List<FundFinancingPayAccount> res = fundFinancingPayAccountMapper.selectList(Wrappers.<FundFinancingPayAccount>lambdaQuery()
                            .eq(FundFinancingPayAccount::getFinancingId, repay.getFinancingId()));
                    List<FundFinancingPayAccount> fundFinancingPayAccountList = filterPayAccount(res);
                    for (FundFinancingPayAccount financingPayAccount : fundFinancingPayAccountList) {
                        Long fundManagerId = 0L;
                        FinancingRepayActualProcessDetail actualDetail = new FinancingRepayActualProcessDetail();
                        actualDetail.setFinancingId(repay.getFinancingId());
                        if (financingMap.containsKey(actualDetail.getFinancingId())) {
                            actualDetail.setFinancingCode(financingMap.get(actualDetail.getFinancingId()).getFinancingCode());
                            actualDetail.setFinancingAmount(financingMap.get(actualDetail.getFinancingId()).getFinancingAmount());
                            if (financingMap.get(actualDetail.getFinancingId()) != null) {
                                List<FundOrganization> organizationList = fundOrganizationService.getByFinancingId(actualDetail.getFinancingId());
                                if (!organizationList.isEmpty()) {
                                    List<String> organizationNameList = organizationList.stream().map(FundOrganization::getOrganizationName).collect(Collectors.toList());
                                    if (!organizationNameList.isEmpty()) {
                                        actualDetail.setOrganizationName(organizationNameList.get(0));
                                    }
                                }
                            }
                            fundManagerId = financingMap.get(actualDetail.getFinancingId()).getFundManagerId();
                        }
                        actualDetail.setFinancingType("DK");
                        actualDetail.setFundFinancingAccountType(financingPayAccount.getAccountCategory());
                        actualDetail.setFinancingRepayActualId(repay.getId());
                        actualDetail.setRepayDate(repay.getRepayDate());
                        if (FundFinancingAccountTypeEnum.REPAY_PRINCIPAL_AND_INTEREST.name().equalsIgnoreCase(financingPayAccount.getAccountCategory())) {
                            actualDetail.setPrincipleAmount(repay.getPrincipleAmount());
                            actualDetail.setRepayAmount(repay.getRepayAmount());
                            actualDetail.setInterestAmount(repay.getInterestAmount());
                        } else if (FundFinancingAccountTypeEnum.REPAY_PRINCIPAL.name().equalsIgnoreCase(financingPayAccount.getAccountCategory())) {
                            actualDetail.setPrincipleAmount(repay.getPrincipleAmount());
                            actualDetail.setRepayAmount(repay.getPrincipleAmount());
                            actualDetail.setInterestAmount(0L);
                        } else if (FundFinancingAccountTypeEnum.REPAY_INTEREST.name().equalsIgnoreCase(financingPayAccount.getAccountCategory())) {
                            actualDetail.setPrincipleAmount(0L);
                            actualDetail.setRepayAmount(repay.getInterestAmount());
                            actualDetail.setInterestAmount(repay.getInterestAmount());
                        }
                        //过滤应还金额=0
                        if (LongUtil.null2zero(actualDetail.getRepayAmount()).equals(0L)) {
                            continue;
                        }
                        actualDetail.setAccountNumber(financingPayAccount.getAccountNumber());
                        actualDetail.setAccountBank(financingPayAccount.getAccountBank());
                        map.putIfAbsent(fundManagerId, new ArrayList<>());
                        map.get(fundManagerId).add(actualDetail);
                    }
                }
            }
            //资金核销岗位
            SysUserService userService = SpringContextHolder.getBean(SysUserService.class);
            Set<Long> userIds = userService.getUserIdsByRole("ZJGLB-ZJHXG");
            //发送待办
            for (Long userId : userIds) {
                List<FinancingRepayActualProcessDetail> res = new ArrayList<>();
                for (Long fundManagerId : map.keySet()) {
                    List<FinancingRepayActualProcessDetail> processDetailList = map.get(fundManagerId);
                    if (processDetailList.isEmpty()) {
                        continue;
                    }
                    res.addAll(processDetailList);
                }
                if (!res.isEmpty()) {
                    //prepare表
                    CommonProcessPrepare prepare = CommonProcessPrepare.builder()
                            .processType(ProcessModelTypeEnum.FinancingRepayWriteOffConfirmFlow.name())
                            .status(PEND_COMMIT.name())
                            .applyTime(LocalDateTime.now())
                            .formName("融资还款核销确认")
                            .currentNode("财务经理确认")
                            .currentAssignee(JSONUtil.toJsonStr(Collections.singletonList(userId))).build();
                    getBean(CommonProcessPrepareService.class).save(prepare);
                    for (FinancingRepayActualProcessDetail detail : res) {
                        detail.setPrepareId(prepare.getId());
                    }
                    getBean(FinancingRepayActualProcessDetailService.class).saveBatch(res);
                }
            }
        } catch (Exception e) {
            log.error("生成融资还款核销确认表记录失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    //账户去重
    private List<FundFinancingPayAccount> filterPayAccount(List<FundFinancingPayAccount> accountList) {
        if (accountList.isEmpty()) {
            return new ArrayList<>();
        }
        List<FundFinancingPayAccount> res = new ArrayList<>();
        Map<String, List<FundFinancingPayAccount>> map = new HashMap<>();
        Map<String, List<FundFinancingPayAccount>> categoryMap = new HashMap<>();
        for (FundFinancingPayAccount account : accountList) {
            if (StringUtils.isNotBlank(account.getAccountNumber())) {
                map.putIfAbsent(account.getAccountNumber(), new ArrayList<>());
                map.get(account.getAccountNumber()).add(account);
            }
        }
        for (Map.Entry<String, List<FundFinancingPayAccount>> entry : map.entrySet()) {
            List<FundFinancingPayAccount> list = entry.getValue();
            if (list.size() == 1) {
                res.addAll(list);
            } else if (list.size() > 1) {
                for (FundFinancingPayAccount payAccount : list) {
                    if (StringUtils.isNotBlank(payAccount.getAccountCategory())) {
                        categoryMap.putIfAbsent(payAccount.getAccountCategory(), new ArrayList<>());
                        categoryMap.get(payAccount.getAccountCategory()).add(payAccount);
                    }
                }
            }
        }
        for (Map.Entry<String, List<FundFinancingPayAccount>> entry : categoryMap.entrySet()) {
            List<FundFinancingPayAccount> list = entry.getValue();
            res.add(list.get(0));
        }
        return res;
    }

    //同步融资到还本付息
    @XxlJob("syncFinancingRepay")
    public void syncFinancingRepay() {
        try {
            //查询所有起息状态下到间融
            List<FundFinancingBaseInfo> financingBaseInfoList = fundFinancingBaseInfoService.list(Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                    .eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name()));
                    //.notIn(FundFinancingBaseInfo::getApprovalStatus, FundFinancingProcessStatus.NEW_UN_SUBMIT.name(), FundFinancingProcessStatus.NEW_UNDER_APPROVAL.name(), FundFinancingProcessStatus.CHANGING_UN_SUBMIT.name(), FundFinancingProcessStatus.CHANGING_UNDER_APPROVAL.name()));
            if (ObjectUtil.isNotEmpty(financingBaseInfoList)) {
                for (FundFinancingBaseInfo fundFinancingBaseInfo : financingBaseInfoList) {
                    Long financingId = fundFinancingBaseInfo.getId();
                    FundReceiptRepayBaseInfo baseInfo = fundReceiptRepayBaseInfoService.getOne(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                            .eq(FundReceiptRepayBaseInfo::getFinancingId, financingId)
                            .isNull(FundReceiptRepayBaseInfo::getFinancingType)
                            .last("limit 1"));
                    FundFinancingBaseInfoLib financingBaseInfoLib = fundFinancingBaseInfoLibMapper.selectOne(Wrappers.<FundFinancingBaseInfoLib>lambdaQuery()
                            .eq(FundFinancingBaseInfoLib::getOriginId, financingId)
                            .eq(FundFinancingBaseInfoLib::getVersionType, 1)
                            .orderByDesc(FundFinancingBaseInfoLib::getVersion)
                            .last("limit 1"));
                    List<FundFinancingRepayActualLib> repayActualLibs = fundFinancingRepayActualLibMapper.selectList(Wrappers.<FundFinancingRepayActualLib>lambdaQuery()
                            .eq(FundFinancingRepayActualLib::getFinancingId, financingId)
                            .eq(FundFinancingRepayActualLib::getVersion, financingBaseInfoLib.getVersion())
                            .gt(FundFinancingRepayActual::getPhase, 0)
                            .orderByAsc(FundFinancingRepayActualLib::getRepayDate));
                    fundReceiptRepayBaseInfoService.upgradeRecipeRepay(baseInfo, financingBaseInfoLib, repayActualLibs);
                    fundReceiptRepayBaseInfoService.syncToFlowPlan(baseInfo.getId());
                }
            }

        } catch (Exception e) {
            log.error("同步间融到还本付息异常", e);
            throw new MithrasException("同步间融到还本付息异常");
        }
        //同步直融数据
        try {
            //查询所以起息状态下到间融
            List<FundDirectFinancingBaseInfo> directFinancingBaseInfoList = directFinancingBaseInfoMapper.selectList(Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery()
                    .in(FundDirectFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name(), FundFinancingStatusEnum.EFFECT.name()));
            if (ObjectUtil.isNotEmpty(directFinancingBaseInfoList)) {
                for (FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo : directFinancingBaseInfoList) {
                    Long financingId = fundDirectFinancingBaseInfo.getId();
                    Long fundReceiptRepayBaseInfoId = fundReceiptRepayBaseInfoService.upgradeDirectFinancingReceipt(financingId);
                    fundReceiptRepayBaseInfoService.syncToFlowPlan(fundReceiptRepayBaseInfoId);
                }
            }
        } catch (Exception e) {
            log.error("同步间融到还本付息异常", e);
            throw new MithrasException("同步间融到还本付息异常");
        }
    }
}
