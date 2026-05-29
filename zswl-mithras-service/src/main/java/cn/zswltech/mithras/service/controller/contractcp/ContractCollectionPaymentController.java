package cn.zswltech.mithras.service.controller.contractcp;

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
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.contractcp.ContractCollectionPaymentApi;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.contractcp.*;
import cn.zswltech.mithras.dto.fund.RentPayNoticeProcessDTO;
import cn.zswltech.mithras.common.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.enums.contractcp.CashSelectTypeEnum;
import cn.zswltech.mithras.service.enums.payment.PaymentStatusEnum;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.service.mapper.SystemConfigMapper;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.SystemConfig;
import cn.zswltech.mithras.service.mapper.model.basedata.BaseDataBankAccount;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.service.mapper.model.process.prepare.RentCollectionMonthDetail;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayCashFlowService;
import cn.zswltech.mithras.service.service.lib.contractcp.ContractCollectionPaymentService;
import cn.zswltech.mithras.service.service.process.prepare.CommonProcessPrepareService;
import cn.zswltech.mithras.service.service.process.prepare.RentCollectionMonthDetailService;
import cn.zswltech.mithras.common.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;
import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.service.enums.CommonProcessPrepareStatus.PEND_COMMIT;
import static cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum.UNCOLLECTION;
import static cn.zswltech.mithras.service.enums.fund.financing.FundFinancingStatusEnum.CARRY_INTEREST;
import static cn.zswltech.mithras.service.enums.fund.financing.FundFinancingStatusEnum.EFFECT;
import static cn.zswltech.mithras.service.job.NextMonthRentNotify.FinancingType.direct;
import static cn.zswltech.mithras.service.job.NextMonthRentNotify.FinancingType.indirect;
import static java.util.stream.Collectors.groupingBy;

/**
 * @create: 2022-08-17
 **/

@RestController
@Slf4j
public class ContractCollectionPaymentController implements ContractCollectionPaymentApi {

    @Resource
    private ContractCollectionPaymentService contractCollectionPaymentService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;

    @Resource
    private HttpServletResponse httpServletResponse;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    public R<PageR<ContractCollectionPaymentListRSP>> list(@Valid ContractCollectionPaymentListREQ req) {
        return R.ok(contractCollectionPaymentService.list(req));
    }

