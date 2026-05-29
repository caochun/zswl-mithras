//package cn.zswltech.mithras.service.job;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.date.StopWatch;
//import cn.zswltech.mithras.service.config.redis.RedisDistLock;
//import cn.zswltech.mithras.common.constant.VersionTypeConstants;
//import cn.zswltech.mithras.service.mapper.model.contract.ContractReceipt;
//import cn.zswltech.mithras.service.mapper.model.contract.ContractRentActual;
//import cn.zswltech.mithras.service.mapper.model.contract.ContractRentActualLib;
//import cn.zswltech.mithras.service.mapper.model.ftp.FtpInterestBaseInfo;
//import cn.zswltech.mithras.service.mapper.model.payment.FtpAssessmentInfo;
//import cn.zswltech.mithras.service.mapper.model.payment.FtpPriceInfo;
//import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
//import cn.zswltech.mithras.service.others.MithrasException;
//import cn.zswltech.mithras.service.service.contract.ContractReceiptService;
//import cn.zswltech.mithras.service.service.ftp.FtpInterestBaseInfoService;
//import cn.zswltech.mithras.service.service.lib.contract.ContractRentActualLibService;
//import cn.zswltech.mithras.service.service.payment.FtpAssessmentInfoService;
//import cn.zswltech.mithras.service.service.payment.FtpPriceInfoService;
//import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.xxl.job.core.handler.annotation.XxlJob;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.time.LocalDate;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
///**
// * @author yangxiong
// * @date 2024/8/22/14:15
// * @description
// */
//@Slf4j
//@Component
//public class FtpPriceTableInitJob {
//
//    @Resource
//    private RedisDistLock redisDistLock;
//    @Resource
//    private FtpPriceInfoService ftpPriceInfoService;
//    @Resource
//    private PaymentBaseInfoService paymentBaseInfoService;
//    @Resource
//    private ContractReceiptService contractReceiptService;
//    @Resource
//    private ContractRentActualLibService contractRentActualLibService;
//    @Resource
//    private FtpInterestBaseInfoService ftpInterestBaseInfoService;
//    @Resource
//    private FtpAssessmentInfoService ftpAssessmentInfoService;
//
//    @XxlJob(value = "initFtpPriceTable")
//    public void initFtpPriceTable() {
//        try {
//            StopWatch stopWatch = new StopWatch();
//            stopWatch.start();
//            boolean initFtpPriceTable = redisDistLock.tryLockWithoutReleaseTime("initFtpPriceTable", 2);
//            if (!initFtpPriceTable) {
//                log.error("初始化Ftp价格表-获取分布式锁失败，请稍后再试");
//                throw new MithrasException("初始化Ftp价格表-获取分布式锁失败，请稍后再试");
//            }
//            log.info("开始初始化Ftp价格表..............");
//            List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoService.list();
//            Map<Long, PaymentBaseInfo> paymentBaseInfoMap = paymentBaseInfos.stream().collect(Collectors.toMap(PaymentBaseInfo::getReceiptIdFinal, Function.identity(), (v1, v2) -> v1));
//            Map<Long, ContractReceipt> conreceiptMap = contractReceiptService.list(Wrappers.<ContractReceipt>lambdaQuery()
//                    .in(ContractReceipt::getId, paymentBaseInfos.stream().map(PaymentBaseInfo::getReceiptIdFinal).collect(Collectors.toSet()))
//            ).stream().collect(Collectors.toMap(ContractReceipt::getId, Function.identity(), (v1, v2) -> v1));
//
//            Map<Long, FtpInterestBaseInfo> ftpInterestBaseInfoMap = ftpInterestBaseInfoService.list(Wrappers.<FtpInterestBaseInfo>lambdaQuery()
//                    .in(FtpInterestBaseInfo::getReceiptId, paymentBaseInfos.stream().map(PaymentBaseInfo::getReceiptIdFinal).collect(Collectors.toSet()))
//            ).stream().collect(Collectors.toMap(FtpInterestBaseInfo::getReceiptId, Function.identity(), (v1, v2) -> v1));
//            paymentBaseInfoMap.forEach((receiptId, paymentBaseInfo) -> {
//                if (Objects.isNull(paymentBaseInfo.getReceiptIdFinal())) {
//                    log.error("FTP计息任务-未找到付款申请信息，借据编号: {}", paymentBaseInfo.getReceiptCode());
//                    return;
//                }
//                ContractReceipt contractReceipt = conreceiptMap.get(paymentBaseInfo.getReceiptIdFinal());
//                if (Objects.isNull(contractReceipt)) {
//                    log.error("FTP计息任务-未找到合同收款信息，借据编号: {}", paymentBaseInfo.getReceiptCode());
//                    return;
//                }
//                FtpInterestBaseInfo ftpInterestBaseInfo = ftpInterestBaseInfoMap.get(paymentBaseInfo.getReceiptIdFinal());
//                if (Objects.isNull(ftpInterestBaseInfo)) {
//                    log.error("FTP计息任务-未找到Ftp计息基础信息，借据编号: {}", paymentBaseInfo.getReceiptCode());
//                    return;
//                }
//
//                // 拿到了基本信信息，找到实际租金表
//                List<ContractRentActualLib> contractRentActualLibs = contractRentActualLibService.list(Wrappers.<ContractRentActualLib>lambdaQuery()
//                        .eq(ContractRentActualLib::getReceiptId, paymentBaseInfo.getReceiptIdFinal())
//                        .eq(ContractRentActualLib::getVersionType, VersionTypeConstants.NORMAL)
//                        .isNotNull(ContractRentActual::getCashFlowCode)
//                );
//                if (CollUtil.isEmpty(contractRentActualLibs)) {
//                    log.error("FTP计息任务-未找到实际租金表，借据编号: {}", paymentBaseInfo.getReceiptCode());
//                    return;
//                }
//
//                Map<String, List<ContractRentActualLib>> contractRentVersionMap = contractRentActualLibs.stream().collect(Collectors.groupingBy(ContractRentActualLib::getVersion));
//                contractRentActualLibs.sort(Comparator.comparing(ContractRentActualLib::getVersion));
//                List<ContractRentActualLib> effectRentList = contractRentVersionMap.get(contractRentActualLibs.get(contractRentActualLibs.size() - 1).getVersion());
//
//                effectRentList.sort(Comparator.comparing(ContractRentActual::getCashFlowPhase));
//                ContractRentActualLib contractRentActualLib = effectRentList.get(effectRentList.size() - 1);
//
//                LocalDate startDate = LocalDate.of(2024, 8, 1);
//                List<FtpPriceInfo> needInsertPriceList = new ArrayList<>(128);
//                Long assessmentPrice = Long.valueOf(Optional.ofNullable(ftpInterestBaseInfo.getLastCashFtp()).orElse(0));
//                while (!startDate.isAfter(contractRentActualLib.getCashFlowDate())) {
//                    FtpPriceInfo ftpPriceInfo = new FtpPriceInfo();
//                    ftpPriceInfo.setOverduePrice(0L);
//                    ftpPriceInfo.setGuidePrice(assessmentPrice);
//                    ftpPriceInfo.setPledgePrice(0L);
//                    ftpPriceInfo.setPaymentId(paymentBaseInfo.getId());
//                    ftpPriceInfo.setRecordDate(startDate);
//                    ftpPriceInfo.setCostIsConfirmed(0);
//                    ftpPriceInfo.setHandAdjustment(0L);
//                    ftpPriceInfo.setAssessmentPrice(assessmentPrice);
//                    needInsertPriceList.add(ftpPriceInfo);
//                    startDate = startDate.plusDays(1);
//                }
//                // 构建FTP考核信息
//                FtpAssessmentInfo ftpAssessmentInfo = FtpAssessmentInfo.builder()
//                        .basePrice(assessmentPrice)
//                        .guidePrice(assessmentPrice)
//                        .pledgePrice(0L)
//                        .mountainAdjustment(0L)
//                        .gradeAdjustment(0L)
//                        .handAdjustment(0L)
//                        .ticketPrice(Long.valueOf(Optional.ofNullable(paymentBaseInfo.getBillFtp()).orElse(0)))
//                        .paymentId(paymentBaseInfo.getId())
//                        .assessmentPrice(assessmentPrice)
//                        .build();
//                ftpAssessmentInfoService.save(ftpAssessmentInfo);
//                if (CollUtil.isNotEmpty(needInsertPriceList)) {
//                    ftpPriceInfoService.saveBatch(needInsertPriceList);
//                }
//            });
//            stopWatch.stop();
//            stopWatch.prettyPrint(TimeUnit.SECONDS);
//            log.info("初始化Ftp价格表完成..............");
//        } catch (Exception e) {
//            log.error("初始化Ftp价格表失败，请稍后再试", e);
//            throw new MithrasException(e.getMessage());
//        } finally {
//            redisDistLock.unlock("initFtpPriceTable");
//        }
//    }
//}
