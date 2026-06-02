//package cn.zswltech.mithras.service.service.payment;
//
//import cn.hutool.core.bean.BeanUtil;
//import cn.hutool.core.date.DatePattern;
//import cn.hutool.core.text.CharSequenceUtil;
//import cn.zswltech.mithras.api.common.PageR;
//import cn.zswltech.mithras.api.payment.dto.FtpPriceCheckREQ;
//import cn.zswltech.mithras.api.payment.dto.FtpPriceListREQ;
//import cn.zswltech.mithras.api.payment.dto.FtpPriceListRSP;
//import cn.zswltech.mithras.api.payment.dto.FtpPriceUpdateREQ;
//import cn.zswltech.mithras.dto.flow.search.ProcessListREQ;
//import cn.zswltech.mithras.dto.flow.search.ProcessListRSP;
//import cn.zswltech.mithras.service.constant.ResultMsg;
//import cn.zswltech.mithras.service.constant.VersionTypeConstants;
//import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
//import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
//import cn.zswltech.mithras.service.enums.payment.PaymentStatusEnum;
//import cn.zswltech.mithras.service.job.FtpInterestJob;
//import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
//import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceiptLib;
//import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
//import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActualLib;
//import cn.zswltech.mithras.service.mapper.model.finance.FinanceProjectProfit;
//import cn.zswltech.mithras.service.mapper.model.finance.FinanceProjectProfitDetail;
//import cn.zswltech.mithras.ftp.oldftp.model.FtpInterestBaseInfo;
//import cn.zswltech.mithras.service.mapper.model.payment.FtpAssessmentInfo;
//import cn.zswltech.mithras.service.mapper.model.payment.FtpPriceInfo;
//import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
//import cn.zswltech.mithras.service.mapper.payment.FtpPriceInfoMapper;
//import cn.zswltech.mithras.service.others.MithrasException;
//import cn.zswltech.mithras.service.service.finance.FinanceProjectProfitDetailService;
//import cn.zswltech.mithras.service.service.finance.FinanceProjectProfitService;
//import cn.zswltech.mithras.service.service.flow.MyTaskService;
//import cn.zswltech.mithras.service.service.ftp.FtpInterestBaseInfoService;
//import cn.zswltech.mithras.service.service.ftp.FtpInterestDetailRecordService;
//import cn.zswltech.mithras.contract.service.lib.contract.ContractReceiptLibService;
//import cn.zswltech.mithras.contract.service.lib.contract.ContractRentActualLibService;
//import cn.zswltech.mithras.service.util.StringUtil;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.annotation.Resource;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.time.temporal.TemporalAdjusters;
//import java.util.*;
//import java.util.concurrent.CompletableFuture;
//import java.util.stream.Collectors;
//
///**
// * @author yangxiong
// * @description 针对表【ftp_price_info(FTP价格表)】的数据库操作Service
// * @createDate 2024-08-12 16:42:44
// */
//@Slf4j
//@Service
//public class FtpPriceInfoService extends ServiceImpl<FtpPriceInfoMapper, FtpPriceInfo> {
//
//    @Resource
//    private FtpPriceInfoService thisService;
//    @Resource
//    private FtpInterestJob ftpInterestJob;
//    @Resource
//    private MyTaskService myTaskService;
//    @Resource
//    private FtpAssessmentInfoService assessmentInfoService;
//    @Resource
//    private PaymentBaseInfoService paymentBaseInfoService;
//    @Resource
//    private FtpInterestBaseInfoService ftpInterestBaseInfoService;
//    @Resource
//    private FinanceProjectProfitService financeProjectProfitService;
//    @Resource
//    private FtpInterestDetailRecordService ftpInterestDetailRecordService;
//    @Resource
//    private ContractReceiptLibService contractReceiptLibService;
//    @Resource
//    private ContractRentActualLibService contractRentActualLibService;
//
//    public PageR<FtpPriceListRSP> pageList(FtpPriceListREQ req) {
//        PaymentBaseInfo paymentBaseInfo = null;
//        if (CharSequenceUtil.isNotBlank(req.getReceiptCode())) {
//            ContractReceiptLib receiptLib = contractReceiptLibService.getOne(Wrappers.<ContractReceiptLib>lambdaQuery()
//                    .eq(ContractReceiptLib::getVersionType, VersionTypeConstants.NORMAL)
//                    .eq(ContractReceipt::getReceiptCode, req.getReceiptCode())
//                    .orderByDesc(ContractReceiptLib::getVersion)
//                    .last(StringUtil.mysqlLimitOne()));
//            if (Objects.nonNull(receiptLib)) {
//                paymentBaseInfo = paymentBaseInfoService.getOne(Wrappers.<PaymentBaseInfo>lambdaQuery()
//                        .eq(PaymentBaseInfo::getReceiptIdFinal, receiptLib.getOriginId())
//                        .last(StringUtil.mysqlLimitOne()));
//                if (Objects.isNull(paymentBaseInfo)) {
//                    // 这里是在不行就返回全量的数据，不影响正常的使用，打印日志兜底
//                    log.error("借据：{}不存在生效的付款申请", req.getReceiptCode());
//                }
//            }
//        }
//        Page<FtpPriceInfo> page = new Page<>(req.getPage(), req.getPageSize());
//        Page<FtpPriceInfo> ftpPriceInfoPage = baseMapper.selectPage(page, Wrappers.<FtpPriceInfo>lambdaQuery()
//                .ge(Objects.nonNull(req.getRecordDateFrom()), FtpPriceInfo::getRecordDate, req.getRecordDateFrom())
//                .le(Objects.nonNull(req.getRecordDateTo()), FtpPriceInfo::getRecordDate, req.getRecordDateTo())
//                .eq(Objects.nonNull(paymentBaseInfo), FtpPriceInfo::getPaymentId, Optional.ofNullable(paymentBaseInfo).map(PaymentBaseInfo::getId).orElse(null))
//                .eq(Objects.nonNull(req.getCostIsConfirmed()), FtpPriceInfo::getCostIsConfirmed, req.getCostIsConfirmed()));
//        if (ftpPriceInfoPage.getRecords().isEmpty()) {
//            return PageR.empty(page.getCurrent(), page.getSize());
//        }
//        // 构建返回结果
//        List<FtpPriceListRSP> rspList = ftpPriceInfoPage.getRecords().stream().map(dto -> {
//            FtpPriceListRSP rsp = new FtpPriceListRSP();
//            BeanUtil.copyProperties(dto, rsp);
//            return rsp;
//        }).collect(Collectors.toList());
//        return PageR.of(rspList, ftpPriceInfoPage.getTotal());
//    }
//
//    @Transactional(rollbackFor = Throwable.class)
//    public void updateRecord(FtpPriceUpdateREQ req) {
//        // 校验原始记录是否存在
//        FtpPriceCheckREQ checkReq = new FtpPriceCheckREQ();
//        checkReq.setId(req.getId());
//        checkReq.setRecordDate(req.getRecordDate());
//        Boolean needCalculate = check(checkReq);
//
//        FtpPriceInfo priceInfo = thisService.getById(req.getId());
//        if (Objects.isNull(priceInfo)) {
//            throw new MithrasException("ftp价格表原本" + ResultMsg.RECORD_NOT_EXIST);
//        }
//        // 这里需要更新当前日期后面的所有记录
//        thisService.lambdaUpdate()
//                .eq(FtpPriceInfo::getPaymentId, priceInfo.getPaymentId())
//                .ge(FtpPriceInfo::getRecordDate, req.getRecordDate())
//                .set(FtpPriceInfo::getAssessmentPrice, req.getPledgePrice() + req.getHandAdjustment() + req.getGuidePrice() + req.getOverduePrice())
//                .set(FtpPriceInfo::getGuidePrice, req.getGuidePrice())
//                .set(FtpPriceInfo::getHandAdjustment, req.getHandAdjustment())
//                .set(FtpPriceInfo::getOverduePrice, req.getOverduePrice())
//                .set(FtpPriceInfo::getPledgePrice, req.getPledgePrice())
//                .set(FtpPriceInfo::getRemark, req.getRemark())
//                .update();
//        log.info("ftp价格表修改从{}开始，paymentId = {}的所有记录", req.getRecordDate(), priceInfo.getPaymentId());
//
//        // 判断是否需要更新成本是否确认
//        if (Boolean.TRUE.equals(needCalculate)) {
//            asyncCalculateFtpInterest(req, priceInfo);
//        }
//    }
//
//    public void asyncCalculateFtpInterest(FtpPriceUpdateREQ req, FtpPriceInfo priceInfo) {
//        // 首先需要找到最新的FTP计息已确认日期
//        CompletableFuture.runAsync(() -> {
//            try {
//                FinanceProjectProfit projectProfit = financeProjectProfitService.getOne(Wrappers.<FinanceProjectProfit>lambdaQuery()
//                        .eq(FinanceProjectProfit::getIsConfirmed, YesOrNoNumberEnum.YES.getCode())
//                        .orderByDesc(FinanceProjectProfit::getYear)
//                        .orderByDesc(FinanceProjectProfit::getMonth)
//                        .last(StringUtil.mysqlLimitOne()));
//                if (Objects.isNull(projectProfit)) {
//                    throw new MithrasException("项目利润表不存在已经生效的数据");
//                }
//                PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(priceInfo.getPaymentId());
//                if (Objects.isNull(paymentBaseInfo)) {
//                    throw new MithrasException("paymentBaseInfo不存在");
//                }
//                if (Objects.isNull(paymentBaseInfo.getReceiptIdFinal())) {
//                    throw new MithrasException(paymentBaseInfo.getPaymentCode() + "不存在生效的借据");
//                }
//                // 找到FTP计息的基本信息
//                FtpInterestBaseInfo interestBaseInfo = ftpInterestBaseInfoService.getOne(Wrappers.<FtpInterestBaseInfo>lambdaQuery()
//                        .eq(FtpInterestBaseInfo::getReceiptId, paymentBaseInfo.getReceiptIdFinal())
//                        .last(StringUtil.mysqlLimitOne()));
//                if (Objects.isNull(interestBaseInfo)) {
//                    throw new MithrasException("ftp计息基本信息不存在");
//                }
//                //这里的日期只能是已经确认的FTP的最新详情的日期
//                LocalDate endDate = interestBaseInfo.getLastUpdateDate();
//
//                log.info("ftp价格表修改：FTP计息任务重跑入参：paymentId = {}, receiptId = {}, recordDate = {}, endDate = {}",
//                        priceInfo.getPaymentId(), interestBaseInfo.getReceiptId(), req.getRecordDate(), endDate);
//                ftpInterestJob.calculateFtpInterest(interestBaseInfo.getId(), req.getRecordDate(), endDate);
//                log.info("ftp价格表修改：FTP计息任务重跑成功");
//            } catch (Exception e) {
//                log.error("ftp价格表修改：FTP计息任务重跑失败，需要手动重跑", e);
//            }
//        });
//    }
//
//    public Boolean check(FtpPriceCheckREQ req) {
//        FtpPriceInfo priceInfo = thisService.getById(req.getId());
//        if (Objects.isNull(priceInfo)) {
//            throw new MithrasException("ftp价格表原本" + ResultMsg.RECORD_NOT_EXIST);
//        }
//        // 找到当前所选时间的项目利润表状态
//        FinanceProjectProfit projectProfit = financeProjectProfitService.getOne(Wrappers.<FinanceProjectProfit>lambdaQuery()
//                .eq(FinanceProjectProfit::getYear, req.getRecordDate().getYear())
//                .eq(FinanceProjectProfit::getMonth, req.getRecordDate().getMonthValue())
//                .last(StringUtil.mysqlLimitOne()));
//        if (Objects.isNull(projectProfit)) {
//            return false;
//        }
//        // 判断项目利润表是否确认
//        return Objects.equals(projectProfit.getIsConfirmed(), YesOrNoNumberEnum.YES.getCode());
//    }
//
//    /**
//     * 接收一个年月，yyyy-MM，修改该月份的FTP价格表是否确认为是
//     */
//    public void confirmFtpPrice(String yearMonth) {
//        try {
//            LocalDate localDate = LocalDate.parse(yearMonth, DateTimeFormatter.ofPattern(DatePattern.NORM_MONTH_PATTERN));
//            LocalDate startDate = localDate.with(TemporalAdjusters.firstDayOfMonth());
//            LocalDate endDate = localDate.with(TemporalAdjusters.lastDayOfMonth());
//            thisService.lambdaUpdate()
//                    .ge(FtpPriceInfo::getRecordDate, startDate)
//                    .le(FtpPriceInfo::getRecordDate, endDate)
//                    .set(FtpPriceInfo::getCostIsConfirmed, YesOrNoNumberEnum.YES.getCode())
//                    .update();
//            log.info("ftp价格表修改：修改{}-{}的所有记录成本是否确认为是", startDate, endDate);
//        } catch (Exception e) {
//            log.error("ftp价格表修改：修改{}的所有FTP的价格表【是否确认】状态失败, 需要手动设置！， ", yearMonth, e);
//        }
//    }
//
//    public void initFtpPrice(String processInstanceId) {
//        try {
//            // 因为不知道是什么流程，所以先通过流程实例id获取对应的流程信息
//            ProcessListREQ req = new ProcessListREQ();
//            req.setProcessInstanceId(processInstanceId);
//            req.setPage(1);
//            req.setPageSize(1);
//            // 实际上有流程ID的话，只会查到一个数据
//            ProcessListRSP rsp = myTaskService.searchList(req).getList().get(0);
//            if (CharSequenceUtil.equalsAny(rsp.getModelKey(), ProcessModelTypeEnum.ContractStartRentFlow.name(),
//                    ProcessModelTypeEnum.ContractStartRentAutoFlow.name())) {
//                // 合同起租 实际只会存在一个借据
//                PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getOne(Wrappers.<PaymentBaseInfo>lambdaQuery()
//                        .eq(PaymentBaseInfo::getContractId, rsp.getBusinessKey())
//                        .isNotNull(PaymentBaseInfo::getReceiptId)
//                        .in(PaymentBaseInfo::getPaymentStatus, PaymentStatusEnum.TAKE_EFFECT.name(), PaymentStatusEnum.FINISHED.name())
//                        .last(StringUtil.mysqlLimitOne()));
//                if (Objects.isNull(paymentBaseInfo)) {
//                    throw new MithrasException("paymentBaseInfo不存在");
//                }
//                if (Objects.isNull(paymentBaseInfo.getReceiptIdFinal())) {
//                    throw new MithrasException(paymentBaseInfo.getPaymentCode() + "不存在生效的借据");
//                }
//                // 拿到生效的借据并找到实际租金表信息
//                ContractReceiptLib contractReceiptLib = contractReceiptLibService.getOne(Wrappers.<ContractReceiptLib>lambdaQuery()
//                        .eq(ContractReceiptLib::getOriginId, paymentBaseInfo.getReceiptIdFinal())
//                        .eq(ContractReceiptLib::getVersionType, VersionTypeConstants.NORMAL)
//                        .orderByDesc(ContractReceiptLib::getVersion)
//                        .last(StringUtil.mysqlLimitOne()));
//                invokeSave(contractReceiptLib);
//            }
//
//            if (CharSequenceUtil.equalsAny(rsp.getModelKey(), ProcessModelTypeEnum.ContractAddNewReceiptFlow.name(),
//                    ProcessModelTypeEnum.ContractAddNewReceiptAutoFlow.name())) {
//                // 新增借据 这时候流程的businessKey就是新借据的id
//                ContractReceiptLib contractReceiptLib = contractReceiptLibService.getOne(Wrappers.<ContractReceiptLib>lambdaQuery()
//                        .eq(ContractReceiptLib::getOriginId, Long.valueOf(rsp.getBusinessKey()))
//                        .eq(ContractReceiptLib::getVersionType, VersionTypeConstants.NORMAL)
//                        .orderByDesc(ContractReceiptLib::getVersion)
//                        .last(StringUtil.mysqlLimitOne()));
//                invokeSave(contractReceiptLib);
//            }
//        } catch (Exception e) {
//            log.error("ftp价格表修改：初始化FTP价格表失败, 需要手动设置！，processInstanceId = [{}] ", processInstanceId, e);
//        }
//    }
//
//    private void invokeSave(ContractReceiptLib contractReceiptLib) {
//        if (Objects.isNull(contractReceiptLib)) {
//            throw new MithrasException("contractReceiptLib不存在");
//        }
//        // 找到该借据对应的付款信息
//        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getOne(Wrappers.<PaymentBaseInfo>lambdaQuery()
//                .eq(PaymentBaseInfo::getReceiptIdFinal, contractReceiptLib.getOriginId())
//                .last(StringUtil.mysqlLimitOne()));
//        List<ContractRentActualLib> contractRentActualLibs = contractRentActualLibService.list(Wrappers.<ContractRentActualLib>lambdaQuery()
//                .eq(ContractRentActualLib::getVersion, contractReceiptLib.getVersion())
//                .eq(ContractRentActual::getReceiptId, contractReceiptLib.getOriginId()));
//        if (contractRentActualLibs.isEmpty()) {
//            throw new MithrasException("contractRentActualLibs不存在");
//        }
//        contractRentActualLibs.sort(Comparator.comparing(ContractRentActualLib::getCashFlowPhase));
//        // 拿到第一期和最后一期的应还日期
//        LocalDate startDate = contractRentActualLibs.get(0).getCashFlowDate();
//        LocalDate endDate = contractRentActualLibs.get(contractRentActualLibs.size() - 1).getCashFlowDate();
//        // 找到流程中的FTP信息
//        FtpAssessmentInfo assessmentInfo = assessmentInfoService.getOne(Wrappers.<FtpAssessmentInfo>lambdaQuery()
//                .eq(FtpAssessmentInfo::getPaymentId, paymentBaseInfo.getId())
//                .last(StringUtil.mysqlLimitOne()));
//        if (Objects.isNull(assessmentInfo)) {
//            throw new MithrasException("assessmentInfo不存在");
//        }
//        // 拿到日期之后，直接初始化FTP价格表
//        List<FtpPriceInfo> ftpPriceInfos = new ArrayList<>();
//        while (startDate.isBefore(endDate)) {
//            FtpPriceInfo ftpPriceInfo = new FtpPriceInfo();
//            ftpPriceInfo.setPaymentId(paymentBaseInfo.getId());
//            ftpPriceInfo.setRecordDate(startDate);
//            ftpPriceInfo.setGuidePrice(assessmentInfo.getGuidePrice());
//            ftpPriceInfo.setHandAdjustment(assessmentInfo.getHandAdjustment());
//            ftpPriceInfo.setPledgePrice(assessmentInfo.getPledgePrice());
//            ftpPriceInfo.setOverduePrice(0L);
//            ftpPriceInfo.setAssessmentPrice(assessmentInfo.getAssessmentPrice());
//            ftpPriceInfo.setRemark(assessmentInfo.getRemark());
//            ftpPriceInfo.setCostIsConfirmed(YesOrNoNumberEnum.NO.getCode());
//            ftpPriceInfos.add(ftpPriceInfo);
//            startDate = startDate.plusDays(1);
//        }
//        if (!ftpPriceInfos.isEmpty()) {
//            thisService.saveBatch(ftpPriceInfos);
//        }
//    }
//}
