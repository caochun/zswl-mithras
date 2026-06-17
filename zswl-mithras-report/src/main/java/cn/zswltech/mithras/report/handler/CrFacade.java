package cn.zswltech.mithras.report.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.report.ReportNotificationPort;
import cn.zswltech.mithras.dto.report.BatchExportExcelREQ;
import cn.zswltech.mithras.dto.report.ReportListBaseREQ;
import cn.zswltech.mithras.dto.report.account.AccountListREQ;
import cn.zswltech.mithras.dto.report.batch.BatchReportREQ;
import cn.zswltech.mithras.dto.report.client.ClientListREQ;
import cn.zswltech.mithras.dto.report.fiveclass.FiveClassListREQ;
import cn.zswltech.mithras.dto.report.guarantor.GuarantorListREQ;
import cn.zswltech.mithras.dto.report.mortgage.MortgageListREQ;
import cn.zswltech.mithras.dto.report.overduerecord.OverdueRecordListREQ;
import cn.zswltech.mithras.dto.report.pledge.PledgeListREQ;
import cn.zswltech.mithras.dto.report.repay.RepayListREQ;
import cn.zswltech.mithras.dto.report.specialtrade.SpecialTradeListREQ;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.report.config.ReportConstants;
import cn.zswltech.mithras.report.enums.common.*;
import cn.zswltech.mithras.report.flow.ICrProcessWorker;
import cn.zswltech.mithras.report.flow.ProcHelper;
import cn.zswltech.mithras.report.handler.impl.CrOverdueHandler;
import cn.zswltech.mithras.report.handler.impl.current.CrOverdueNewHandler;
import cn.zswltech.mithras.report.mapper.BatchRecordMapper;
import cn.zswltech.mithras.report.mapper.CrModifyDataSnapMapper;
import cn.zswltech.mithras.report.mapper.base.model.CrAccountBase;
import cn.zswltech.mithras.report.mapper.draft.CrAccountDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import cn.zswltech.mithras.report.mapper.model.BatchRecord;
import cn.zswltech.mithras.report.mapper.model.CrModifyDataSnap;
import cn.zswltech.mithras.report.service.BatchRecordService;
import cn.zswltech.mithras.report.service.CrModifyDataSnapService;
import cn.zswltech.mithras.report.service.draft.CrAccountDraftService;
import cn.zswltech.mithras.report.util.ReportBizUtil;
import cn.zswltech.mithras.foundation.cache.RedisDistLock;
import cn.zswltech.mithras.foundation.cache.RedisHelper;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.gendoc.BusinessDataRepository;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.workflow.process.BizProcessDataService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.CONCURRENT_OPERATION;

/**
 * 征信报送处理器
 *
 * @author wangchuanhao
 * @date 2022/10/9 3:07 PM
 */
@Component
@Slf4j
public class CrFacade {
    public static final Map<String, Long> AMOUNT_MAP = new HashMap<>(8);

    @Resource
    private List<CrAbstractHandler> crHandlerList;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private List<ICrProcessWorker> processWorkerList;
    @Resource
    private ProcHelper procHelper;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private RedisHelper redisHelper;
    @Resource
    private BatchRecordService batchRecordService;
    @Resource
    private ReportNotificationPort reportNotificationPort;
    @Resource
    private HistoryService historyService;
    @Resource
    private CrModifyDataSnapMapper modifyDataSnapMapper;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private HttpServletResponse response;
    @Resource
    private BusinessDataRepository businessDataRepository;

