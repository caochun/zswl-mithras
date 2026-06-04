package cn.zswltech.mithras.service.service.ftp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.ftp.*;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.job.FtpInterestJob;
import cn.zswltech.mithras.ftp.oldftp.mapper.FtpInterestBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceProjectProfit;
import cn.zswltech.mithras.ftp.oldftp.model.FtpInterestBaseInfo;
import cn.zswltech.mithras.ftp.oldftp.model.FtpInterestDetailRecord;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.FtpAssessmentInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractReceiptService;
import cn.zswltech.mithras.finance.service.FinanceProjectProfitService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/5/16
 * @description
 */
@Slf4j
@Service
public class FtpInterestBaseInfoService extends ServiceImpl<FtpInterestBaseInfoMapper, FtpInterestBaseInfo> {
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private FinanceProjectProfitService financeProjectProfitService;
    @Resource
    private FtpInterestDetailRecordService ftpInterestDetailRecordService;

    public void calculateFtpInterestTimeRangeWithDiff(FtpAssessmentInfo ftpAssessmentInfo, LocalDate interestDiffDate) {
        Long receiptId = ftpAssessmentInfo.getReceiptId();
        FtpInterestBaseInfo ftpInterestBaseInfo = this.getOneByReceiptId(receiptId);
        if (Objects.isNull(ftpInterestBaseInfo)) {
            log.error("借据不存在对应FTP计息任务[借据id:{}]", receiptId);
            throw new MithrasException("借据不存在对应FTP计息任务");
        }
        // 先查一下老的FTP计息之和，为后面钆差做准备
        List<FtpInterestDetailRecord> oldList = ftpInterestDetailRecordService.listSpecificByRange(ftpInterestBaseInfo.getId(), ftpAssessmentInfo.getEffectDate(), LocalDate.now().minusDays(1));
        // 去掉期初余额
        oldList.removeIf(e -> StrUtil.equals(e.getItemText(), "期初余额"));
        if (CollectionUtil.isEmpty(oldList)) {
            // 不存在老数据，无需钆差重算
            return;
        }
        oldList.sort(Comparator.comparing(FtpInterestDetailRecord::getInterestDate));
        LocalDate interestStartDate = oldList.get(0).getInterestDate();
        LocalDate interestEndDate = oldList.get(oldList.size() - 1).getInterestDate();
        // 计算新数据，除了FTP价格外其他都按原数据计算
        List<FtpInterestDetailRecord> newList = oldList.stream().map(e -> {
            FtpInterestDetailRecord newRecord = BeanUtil.copyProperties(e, FtpInterestDetailRecord.class);
            int cashFtp = Optional.ofNullable(ftpAssessmentInfo.getAssessmentPrice()).map(Long::intValue).orElse(0) + Optional.ofNullable(e.getFtpOverdueAdjust()).orElse(0);
            BigDecimal cashInterestBD = BigDecimal.valueOf(Optional.ofNullable(e.getCashOccupy()).orElse(0L)).multiply(BigDecimal.valueOf(cashFtp)).divide(BigDecimal.valueOf(1000000 * 360), 20, RoundingMode.HALF_UP);
            int billFtp = Optional.ofNullable(ftpAssessmentInfo.getTicketPrice()).map(Long::intValue).orElse(0) + Optional.ofNullable(e.getFtpOverdueAdjust()).orElse(0);
            BigDecimal billInterestBD = BigDecimal.valueOf(Optional.ofNullable(e.getBillOccupy()).orElse(0L)).multiply(BigDecimal.valueOf(billFtp)).divide(BigDecimal.valueOf(1000000 * 360), 20, RoundingMode.HALF_UP);
            newRecord.setCashInterest(Util.mithrasLongDecimalTwo(cashInterestBD.longValue()));
            newRecord.setBillInterest(Util.mithrasLongDecimalTwo(billInterestBD.longValue()));
            return newRecord;
        }).collect(Collectors.toList());
//        // 计算差额
//        long cashInterestDiff = 0L;
//        long billInterestDiff = 0L;
//        for (int i = 0; i < oldList.size() - 1; i++) {
//            FtpInterestDetailRecord oldRecord = oldList.get(i);
//            FtpInterestDetailRecord newRecord = newList.get(i);
//            long currentCashDiff = newRecord.getCashInterest() - oldRecord.getCashInterest();
//            cashInterestDiff += currentCashDiff;
//            long currentBillDiff = newRecord.getBillInterest() - oldRecord.getBillInterest();
//            billInterestDiff += currentBillDiff;
//            if (currentCashDiff != 0 || currentBillDiff != 0) {
//                // 打印日志，辅助后期可能需要的排查工作
//                log.info("{}FTP计息变更后存在差额[oldRecord:{}, newRecord:{}]", ftpInterestBaseInfo.getReceiptCode(), JSONUtil.toJsonStr(oldRecord), JSONUtil.toJsonStr(newRecord));
//            }
//        }
        // 处理差额（使用整体钆差而不是每一期钆差是为了解决重复提交FTP计息变更，重复钆差的问题）
        long oldCashInterestSum = oldList.stream().filter(e -> Objects.nonNull(e.getCashInterest())).mapToLong(FtpInterestDetailRecord::getCashInterest).sum();
        long newCashInterestSum = newList.stream().filter(e -> Objects.nonNull(e.getCashInterest())).mapToLong(FtpInterestDetailRecord::getCashInterest).sum();
        long oldBillInterestSum = oldList.stream().filter(e -> Objects.nonNull(e.getBillInterest())).mapToLong(FtpInterestDetailRecord::getBillInterest).sum();
        long newBillInterestSum = newList.stream().filter(e -> Objects.nonNull(e.getBillInterest())).mapToLong(FtpInterestDetailRecord::getBillInterest).sum();
        long cashInterestDiff = newCashInterestSum - oldCashInterestSum;
        long billInterestDiff = newBillInterestSum - oldBillInterestSum;
        if (cashInterestDiff != 0 || billInterestDiff != 0) {
            // 注意，钆差处理需要放在最后，不然本年累计的值会不正确
            String remark = String.format("%s-%s的钆差金额", LocalDateTimeUtil.format(interestStartDate, DatePattern.NORM_DATE_PATTERN), LocalDateTimeUtil.format(interestEndDate, DatePattern.NORM_DATE_PATTERN));
            ftpInterestDetailRecordService.doDiff(ftpInterestBaseInfo.getId(), interestDiffDate, cashInterestDiff, billInterestDiff, remark);
        }
    }

