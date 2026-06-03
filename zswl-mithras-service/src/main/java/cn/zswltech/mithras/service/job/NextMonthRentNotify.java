package cn.zswltech.mithras.service.job;
import cn.zswltech.mithras.workflow.domain.enums.CommonProcessPrepareStatus;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.fund.RentPayNoticeProcessDTO;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.service.mapper.SystemConfigMapper;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.SystemConfig;
import cn.zswltech.mithras.service.mapper.model.basedata.BaseDataBankAccount;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.RentCollectionMonthDetail;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayCashFlowService;
import cn.zswltech.mithras.service.service.process.prepare.CommonProcessPrepareService;
import cn.zswltech.mithras.workflow.application.process.prepare.RentCollectionMonthDetailService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;
import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.workflow.domain.enums.CommonProcessPrepareStatus.PEND_COMMIT;
import static cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum.UNCOLLECTION;
import static cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingStatusEnum.CARRY_INTEREST;
import static cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingStatusEnum.EFFECT;
import static cn.zswltech.mithras.service.job.NextMonthRentNotify.FinancingType.direct;
import static cn.zswltech.mithras.service.job.NextMonthRentNotify.FinancingType.indirect;
import static java.util.stream.Collectors.groupingBy;

/**
 * @author luyi
 * 每月定时⽣成下个⽉整⽉的租⾦⽀付通知表记录，在月底跑，具体查阅xxljob cron配置
 */
@Slf4j
@Component
public class NextMonthRentNotify {

    @XxlJob("generateNextMonthRentNotify")
    @Transactional(rollbackFor = Exception.class)
    public void runJob(String optionalParam, List<Long> contractIdList, Boolean isSupervise) {
        try {
            LocalDateTime currentMonthLastMoment;
            LocalDateTime nextMonthLastMoment;
            //1、确定时间范围
            String param = XxlJobHelper.getJobParam();
            if (isNotBlank(param)) {
                log.info("NextMonthRentNotify - 控制台参数: {}", param);
                optionalParam = param;
            }
            if (isNotBlank(optionalParam)) {
                //可以通过指定入参，生成指定月份的数据；用于手动重跑任务
                currentMonthLastMoment = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).withMonth(Integer.parseInt(optionalParam)).withDayOfMonth(1).minusSeconds(1);
            } else {
                currentMonthLastMoment = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).withDayOfMonth(1).plusMonths(1).minusSeconds(1);
            }
            nextMonthLastMoment = currentMonthLastMoment.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
            //2、获取数据
            List<CollectionBaseInfo> nextMonthRentList = getBean(CollectionBaseInfoMapper.class).selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getWriteOffStatus, UNCOLLECTION.name())
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                    .ge(CollectionBaseInfo::getPlanCollectionDate, currentMonthLastMoment)
                    .le(CollectionBaseInfo::getPlanCollectionDate, nextMonthLastMoment)
                    .in(CollectionUtil.isNotEmpty(contractIdList), CollectionBaseInfo::getContractId, contractIdList)
            );

            //2.1 过滤已经生成的记录(若存在参数则为人工操作，不进行过滤操作)
            if(!isNotBlank(optionalParam) && !isNotEmpty(contractIdList)) {
                List<Long> existId = getBean(RentCollectionMonthDetailService.class).
                        queryByIdList(
                                nextMonthRentList.stream().map(CollectionBaseInfo::getId).collect(Collectors.toList()),
                                nextMonthLastMoment);
                if (isNotEmpty(existId)) {
                    nextMonthRentList = nextMonthRentList.stream().filter(e -> !existId.contains(e.getId())).collect(Collectors.toList());
                }
            }
            if (CollectionUtil.isEmpty(nextMonthRentList)) {
                return;
            }
            //3、按照部门进行分组，并生成记录；
            //3.1、从对应的【合同】获取部门
            Map<Long/*合同id*/, ContractBaseInfo/*合同*/> contractMap = getBean(ContractBaseInfoService.class).listByIds(
                    nextMonthRentList.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toList())
            ).stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e, (v1, v2) -> v1));
            Map<Long/*部门id*/, List<CollectionBaseInfo>/*租金列表*/> deptMapCollection =
                    nextMonthRentList.stream().collect(groupingBy(e -> contractMap.get(e.getContractId()).getBizDeptId()));

            Map<Long/*部门id*/, String/*部门名称*/> deptIdMapName = getBean(OrgDOMapper.class)
                    .selectByIds(new ArrayList<>(deptMapCollection.keySet()), null)
                    .stream().collect(Collectors.toMap(OrgDO::getId, OrgDO::getName, (v1, v2) -> v1));
            //List<Long> assigneeIds = getBean(UserService.class).getUsersByjobcod(financialofficer.name()).stream().map(UserDO::getId).collect(Collectors.toList());
            //（何佳薇-浙江业务部、郑曦-公用事业部和新能源部、沈悦-交通物流部和航运业务部、王诗岚-高端装备部、工建、智能制造部）
            SystemConfig config = getBean(SystemConfigMapper.class).selectOne(Wrappers.<SystemConfig>lambdaQuery()
                    .select(SystemConfig::getConfigValue)
                    .eq(SystemConfig::getConfigKey, "rentPayNoticeProcess")
                    .eq(SystemConfig::getStatus, YesOrNoNumberEnum.YES.getCode())
                    .last(StringUtil.mysqlLimitOne()));
            if (ObjectUtil.isNull(config) || CharSequenceUtil.isBlank(config.getConfigValue())) {
                log.error("缺少业务需要的核心配置，请联系管理员配置！");
                throw new MithrasException("缺少业务需要的核心配置，请联系管理员配置！");
            }

            List<RentPayNoticeProcessDTO> rentPayNoticeProcessDTOList = convertConfig(config);
            Map<String, List<Long>> deptCodeUserIdMap = rentPayNoticeProcessDTOList.stream()
                    .collect(Collectors.toMap(RentPayNoticeProcessDTO::getDeptCode, RentPayNoticeProcessDTO::getUserIds));
            //合同融资关联
            Map<Long, PledgeInfo> contractPledgeMap = contractBankMapByFinancing(contractMap.keySet(), isSupervise);
