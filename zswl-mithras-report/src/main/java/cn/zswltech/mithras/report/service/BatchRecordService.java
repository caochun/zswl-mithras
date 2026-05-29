package cn.zswltech.mithras.report.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.FileApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.report.batch.BatchHeadRSP;
import cn.zswltech.mithras.dto.report.batch.BatchListREQ;
import cn.zswltech.mithras.dto.report.batch.BatchListRSP;
import cn.zswltech.mithras.dto.report.batch.BatchReportREQ;
import cn.zswltech.mithras.report.config.ReportConstants;
import cn.zswltech.mithras.report.enums.common.BatchType;
import cn.zswltech.mithras.report.mapper.BatchRecordMapper;
import cn.zswltech.mithras.report.mapper.model.BatchRecord;
import cn.zswltech.mithras.report.util.ReportBizUtil;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.payment.pubinfo.PublicInfoFileTypeEnum;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.common.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 批次
 *
 * @author wangchuanhao
 * @date 2023/1/12 3:53 PM
 */
@Service
public class BatchRecordService extends ServiceImpl<BatchRecordMapper, BatchRecord> {

    @Resource
    private Id2NameService id2NameService;
    @Resource
    private MaterialsListService materialsListService;

    @Transactional(rollbackFor = Exception.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public BatchRecord addRecord(BatchType type, String processInstanceId, BatchReportREQ req) {
        BatchRecord todayLastRecord = baseMapper.selectOne(Wrappers.<BatchRecord>lambdaQuery()
                .eq(BatchRecord::getType, type.name())
                .ge(BatchRecord::getReportTime, LocalDateTimeUtil.beginOfDay(LocalDateTime.now()))
                .le(BatchRecord::getReportTime, LocalDateTimeUtil.endOfDay(LocalDateTime.now()))
                .orderByDesc(BatchRecord::getBatchSeq)
                .last(StringUtil.mysqlLimitOne())
        );
        int batchSeq = Optional.ofNullable(todayLastRecord).map(BatchRecord::getBatchSeq).orElse(0) + 1;
        BatchRecord batchRecord = BatchRecord.builder()
                .batchSeq(batchSeq)
                .batchNo(ReportBizUtil.genBatchNo(batchSeq))
                .reportTime(LocalDateTime.now())
                .remark(req.getReportDescription())
                .reportorId(AccountUtil.getLoginInfo().getId())
                .type(type.name())
                .processInstanceId(processInstanceId)
                .build();
        baseMapper.insert(batchRecord);
        return batchRecord;
    }

    /**
     * 增量批次列表查询
     *
     * @param req
     * @return
     */
    public PageR<BatchListRSP> increList(BatchListREQ req) {
        Page<BatchRecord> dataPage = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<BatchRecord>lambdaQuery()
                .eq(BatchRecord::getType, BatchType.INCRE.name())
                .like(StringUtils.isNotBlank(req.getBatchNo()), BatchRecord::getBatchNo, req.getBatchNo())
                .ge(Objects.nonNull(req.getReportTimeFrom()), BatchRecord::getReportTime, req.getReportTimeFrom())
                .le(Objects.nonNull(req.getReportTimeTo()), BatchRecord::getReportTime, req.getReportTimeTo())
                .orderByDesc(BatchRecord::getReportTime)
        );
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(dataPage.getRecords().stream().map(BatchRecord::getReportorId).collect(Collectors.toSet()));
        List<BatchListRSP> rspList = dataPage.getRecords().stream().map(d -> {
            BatchListRSP rsp = BeanUtil.copyProperties(d, BatchListRSP.class);
            rsp.setReporterName(userNameMap.get(d.getReportorId()));
            return rsp;
        }).collect(Collectors.toList());
        return PageR.of(rspList, dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

    public BatchHeadRSP batchReason(Long processInstanceId) {
        BatchRecord batchRecord = baseMapper.selectOne(Wrappers.<BatchRecord>lambdaQuery()
                .eq(BatchRecord::getProcessInstanceId, processInstanceId)
                .last(StringUtil.mysqlLimitOne()));
        if (Objects.isNull(batchRecord)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        BatchHeadRSP rsp = new BatchHeadRSP();
        rsp.setBatchNo(batchRecord.getBatchNo());
        rsp.setReportDescription(batchRecord.getRemark());
        return rsp;
    }
}