    @Transactional(rollbackFor = Exception.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void handle(LocalDateTime dealTime) {
        //如过存在流程，则不做任何操作
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(ProcessModelTypeEnum.CreditReportFlow.name())
                .active()
                .singleResult();
        if (ObjectUtil.isNotNull(processInstance)) {
            //单独找到逾期的继续更新
            try {
                getBean(CrOverdueHandler.class).handle(dealTime);
                getBean(CrOverdueNewHandler.class).handle(dealTime);
                getBean(CrOverdueHandler.class).afterHandle(dealTime);
                getBean(CrOverdueNewHandler.class).afterHandle(dealTime);
            } catch (Exception e) {
                log.error("时间：{}, 单独生成逾期数据失败", dealTime.format(DateTimeFormatter.ofPattern(DatePattern.CHINESE_DATE_PATTERN)));
            }
            log.error("{}征信报送存在进行中的流程，数据更新操作暂停", dealTime.format(DateTimeFormatter.ofPattern(DatePattern.CHINESE_DATE_PATTERN)));
            return;
        }
        String lockKey = "mithras:report_execute_lock";
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            log.info("征信报送处理开始, time:{}", LocalDateTimeUtil.format(dealTime, "yyyy-MM-dd HH:mm:ss"));
            ProcessResp processResp = procHelper.findRelatedProcess();
            Map<String, Integer> inProcessKeySetMap = new HashMap<>();
            if (Objects.nonNull(processResp)) {
                for (ICrProcessWorker worker : processWorkerList) {
                    inProcessKeySetMap.put(worker.reportPageEnum().name(), worker.countInProcessData());
                }
            }

            // 为了防止长时间不报送的数据不更新，每天都是全量更新一次，更新完毕之后再把原本的是否报送状态变更回去
            List<CrAccountDraft> accountDrafts = getBean(CrAccountDraftService.class).list(Wrappers.<CrAccountDraft>lambdaQuery()
                    .eq(CrAccountDraft::getReportFlag, YesOrNoNumberEnum.NO.getCode()));
            if (!CollectionUtils.isEmpty(accountDrafts)) {
                log.info("存在暂时不上报的数据，先同步数据，再改成暂时不报送，数据：{}", accountDrafts.stream().map(CrAccountBase::getPaymentApplyCode).collect(Collectors.toList()));
                getBean(CrAccountDraftService.class).lambdaUpdate()
                        .set(CrAccountDraft::getReportFlag, YesOrNoNumberEnum.YES.getCode())
                        .in(CrAccountDraft::getId, accountDrafts.stream().map(CrAccountDraft::getId).collect(Collectors.toList()))
                        .update();
            }
            crHandlerList.sort(Comparator.comparing(CrAbstractHandler::sort));
            for (CrAbstractHandler crHandler : crHandlerList) {
                crHandler.handle(dealTime);
            }
            for (CrAbstractHandler crHandler : crHandlerList) {
                crHandler.afterHandle(dealTime);
            }

            if (!CollectionUtils.isEmpty(accountDrafts)) {
                // 数据更新完成，将原本的报送状态恢复
                getBean(CrAccountDraftService.class).lambdaUpdate()
                        .set(CrAccountDraft::getReportFlag, YesOrNoNumberEnum.NO.getCode())
                        .in(CrAccountDraft::getId, accountDrafts.stream().map(CrAccountDraft::getId).collect(Collectors.toList()))
                        .update();
            }
            if (Objects.nonNull(processResp)) {
                for (ICrProcessWorker worker : processWorkerList) {
                    int inProcessDataCount = worker.countInProcessData();
                    if (!Objects.equals(inProcessDataCount, inProcessKeySetMap.get(worker.reportPageEnum().name()))) {
                        // 同步前后在流程里的数据条目不一样了，发生了变化 进行消息通知lss

                        sendDataChangeMessage(processResp);
                        break;
                    }
                }
            }
            log.info("征信报送处理结束");
        } finally {
            AMOUNT_MAP.clear();
            redisDistLock.unlock(lockKey);
        }
    }