    public void calculateFtpInterestTimeRange(FtpInterestBaseInfo ftpInterestBaseInfo, LocalDate interestStartDate, LocalDate interestEndDate) {
        LocalDate interestDate = interestStartDate;
        while (!interestDate.isAfter(interestEndDate)) {
            ftpInterestDetailRecordService.calculateFtpInterest(ftpInterestBaseInfo, interestDate);
            interestDate = interestDate.plusDays(1);
        }
    }

    public List<FtpInterestBaseInfo> listByContractId(Long contractId) {
        LambdaQueryWrapper<FtpInterestBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(FtpInterestBaseInfo::getContractId, contractId);
        return this.list(query);
    }

    public FtpInterestBaseInfo getOneByReceiptId(Long receiptId) {
        LambdaQueryWrapper<FtpInterestBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(FtpInterestBaseInfo::getReceiptId, receiptId);
        return this.getOne(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void tryInitFtpInterest(Long contractId) {
        List<ContractReceipt> contractReceiptList = contractReceiptService.listByContractId(contractId);
        if (CollectionUtil.isEmpty(contractReceiptList)) {
            return;
        }
        LambdaQueryWrapper<FtpInterestBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(FtpInterestBaseInfo::getContractId, contractId);
        List<FtpInterestBaseInfo> ftpInterestBaseInfoList = this.list(query);
        Set<Long> existReceiptIds = ftpInterestBaseInfoList.stream().map(FtpInterestBaseInfo::getReceiptId).collect(Collectors.toSet());
        // 去掉已有的
        contractReceiptList.removeIf(item -> existReceiptIds.contains(item.getId()));
        if (CollectionUtil.isEmpty(contractReceiptList)) {
            return;
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        // 生成FTP计息任务
        List<FtpInterestBaseInfo> toSaveList = new LinkedList<>();
        for (ContractReceipt contractReceipt : contractReceiptList) {
            toSaveList.add(this.build(contractBaseInfo, contractReceipt));
        }
        this.saveBatch(toSaveList);
    }

    public PageR<FtpInterestPageListRsp> pageList(FtpInterestPageListReq req) {
        Page<FtpInterestBaseInfo> pageQuery = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<FtpInterestBaseInfo> conditionQuery = this.buildQuery(req);
        Page<FtpInterestBaseInfo> pageResult = this.page(pageQuery, conditionQuery);
        if (CollectionUtil.isEmpty(pageResult.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        Set<Long> bizDeptIds = new HashSet<>();
        Set<Long> sponsorUserIds = new HashSet<>();
        for (FtpInterestBaseInfo ftpInterestBaseInfo : pageResult.getRecords()) {
            bizDeptIds.add(ftpInterestBaseInfo.getBizDeptId());
            sponsorUserIds.add(ftpInterestBaseInfo.getSponsorUserId());
        }
        Map<Long, String> bizDeptMap = id2NameService.deptId2Name(bizDeptIds);
        Map<Long, String> sponsorUserMap = id2NameService.sysUserId2Name(sponsorUserIds);
        List<FtpInterestPageListRsp> dataList = new ArrayList<>(pageResult.getRecords().size());
        for (FtpInterestBaseInfo ftpInterestBaseInfo : pageResult.getRecords()) {
            FtpInterestPageListRsp rsp = new FtpInterestPageListRsp();
            rsp.setId(ftpInterestBaseInfo.getId());
            rsp.setContractCode(ftpInterestBaseInfo.getContractCode());
            rsp.setReceiptCode(ftpInterestBaseInfo.getReceiptCode());
            rsp.setClientName(ftpInterestBaseInfo.getClientName());
            rsp.setProjName(ftpInterestBaseInfo.getProjName());
            rsp.setBizDeptName(bizDeptMap.get(ftpInterestBaseInfo.getBizDeptId()));
            rsp.setSponsorUserName(sponsorUserMap.get(ftpInterestBaseInfo.getSponsorUserId()));
            rsp.setLastUpdateDate(LocalDateTimeUtil.format(ftpInterestBaseInfo.getLastUpdateDate(), DatePattern.NORM_DATE_PATTERN));
            rsp.setTotalInterestAmount(ftpInterestBaseInfo.getTotalInterestAmount());
            rsp.setLastCashFtp(ftpInterestBaseInfo.getLastCashFtp());
            dataList.add(rsp);
        }
        return PageR.of(dataList, pageResult.getTotal(), req.getPage(), req.getPageSize());
    }

    public FtpInterestBaseInfoRsp getBaseInfoRsp(FtpInterestIdReq req) {
        FtpInterestBaseInfo ftpInterestBaseInfo = this.getById(req.getFtpInterestId());
        Assert.notNull(ftpInterestBaseInfo, () -> MithrasException.newException("FTP计息数据不存在"));
        FtpInterestBaseInfoRsp rsp = new FtpInterestBaseInfoRsp();
        rsp.setContractCode(ftpInterestBaseInfo.getContractCode());
        rsp.setReceiptCode(ftpInterestBaseInfo.getReceiptCode());
        rsp.setProjName(ftpInterestBaseInfo.getProjName());
        rsp.setClientName(ftpInterestBaseInfo.getClientName());
        rsp.setBizDeptName(id2NameService.deptId2NameSingle(ftpInterestBaseInfo.getBizDeptId()));
        rsp.setSponsorUserName(id2NameService.sysUserId2NameSingle(ftpInterestBaseInfo.getSponsorUserId()));
        return rsp;
    }

    private LambdaQueryWrapper<FtpInterestBaseInfo> buildQuery(FtpInterestPageListReq req) {
        LambdaQueryWrapper<FtpInterestBaseInfo> query = Wrappers.lambdaQuery();
        if (StrUtil.isNotBlank(req.getContractCode())) {
            query.like(FtpInterestBaseInfo::getContractCode, req.getContractCode().trim());
        }
        if (StrUtil.isNotBlank(req.getProjName())) {
            query.like(FtpInterestBaseInfo::getProjName, req.getProjName().trim());
        }
        if (Objects.nonNull(req.getClientId())) {
            query.eq(FtpInterestBaseInfo::getClientId, req.getClientId());
        }
        if (StrUtil.isNotBlank(req.getReceiptCode())) {
            query.like(FtpInterestBaseInfo::getReceiptCode, req.getReceiptCode());
        }
        if (Objects.nonNull(req.getBizDeptId())) {
            query.eq(FtpInterestBaseInfo::getBizDeptId, req.getBizDeptId());
        }
        if (Objects.nonNull(req.getSponsorUserId())) {
            query.eq(FtpInterestBaseInfo::getSponsorUserId, req.getSponsorUserId());
        }
        return query;
    }

    private FtpInterestBaseInfo build(ContractBaseInfo contractBaseInfo, ContractReceipt contractReceipt) {
        FtpInterestBaseInfo ftpInterestBaseInfo = new FtpInterestBaseInfo();
        ftpInterestBaseInfo.setContractId(contractBaseInfo.getId());
        ftpInterestBaseInfo.setContractCode(contractBaseInfo.getContractCode());
        ftpInterestBaseInfo.setReceiptId(contractReceipt.getId());
        ftpInterestBaseInfo.setReceiptCode(contractReceipt.getReceiptCode());
        ftpInterestBaseInfo.setProjReviewId(contractBaseInfo.getProjReviewId());
        ftpInterestBaseInfo.setProjName(contractBaseInfo.getProjName());
        ftpInterestBaseInfo.setClientId(contractBaseInfo.getClientId());
        ftpInterestBaseInfo.setClientName(id2NameService.clientId2NameSingle(contractBaseInfo.getClientId()));
        ftpInterestBaseInfo.setBizDeptId(contractBaseInfo.getBizDeptId());
        ftpInterestBaseInfo.setSponsorUserId(contractBaseInfo.getProjSponsorUserId());
        ftpInterestBaseInfo.setTotalInterestAmount(0L);
        ftpInterestBaseInfo.setFinish(YesOrNoNumberEnum.NO.getCode());
        return ftpInterestBaseInfo;
    }

    @Resource
    private FtpInterestJob ftpInterestJob;

//    public void recalculate(FtpInterestRecalculateReq req) {
//        LocalDate interestDateFrom = LocalDateTimeUtil.parseDate(req.getInterestDate(), DateTimeFormatter.ofPattern(DatePattern.NORM_MONTH_PATTERN)).with(TemporalAdjusters.firstDayOfMonth());
//        LocalDate interestDateTo = interestDateFrom.with(TemporalAdjusters.lastDayOfMonth());
//        ftpInterestJob.calculateFtpInterest(req.getFtpInterestId(), interestDateFrom, interestDateTo);
//    }

    public LocalDate getLatestMonth() {
        // 自动过滤掉【项目利润】中已确认的年月
        FinanceProjectProfit projectProfit = financeProjectProfitService.getOne(Wrappers.<FinanceProjectProfit>lambdaQuery()
                .eq(FinanceProjectProfit::getIsConfirmed, YesOrNoNumberEnum.YES.getCode())
                .orderByDesc(FinanceProjectProfit::getYear)
                .orderByDesc(FinanceProjectProfit::getMonth)
                .last(StringUtil.mysqlLimitOne()));
        if (Objects.isNull(projectProfit)) {
            return null;
        }
        return LocalDate.of(projectProfit.getYear(), projectProfit.getMonth(), 1);
    }
}