//            contractPledgeMap.putAll(contractBankMapByDirectFinancing(contractPledgeMap.keySet(), isSupervise));
            Map<Long, PledgeInfo> directContractPledgeMap = contractBankMapByDirectFinancing(contractMap.keySet(), isSupervise);
            //默认银行账户
            BaseDataBankAccount defaultBank = defaultBank();
            //3.2、部门逐一处理
            LocalDateTime applyTime = LocalDateTime.now();
            for (Long deptId : deptMapCollection.keySet()) {
                OrgDO orgDO = getBean(OrgDOMapper.class).selectByPrimaryKey(deptId);
                if (ObjectUtil.isNull(orgDO)) {
                    log.error("id为{}的岗位不存在", deptId);
                    //做降级，不全部失败
                    return;
                }
                List<Long> assigneeIds = deptCodeUserIdMap.get(orgDO.getCode());
                if (CollUtil.isEmpty(assigneeIds)) {
                    log.error("「{}」租金支付通知配置有误，请联系管理员处理", orgDO.getName());
                    return;
                }
                //prepare表
                CommonProcessPrepare prepare = CommonProcessPrepare.builder()
                        .processType(ProcessModelTypeEnum.RentPaymentNotifyFlow.name())
                        .status(PEND_COMMIT.name())
                        .applyTime(applyTime)
                        .formName(String.format(
                                "%s-%s-租⾦⽀付通知",
                                deptIdMapName.get(deptId),
                                LocalDateTimeUtil.format(nextMonthLastMoment, "yyyy年MM月")))
                        .currentNode("财务经理确认租金")
                        .currentAssignee(JSONUtil.toJsonStr(assigneeIds)).build();
                getBean(CommonProcessPrepareService.class).save(prepare);
                //prepare detail表
                List<CollectionBaseInfo> collectionList = deptMapCollection.get(deptId);
                List<RentCollectionMonthDetail> detailList = new ArrayList<>(collectionList.size());
                for (CollectionBaseInfo collection : collectionList) {
                    ContractBaseInfo contractBaseInfo = contractMap.get(collection.getContractId());
//                    PledgeInfo pledgeInfo = contractPledgeMap.get(collection.getContractId());
                    PledgeInfo pledgeInfo = this.ensurePledgeInfo(contractPledgeMap.get(collection.getContractId()), directContractPledgeMap.get(collection.getContractId()));
                    if (pledgeInfo == null) {
                        pledgeInfo = new PledgeInfo()
                                .setBankName(defaultBank.getAccountBank())
                                .setAccountName(defaultBank.getAccountName())
                                .setAccountNumber(defaultBank.getAccountNumber());
                    }
                    RentCollectionMonthDetail detail = RentCollectionMonthDetail.builder()
                            .prepareId(prepare.getId())
                            .collectionId(collection.getId())
                            .year(nextMonthLastMoment.getYear())
                            .month(nextMonthLastMoment.getMonthValue())
                            .deptId(deptId)
                            .clientId(collection.getClientId())
                            .sponsorId(contractBaseInfo.getProjSponsorUserId())
                            .contractCode(collection.getContractCode())
                            .phase(collection.getPhase())
                            .repayDate(collection.getPlanCollectionDate())
                            .rent(collection.getPlanCollectionAmount())
                            .principal(collection.getPrincipal())
                            .interest(collection.getInterest())
                            .bankName(pledgeInfo.bankName)
                            .bankAccountName(pledgeInfo.accountName)
                            .bankAccountNumber(pledgeInfo.accountNumber)
                            .pledgeId(pledgeInfo.getPledgeId())
                            .financingType(pledgeInfo.getFinancingType())
                            .build();
                    detail.setInitialData(JSONUtil.toJsonStr(detail));
                    detailList.add(detail);
                }
                if (isNotEmpty(detailList)) {
                    getBean(RentCollectionMonthDetailService.class).saveBatch(detailList);
                }
            }
            if (!deptMapCollection.isEmpty()) {

            }
        } catch (Exception e) {
            log.error("生成下个月租金支付通知表记录失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    private PledgeInfo ensurePledgeInfo(PledgeInfo indirectPledge, PledgeInfo directPledge) {
        if (Objects.isNull(indirectPledge) && Objects.isNull(directPledge)) {
            return null;
        } else if (Objects.isNull(directPledge)) {
            return indirectPledge;
        } else if (Objects.isNull(indirectPledge)) {
            return directPledge;
        } else {
            List<PledgeInfo> candidateList = ListUtil.toList(directPledge, indirectPledge);
            // 排序，监管为1，非监管为0，优先监管
            candidateList.sort(Comparator.comparing(PledgeInfo::getIsSupervise).reversed());
            return candidateList.get(0);
        }
    }

    private static List<RentPayNoticeProcessDTO> convertConfig(SystemConfig config) {
        try {
            List<RentPayNoticeProcessDTO> list = JSONUtil.toList(config.getConfigValue(), RentPayNoticeProcessDTO.class);
            if (CollUtil.isEmpty(list)) {
                throw new MithrasException("配置信息有误，请联系管理员处理！");
            }
            return list;
        } catch (Exception e) {
            log.error("配置信息有误，请联系管理员处理！error=>{}", e.getMessage(), e);
            throw new MithrasException("配置信息有误，请联系管理员处理！");
        }
    }


    /**
     * 批量查询合同的融资关联情况
     * 只查询融资未到期的
     *
     * @param contractIdList
     * @return
     */
    Map<Long, PledgeInfo> contractBankMapByFinancing(Collection<Long> contractIdList, Boolean isSupervise) {
        if (!contractIdList.isEmpty()) {
            //间接融资
            List<FundFinancingPledgeInfo> pledgeInfoList = getBean(FundFinancingPledgeInfoService.class).list(Wrappers.<FundFinancingPledgeInfo>lambdaQuery()
                    .in(FundFinancingPledgeInfo::getContractId, contractIdList)
                    .eq(Objects.nonNull(isSupervise), FundFinancingPledgeInfo::getIsSupervise, isSupervise)
            );
            if (!pledgeInfoList.isEmpty()) {
                List<FundFinancingBaseInfo> fundFinancingBaseInfos = getBean(FundFinancingBaseInfoService.class).listByIds(
                        pledgeInfoList.stream().map(FundFinancingPledgeInfo::getFinancingId).collect(Collectors.toList()));
                //过滤，有效的融资记录
                List<Long> financingIds = fundFinancingBaseInfos.stream()
                        .filter(e -> equalsAny(e.getFinancingStatus(), EFFECT.name(), CARRY_INTEREST.name()))
                        .map(FundFinancingBaseInfo::getId).collect(Collectors.toList());
                //借据
                if (!financingIds.isEmpty()) {
                    List<Long> receiptRepayIds = getBean(FundReceiptRepayBaseInfoService.class).list(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                            .in(FundReceiptRepayBaseInfo::getFinancingId, financingIds)
                            .isNull(FundReceiptRepayBaseInfo::getFinancingType)
                    ).stream().map(FundReceiptRepayBaseInfo::getId).collect(Collectors.toList());
                    if (!receiptRepayIds.isEmpty()) {
                        //现金流，继续过滤，融资未到期的记录，
                        QueryWrapper<FundReceiptRepayCashFlow> wrapper = new QueryWrapper<>();
                        wrapper.in("receipt_repay_id", receiptRepayIds);
                        wrapper.groupBy("financing_id");
                        wrapper.select("max(repay_date) as repay_date, financing_id");
                        Map<Long, LocalDate> financingMapRepayDate = getBean(FundReceiptRepayCashFlowService.class).list(wrapper)
                                .stream()
                                .filter(e -> !e.getRepayDate().isBefore(LocalDate.now()))
                                .collect(Collectors.toMap(FundReceiptRepayCashFlow::getFinancingId, FundReceiptRepayCashFlow::getRepayDate));
                        //最终过滤 这里需要判断优先取监管户
                        Map<Long, PledgeInfo> contractBankMap = pledgeInfoList.stream()
                                .filter(e -> financingMapRepayDate.containsKey(e.getFinancingId()))
                                // 在这里就要判断监管户了
                                .collect(Collectors.groupingBy(FundFinancingPledgeInfo::getContractId))
                                .entrySet().stream().map(e -> {
                                    List<FundFinancingPledgeInfo> value = e.getValue();
                                    if (CollUtil.isNotEmpty(value) && value.size() > 1) {
                                        // 尝试寻找监管户，没有的话，就随机取一个
                                        long count = value.stream().filter(a -> Objects.equals(a.getIsSupervise(), Boolean.TRUE)).count();
                                        if (count > 0) {
                                            return value.stream().filter(a -> Objects.equals(a.getIsSupervise(), Boolean.TRUE)).findFirst().get();
                                        } else {
                                            return value.get(0);
                                        }
                                    } else {
                                        return value.get(0);
                                    }
                                }).map(e -> new PledgeInfo().setPledgeId(e.getId())
                                        .setFinancingType(indirect.name())
                                        .setBankName(e.getAccountBank())
                                        .setAccountName(e.getAccountName())
                                        .setContractId(e.getContractId())
                                        .setAccountNumber(e.getAccountNumber())
                                        .setIsSupervise(Objects.equals(e.getIsSupervise(), Boolean.TRUE) ? YesOrNoNumberEnum.YES.getCode() : YesOrNoNumberEnum.NO.getCode()))
                                .collect(Collectors.toMap(e -> e.contractId, e -> e, (v1, v2) -> v1));
                        return contractBankMap;
                    }
                }
            }
        }
        return MapUtil.empty();
    }

    /**
     * 批量查询合同的融资关联情况
     * 只查询融资未到期的
     *
     * @param contractIdList
     * @return
     */
    Map<Long, PledgeInfo> contractBankMapByDirectFinancing(Collection<Long> contractIdList, Boolean isSupervise) {
        if (!contractIdList.isEmpty()) {
            //直接融资
            List<FundDirectFinancingPledgeInfo> pledgeInfoList = getBean(FundDirectFinancingPledgeInfoService.class).list(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery()
                    .in(FundDirectFinancingPledgeInfo::getContractId, contractIdList)
                    .eq(Objects.nonNull(isSupervise), FundDirectFinancingPledgeInfo::getIsSupervise, isSupervise)
            );
            if (!pledgeInfoList.isEmpty()) {
                List<FundDirectFinancingBaseInfo> fundFinancingBaseInfos = getBean(FundDirectFinancingBaseInfoService.class).listByIds(
                        pledgeInfoList.stream().map(FundDirectFinancingPledgeInfo::getFinancingId).collect(Collectors.toList()));
                //过滤，有效的融资记录
                List<Long> financingIds = fundFinancingBaseInfos.stream()
                        .filter(e -> equalsAny(e.getFinancingStatus(), CARRY_INTEREST.name()))
                        .map(FundDirectFinancingBaseInfo::getId).collect(Collectors.toList());
                if (!financingIds.isEmpty()) {

                    //借据
                    List<Long> receiptRepayIds = getBean(FundReceiptRepayBaseInfoService.class).list(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                            .in(FundReceiptRepayBaseInfo::getFinancingId, financingIds)
                            .eq(FundReceiptRepayBaseInfo::getFinancingType, "DIRECT")
                    ).stream().map(FundReceiptRepayBaseInfo::getId).collect(Collectors.toList());
                    if (!receiptRepayIds.isEmpty()) {
                        //现金流，继续过滤，融资未到期的记录，
                        QueryWrapper<FundReceiptRepayCashFlow> wrapper = new QueryWrapper<>();
                        wrapper.in("receipt_repay_id", receiptRepayIds);
                        wrapper.groupBy("financing_id");
                        wrapper.select("max(repay_date) as repay_date, financing_id");
                        Map<Long, LocalDate> financingMapRepayDate = getBean(FundReceiptRepayCashFlowService.class).list(wrapper)
                                .stream()
                                .filter(e -> !e.getRepayDate().isBefore(LocalDate.now()))
                                .collect(Collectors.toMap(FundReceiptRepayCashFlow::getFinancingId, FundReceiptRepayCashFlow::getRepayDate));
                        //最终过滤
                        Map<Long, PledgeInfo> contractBankMap = pledgeInfoList.stream()
                                .filter(e -> financingMapRepayDate.containsKey(e.getFinancingId()))
                                .map(e -> new PledgeInfo().setPledgeId(e.getId())
                                        .setFinancingType(direct.name())
                                        .setBankName(e.getAccountBank())
                                        .setAccountName(e.getAccountName())
                                        .setContractId(e.getContractId())
                                        .setAccountNumber(e.getAccountNumber())
                                        .setIsSupervise(Objects.equals(e.getIsSupervise(), Boolean.TRUE) ? YesOrNoNumberEnum.YES.getCode() : YesOrNoNumberEnum.NO.getCode()))
                                .collect(Collectors.toMap(e -> e.contractId, e -> e, (v1, v2) -> v1));
                        return contractBankMap;
                    }
                }
            }
        }
        return MapUtil.empty();
    }

    public BaseDataBankAccount defaultBank() {
        //产品要求直接写死
        BaseDataBankAccount bank = new BaseDataBankAccount();
        bank.setAccountNumber("1202 0212 1990 0394 595");
        bank.setAccountBank("中国工商银行杭州市武林支行");
        bank.setAccountName("浙江浙商融资租赁有限公司");
        return bank;
       /* return getBean(BaseDataBankAccountService.class).getOne(Wrappers.<BaseDataBankAccount>lambdaQuery()
                .eq(BaseDataBankAccount::getAccountStatus, BaseDataBankAccountStatusEnum.NORMAL.name())
                .eq(BaseDataBankAccount::getAccountBank, "中国工商银行杭州市武林支行")
                .last(mysqlLimitOne())
        );*/
    }

    @Data
    @Accessors(chain = true)
    public static class PledgeInfo {
        //indirect，direct
        private String financingType;
        private Long contractId;
        private Long pledgeId;
        private String bankName;
        private String accountName;
        private String accountNumber;
        private Integer isSupervise;
    }

    public enum FinancingType {
        indirect, direct
    }
}