    /**
     * 流程结束进行抄表操作
     *
     * @param businessKey
     * @param endType
     * @param startUserId
     * @param processInstanceId
     */
    @Transactional(rollbackFor = Exception.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void processEnd(Long businessKey, Integer endType, Long startUserId, String processInstanceId, LocalDateTime reportTime) {
        BatchRecord procSnapRecord = batchRecordService.getOne(Wrappers.<BatchRecord>lambdaQuery()
                .eq(BatchRecord::getType, BatchType.INCRE.name())
                .orderByDesc(BatchRecord::getBatchNo)
                .last(StringUtil.mysqlLimitOne())
        );

        BatchRecord fullSnapRecord = batchRecordService.getOne(Wrappers.<BatchRecord>lambdaQuery()
                .eq(BatchRecord::getType, BatchType.FULL.name())
                .orderByDesc(BatchRecord::getBatchNo)
                .last(StringUtil.mysqlLimitOne())
        );
        //如果流程没通过，需要将最新的生效数据拷贝一份
        boolean pass = ProcessBusinessStatusEnum.success(endType);
        if (!pass) {
            List<CrModifyDataSnap> modifyDataSnaps = modifyDataSnapMapper.selectList(Wrappers.<CrModifyDataSnap>lambdaQuery()
                    .eq(CrModifyDataSnap::getProcBusinessKey, businessKey)
                    .eq(CrModifyDataSnap::getVersionType, VersionTypeConstants.NORMAL));

            log.info("征信报送流程关闭，流程中数据:{}", modifyDataSnaps);

            if (CollUtil.isNotEmpty(modifyDataSnaps)) {
                List<CrModifyDataSnap> list = new ArrayList<>(modifyDataSnaps.size());
                modifyDataSnaps.forEach(snap -> {
                    CrModifyDataSnap modifyDataSnap = BeanUtil.copyProperties(snap, CrModifyDataSnap.class, ReportConstants.IGNORE_ID, "procBusinessKey", "batchNo");
                    modifyDataSnap.setVersion(ReportBizUtil.getVersion(modifyDataSnap.getVersion()));
                    modifyDataSnap.setApprovalStatus(ApprovalStatus.UN_SUBMIT.name());
                    modifyDataSnap.setIsShow(YesOrNoNumberEnum.YES.getCode());
                    snap.setIsShow(YesOrNoNumberEnum.NO.getCode());
                    list.add(modifyDataSnap);
                });
                log.info("征信报送流程关闭，拷贝的数据：{}", list);
                SpringContextHolder.getBean(CrModifyDataSnapService.class).saveBatch(list);
                SpringContextHolder.getBean(CrModifyDataSnapService.class).updateBatchById(modifyDataSnaps);
            }
        }

        for (ICrProcessWorker worker : processWorkerList) {
            worker.processEnd(businessKey, endType, startUserId, processInstanceId, reportTime, procSnapRecord, fullSnapRecord);
        }
        if (pass) {
            SpringContextHolder.getBean(CrModifyDataSnapService.class)
                    .lambdaUpdate()
                    .eq(CrModifyDataSnap::getIsShow, YesOrNoNumberEnum.YES.getCode())
                    .set(CrModifyDataSnap::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name())
                    .set(CrModifyDataSnap::getIsShow, YesOrNoNumberEnum.NO.getCode())
                    .update();
        }

        // 流程审批通过之后，需要将数据的报送开关设置成一样
        getBean(CrAccountDraftMapper.class).updateReportFlag(String.valueOf(businessKey));
        log.info("征信库数据已处理完成, processInstanceId:{}", processInstanceId);
    }

    /**
     * 提交审批
     */
    @Transactional(rollbackFor = Exception.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void submit(BatchReportREQ req) {
        String lockKey = "mithras:report_execute_lock";
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            ProcessResp processResp = procHelper.findRelatedProcess();
            if (Objects.nonNull(processResp)) {
                throw new MithrasException("存在审批中的数据，请先结束审批流，再提交新的审批数据！");
            }
            Long procBusinessKey = IdUtil.getSnowflakeNextId();
            int seq = redisHelper.incrBy(ReportCacheEnum.CREDIT_REPORT_SUBMIT_SEQ.buildKey(LocalDateTimeUtil.format(LocalDate.now(), "yyyy-MM-dd")), 1L, ReportCacheEnum.CREDIT_REPORT_SUBMIT_SEQ.getExpire());
            for (ICrProcessWorker worker : processWorkerList) {
                worker.submit(procBusinessKey, req.getBatchNo());
            }
            // 此处可能存在事务问题 但如果流程提交有问题抛异常 流程本身事务会回滚，report业务事务也会回滚 暂不处理
            StartProcessReq startProcessReq = new StartProcessReq();
            startProcessReq.setModelKey(ProcessModelTypeEnum.CreditReportFlow.name());
            startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                    .map(AccountVO::getId)
                    .map(String::valueOf)
                    .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
            startProcessReq.setBusinessKey(String.valueOf(procBusinessKey));
            startProcessReq.setSubModule("DEFAULT");
            // 名字格式：2023010301征信报送
            startProcessReq.setProcessInstanceName(String.format("%s%02d征信报送", LocalDateTimeUtil.format(LocalDate.now(), "yyyyMMdd"), seq));
            startProcessReq.setStartUserDeptId(Optional.ofNullable(sysUserService.getUserDept()).map(OrgDO::getId).map(Objects::toString).orElse(""));
            String processInstanceId = processApiService.start(startProcessReq);
            bizProcessDataService.recordBizData(processInstanceId, null);

            //批次在提交的时候生成
            batchRecordService.addRecord(BatchType.INCRE, processInstanceId, req);
            batchRecordService.addRecord(BatchType.FULL, processInstanceId, req);
        } finally {
            redisDistLock.unlock(lockKey);
        }
    }

    private void sendDataChangeMessage(ProcessResp processResp) {
        // 寻找流程已达节点审批人
        List<Long> receiverList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processResp.getProcessInstanceId()).list()
                .stream().map(HistoricTaskInstance::getAssignee).filter(Objects::nonNull).map(Long::valueOf).collect(Collectors.toList());
        receiverList.add(Long.valueOf(processResp.getStartUserId()));
        receiverList = receiverList.stream().distinct().collect(Collectors.toList());
        reportNotificationPort.sendCreditReportDataChange(receiverList, processResp.getProcessInstanceId());
    }