    @Override
    public void exportList(ContractCollectionPaymentListREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("合同收付款列表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            contractCollectionPaymentService.exportList(req, httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出合同收付款列表发生未知异常", e);
            throw new MithrasException("导出合同收付款列表发生未知异常");
        }
    }

    /**
     * 发送租金支付通知书
     * @param req
     */
    @Override
    public R<Void> pushRentNotify(ContractCollectionPaymentListREQ req){
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            try {
                LocalDateTime currentMonthLastMoment;//当前月最后
                LocalDateTime nextMonthLastMoment;//下个月最后
                //1、确定时间范围
                currentMonthLastMoment = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).withDayOfMonth(1).plusMonths(1).minusSeconds(1);
                nextMonthLastMoment = currentMonthLastMoment.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
                //2、获取数据(>本月最后一秒&&<=下个月最后一秒 的 未收款的租金流水信息)
                List<CollectionBaseInfo> nextMonthRentList = getBean(CollectionBaseInfoMapper.class).selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getWriteOffStatus, UNCOLLECTION.name())
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                        .gt(CollectionBaseInfo::getPlanCollectionDate, currentMonthLastMoment)
                        .le(CollectionBaseInfo::getPlanCollectionDate, nextMonthLastMoment)
                );

                //2.1 过滤已经生成的记录（用下个月年、月和流水id，判断是否已经生成过记录）
                List<Long> existId = getBean(RentCollectionMonthDetailService.class).
                        queryByIdList(
                                nextMonthRentList.stream().map(CollectionBaseInfo::getId).collect(Collectors.toList()),
                                nextMonthLastMoment);
                if (isNotEmpty(existId)) {
                    //如果存在记录过的信息，从查询的所有下月还款的流水中去掉
                    nextMonthRentList = nextMonthRentList.stream().filter(e -> !existId.contains(e.getId())).collect(Collectors.toList());
                }
                if (CollectionUtil.isEmpty(nextMonthRentList)) {
                    throw new MithrasException("没有待发送的次月租金支付通知书！");
                }
                //3、按照部门进行分组，并生成记录；
                //查询合同信息，并生成Map<合同id,合同>集合
                Map<Long, ContractBaseInfo> contractMap = getBean(ContractBaseInfoService.class).listByIds(
                        nextMonthRentList.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toList())
                ).stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e, (v1, v2) -> v1));
                //查询租金信息，并生成Map<部门id,租金>集合
                Map<Long, List<CollectionBaseInfo>> deptMapCollection =
                        nextMonthRentList.stream().collect(groupingBy(e -> contractMap.get(e.getContractId()).getBizDeptId()));
                //查询部门信息，并生成Map<部门id，部门名称>集合
                Map<Long, String> deptIdMapName = getBean(OrgDOMapper.class)
                        .selectByIds(new ArrayList<>(deptMapCollection.keySet()), null)
                        .stream().collect(Collectors.toMap(OrgDO::getId, OrgDO::getName, (v1, v2) -> v1));
                //查询系统配置信息(租金支付通知流程-部门人员配置关系)
                SystemConfig config = getBean(SystemConfigMapper.class).selectOne(Wrappers.<SystemConfig>lambdaQuery()
                        .select(SystemConfig::getConfigValue)
                        .eq(SystemConfig::getConfigKey, "rentPayNoticeProcess")
                        .eq(SystemConfig::getStatus, YesOrNoNumberEnum.YES.getCode())
                        .last(StringUtil.mysqlLimitOne()));
                if (ObjectUtil.isNull(config) || CharSequenceUtil.isBlank(config.getConfigValue())) {
                    log.error("缺少业务需要的核心配置，请联系管理员配置！");
                    throw new MithrasException("缺少业务需要的核心配置，请联系管理员配置！");
                }
                //将查询的通知流程信息转成集合
                List<RentPayNoticeProcessDTO> rentPayNoticeProcessDTOList = convertConfig(config);
                //生成  部门-人员List  集合
                Map<String, List<Long>> deptCodeUserIdMap = rentPayNoticeProcessDTOList.stream()
                        .collect(Collectors.toMap(RentPayNoticeProcessDTO::getDeptCode, RentPayNoticeProcessDTO::getUserIds));
                //合同融资关联
                //间融
                Map<Long, PledgeInfo> contractPledgeMap = contractBankMapByFinancing(contractMap.keySet(), true);
                //直融
                Map<Long, PledgeInfo> directContractPledgeMap = contractBankMapByDirectFinancing(contractMap.keySet(), true);
                //默认银行账户
                BaseDataBankAccount defaultBank = defaultBank();
                //3.2、部门逐一处理
                LocalDateTime applyTime = LocalDateTime.now();
                for (Long deptId : deptMapCollection.keySet()) {
                    OrgDO orgDO = getBean(OrgDOMapper.class).selectByPrimaryKey(deptId);
                    if (ObjectUtil.isNull(orgDO)) {
                        log.error("id为{}的部门不存在", deptId);
                        throw new MithrasException("id为{"+deptId+"}的部门不存在");
                    }
                    List<Long> assigneeIds = deptCodeUserIdMap.get(orgDO.getCode());
                    if (CollUtil.isEmpty(assigneeIds)) {
                        log.error("「{}」租金支付通知配置有误，请联系管理员处理", orgDO.getName());
                        throw new MithrasException("「{"+orgDO.getName()+"}」租金支付通知配置有误，请联系管理员处理");
                    }
                    //向common_process_prepare表中插入数据
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
                    //向rent_collection_month_detail表中插入数据
                    //根据部门id从集合中取租金信息
                    List<CollectionBaseInfo> collectionList = deptMapCollection.get(deptId);
                    List<RentCollectionMonthDetail> detailList = new ArrayList<>(collectionList.size());
                    //循环取租金信息
                    for (CollectionBaseInfo collection : collectionList) {
                        //从租金信息中取合同id，再取合同信息
                        ContractBaseInfo contractBaseInfo = contractMap.get(collection.getContractId());
                        //处理直融、间融信息
                        PledgeInfo pledgeInfo = this.ensurePledgeInfo(contractPledgeMap.get(collection.getContractId()), directContractPledgeMap.get(collection.getContractId()));
                        //如果不存在融资信息，使用默认的银行账户信息
                        if (pledgeInfo == null) {
                            pledgeInfo = new PledgeInfo()
                                    .setBankName(defaultBank.getAccountBank())
                                    .setAccountName(defaultBank.getAccountName())
                                    .setAccountNumber(defaultBank.getAccountNumber());
                        }
                        //向表中插入数据
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
            } catch (Exception e) {
                transactionStatus.setRollbackOnly();
                log.error("发送次月租金支付通知书失败", e);
                throw e;
            }
        });
        return R.ok();
    }

    @Override
    public R<List<SelectRSP>> contractList(@Valid ContractcpContractDetailREQ req) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(req.getContractId());
        List<String> lists = Arrays.asList(ContractStatus.TAKE_EFFECT.name(), ContractStatus.START_RENT.name(), ContractStatus.SETTLE.name());
        List<ContractBaseInfo> list = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery().select(ContractBaseInfo::getId,ContractBaseInfo::getContractCode)
                .eq(ContractBaseInfo::getProjReviewId, contractBaseInfo.getProjReviewId())
                .in(ContractBaseInfo::getContractStatus,lists));
        List<SelectRSP> rsps = list.stream().map(e -> new SelectRSP(e.getContractCode(), e.getId().toString())).collect(Collectors.toList());
        return R.ok(rsps);
    }

    @Override
    public R<ContractInfoRSP> contractDetail(@Valid ContractcpContractDetailREQ req) {
        return R.ok(contractCollectionPaymentService.contractInfo(req));
    }

    @Override
    public R<List<SelectRSP>> cashList(@Valid ContractcpContractDetailREQ req) {
        List<SelectRSP> select= new ArrayList<>(Arrays.stream(CashSelectTypeEnum.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList()));
        List<PaymentBaseInfo> infos = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().select(PaymentBaseInfo::getReceiptCode)
                .eq(PaymentBaseInfo::getContractId, req.getContractId()).in(PaymentBaseInfo::getPaymentStatus, Arrays.asList(PaymentStatusEnum.TAKE_EFFECT.name(), PaymentStatusEnum.FINISHED.name()))
                .isNotNull(PaymentBaseInfo::getReceiptCode));
        List<String> codes = infos.stream().map(PaymentBaseInfo::getReceiptCode).distinct().collect(Collectors.toList());
        for (String code : codes) {
            SelectRSP tmp = new SelectRSP();
            tmp.setLabel(code);
            tmp.setValue(code);
            select.add(tmp);
        }
        return R.ok(select);
    }

    @Override
    public R<PageR<ContractRentActualInfoRSP>> cashDetail(@Valid ContractCollectionPaymentDetailREQ req) {
        return R.ok(contractCollectionPaymentService.cashDetail(req));
    }

    @Override
    public R<Void> exportCashDetail(ContractCollectionPaymentDetailExportREQ req) {
        try {
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("现金流明细" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            contractCollectionPaymentService.exportCashDetail(req, httpServletResponse.getOutputStream());
            return R.ok();
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出现金流明细发生未知异常", e);
            return R.fail("导出现金流明细发生未知异常");
        }
    }

    /**
     * 查询系统配置信息(将流程JSON信息转成List集合)
     * @param config
     * @return
     */
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
     * 批量查询合同的融资关联情况（间融）
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
     * 批量查询合同的融资关联情况（直融）
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

    /**
     * 初始化默认的银行账户信息
     * @return
     */
    public BaseDataBankAccount defaultBank() {
        //产品要求直接写死
        BaseDataBankAccount bank = new BaseDataBankAccount();
        bank.setAccountNumber("1202 0212 1990 0394 595");
        bank.setAccountBank("中国工商银行杭州市武林支行");
        bank.setAccountName("浙江浙商融资租赁有限公司");
        return bank;
    }

    /**
     * 处理直融、间融信息：返回不为空的融资信息，如果都有数据则进行排序
     * @param indirectPledge
     * @param directPledge
     * @return
     */
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

    @Data
    @Accessors(chain = true)
    public static class PledgeInfo {
        private String financingType;
        private Long contractId;
        private Long pledgeId;
        private String bankName;
        private String accountName;
        private String accountNumber;
        private Integer isSupervise;
    }
}