    public String getBatchNumber() {
        BatchRecord todayLastRecord = batchRecordService.getOne(Wrappers.<BatchRecord>lambdaQuery()
                .ge(BatchRecord::getReportTime, LocalDateTimeUtil.beginOfDay(LocalDateTime.now()))
                .le(BatchRecord::getReportTime, LocalDateTimeUtil.endOfDay(LocalDateTime.now()))
                .orderByDesc(BatchRecord::getBatchSeq)
                .last(StringUtil.mysqlLimitOne()));

        int batchSeq = Optional.ofNullable(todayLastRecord).map(BatchRecord::getBatchSeq).orElse(0) + 1;
        return ReportBizUtil.genBatchNo(batchSeq);
    }

    public void exportExcel(BatchExportExcelREQ req) {
        QueryChannel channel = QueryChannel.of(req.getChannel());
        if (Objects.isNull(channel)) {
            throw new MithrasException("查询渠道不合法" + req.getChannel());
        }

        // 如果是批次维度，需要校验批次id
        BatchRecord batchRecord = null;
        ReportListBaseREQ baseReq = new ReportListBaseREQ();
        baseReq.setChannel(channel.name());
        if (channel == QueryChannel.PROC_BATCH) {
            Util.errMissingParam(Objects.isNull(req.getBatchId()), "批次id不能为空");
            batchRecord = SpringContextHolder.getBean(BatchRecordMapper.class).selectById(req.getBatchId());
            Util.errMissingParam(Objects.isNull(batchRecord), "批次不存在");
            baseReq.setBatchId(batchRecord.getId());
        }
//        } else if (channel == QueryChannel.EFFECT) {
//            Util.errMissingParam(Objects.isNull(req.getAccountId()), "账号表id不能为空");
//            baseReq.setAccountId(req.getAccountId());
//        }

        ServletOutputStream os = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            // 首先将文件流构建出来，然后根据不同的查询渠道进行不同的数据填充处理
            ExcelWriter excelWriter = ExcelUtil.getWriter(true);
            // 删除默认的 Sheet1
            excelWriter.getWorkbook().removeSheetAt(0);
            String type = channel.equals(QueryChannel.EDIT) ? "待报送" : channel.equals(QueryChannel.PROC) ? "流程"
                    : channel.equals(QueryChannel.PROC_BATCH) ? "批次" + batchRecord.getBatchNo() : "已报送";
            String fileName = URLEncoder.encode("征信报送-" + type + "-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx", "UTF-8");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName);
            response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
            // 将这个列表排序，根据reportPageEnum 里面的sort排序
            List<ICrProcessWorker> list = processWorkerList;
            list = list.stream()
                    .sorted(Comparator.comparingInt(o -> o.reportPageEnum().getSort()))
                    .collect(Collectors.toList());
            // 在内存中构建流
            for (ICrProcessWorker worker : list) {
                // 找到列表数据，然后写入 sheet 页 分批次查询 500 条一批
                int currentPage = 0;
                baseReq.setPage(currentPage);
                baseReq.setPageSize(500);
                PageR<Map<String, DiffValue>> page;
                List<List<String>> data = new ArrayList<>();
                TableExportFieldsEnum tableExportFieldsEnum = TableExportFieldsEnum.getByName(worker.reportPageEnum().name());
                if (Objects.isNull(tableExportFieldsEnum)) {
                    throw new MithrasException(String.format("模块%s不支持", worker.reportPageEnum().display()));
                }
                // 构建专属的sheet
                excelWriter.setSheet(worker.reportPageEnum().display());
                excelWriter.writeHeadRow(tableExportFieldsEnum.getFields().stream().map(Pair::getValue).collect(Collectors.toList()));
                do {
                    currentPage++;
                    baseReq.setPage(currentPage);
                    page = worker.list(buildReq(baseReq, worker.reportPageEnum()));
                    // 获取数据，遍历九张表的导出字段配置
                    if (CollUtil.isNotEmpty(page.getList())) {
                        for (Map<String, DiffValue> map : page.getList()) {
                            List<String> row = new ArrayList<>();
                            for (Pair<String, String> field : tableExportFieldsEnum.getFields()) {
                                DiffValue value = map.get(field.getKey());
                                // 单独处理身份标识类型
                                if ((worker.reportPageEnum().equals(ReportPageEnum.PLEDGE)
                                        || worker.reportPageEnum().equals(ReportPageEnum.GUARANTOR))
                                        && CharSequenceUtil.equalsAny(field.getKey(), "guarantorIdType", "pledgeIdType")) {
                                    if (worker.reportPageEnum().equals(ReportPageEnum.PLEDGE) && Objects.equals(map.get("pledgeType").getValue(), "2")) {
                                        row.add("统一社会信用代码");
                                    } else if (worker.reportPageEnum().equals(ReportPageEnum.GUARANTOR) && Objects.equals(map.get("clientType").getValue(), "2")) {
                                        row.add("统一社会信用代码");
                                    } else {
                                        row.add(businessDataRepository.getCertTypeNameFromLocalCache(String.valueOf(value.getValue())));
                                    }
                                    continue;
                                }
                                row.add(worker.converter(field.getKey(), value));
                            }
                            data.add(row);
                        }
                    }
                }
                while (page.getTotal() > (long) currentPage * baseReq.getPageSize());

                // 将单个sheet的数据写入到excel中
                if (CollUtil.isNotEmpty(data)) {
                    excelWriter.write(data);
                }
                // 设置自动列宽（需要单独调用）
                excelWriter.autoSizeColumnAll();
                for (int i = 0; i < tableExportFieldsEnum.getFields().size(); i++) {
                    // 针对中文优化列宽计算
                    int chineseWidth = excelWriter.getSheet().getColumnWidth(i) * 3 / 2;
                    excelWriter.setColumnWidth(i, Math.min(chineseWidth, 30));
                }
            }

            // 数据写入完毕，开始将内存中的数据写入响应
            os = response.getOutputStream();
            excelWriter.flush(os, true);
            os.write(bos.toByteArray());
            os.flush();
        } catch (Exception e) {
            log.error("下载失败", e);
            throw new MithrasException("下载失败");
        } finally {
            try {
                bos.close();
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                log.error("关闭输出流异常", e);
            }
        }
    }

    private ReportListBaseREQ buildReq(ReportListBaseREQ baseReq, ReportPageEnum reportPageEnum) {
        switch (reportPageEnum) {
            case CLIENT:
                return BeanUtil.copyProperties(baseReq, ClientListREQ.class);
            case MORTGAGE:
                return BeanUtil.copyProperties(baseReq, MortgageListREQ.class);
            case PLEDGE:
                return BeanUtil.copyProperties(baseReq, PledgeListREQ.class);
            case GUARANTOR:
                return BeanUtil.copyProperties(baseReq, GuarantorListREQ.class);
            case ACCOUNT:
                return BeanUtil.copyProperties(baseReq, AccountListREQ.class);
            case REPAY:
                return BeanUtil.copyProperties(baseReq, RepayListREQ.class);
            case SPECIAL_TRADE:
                return BeanUtil.copyProperties(baseReq, SpecialTradeListREQ.class);
            case OVERDUE_RECORD:
                return BeanUtil.copyProperties(baseReq, OverdueRecordListREQ.class);
            case FIVE_CLASS:
                return BeanUtil.copyProperties(baseReq, FiveClassListREQ.class);
            default:
                throw new MithrasException("不支持的类型");
        }
    }
}
