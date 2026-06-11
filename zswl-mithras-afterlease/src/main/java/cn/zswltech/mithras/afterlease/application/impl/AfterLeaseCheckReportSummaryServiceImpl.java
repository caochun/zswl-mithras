package cn.zswltech.mithras.afterlease.application.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportCSREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportCSRSP;
import cn.zswltech.mithras.afterlease.application.convert.AfterLeaseCheckReportConvert;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckReportAreaTypeEnum;
import cn.zswltech.mithras.afterlease.mapper.NewAfterLeaseCheckReportSummaryMapper;
import cn.zswltech.mithras.afterlease.mapper.model.*;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckPlanClientService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportMetaService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportSummaryService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportTemplateService;
import cn.zswltech.mithras.afterlease.application.lib.AfterLeaseCheckReportSummaryLibService;
import cn.zswltech.mithras.afterlease.application.lib.handler.impl.AfterLeaseCheckReportSummaryLibHandler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/11/17
 * @description
 */
@Service
public class AfterLeaseCheckReportSummaryServiceImpl extends ServiceImpl<NewAfterLeaseCheckReportSummaryMapper, NewAfterLeaseCheckReportSummary> implements AfterLeaseCheckReportSummaryService {
    @Resource
    private AfterLeaseCheckPlanClientService afterLeaseCheckPlanClientService;
    @Resource
    private AfterLeaseCheckReportTemplateService afterLeaseCheckReportTemplateService;
    @Resource
    private AfterLeaseCheckReportMetaService afterLeaseCheckReportMetaService;
    @Resource
    private AfterLeaseCheckReportSummaryLibService afterLeaseCheckReportSummaryLibService;
    @Resource
    private AfterLeaseCheckReportSummaryLibHandler afterLeaseCheckReportSummaryLibHandler;

    @Override
    public void removeByCheckPlanClientId(Long checkPlanClientId) {
        LambdaQueryWrapper<NewAfterLeaseCheckReportSummary> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckReportSummary::getCheckPlanClientId, checkPlanClientId);
        this.remove(query);
    }

    @Override
    public void saveReportSummary(AfterLeaseCheckReportCSREQ req) {
//        NewAfterLeaseCheckPlanClient checkPlanClient = afterLeaseCheckPlanClientService.getById(req.getCheckPlanClientId());
//        Assert.notNull(checkPlanClient, () -> MithrasException.newException("检查计划客户记录不存在"));
//        NewAfterLeaseCheckReportMeta checkReportMeta = afterLeaseCheckReportMetaService.getByCheckPlanClientId(req.getCheckPlanClientId());
//        Assert.notNull(checkReportMeta, () -> MithrasException.newException("检查报告元数据不存在"));
//        // 查询一下已有数据
//        List<NewAfterLeaseCheckReportSummary> checkReportSummaryList = this.listByCheckPlanClientId(checkPlanClient.getId());
//        // 按照内容id分组
//        Map<Long, NewAfterLeaseCheckReportSummary> checkReportSummaryMap;
//        if (CollectionUtil.isEmpty(checkReportSummaryList)) {
//            checkReportSummaryMap = new HashMap<>();
//        } else {
//            checkReportSummaryMap = checkReportSummaryList.stream().collect(Collectors.toMap(NewAfterLeaseCheckReportSummary::getId, e -> e));
//        }
//        // 查询模板数据
//        List<NewAfterLeaseCheckReportTemplate> reportSummaryTemplateList = afterLeaseCheckReportTemplateService.listBy(checkReportMeta.getReportType(), AfterLeaseCheckReportAreaTypeEnum.SUMMARY.name());
//        // 按照模板id分组
//        Map<Long, NewAfterLeaseCheckReportTemplate> reportSummaryTemplateMap;
//        if (CollectionUtil.isEmpty(reportSummaryTemplateList)) {
//            reportSummaryTemplateMap = new HashMap<>();
//        } else {
//            reportSummaryTemplateMap = reportSummaryTemplateList.stream().collect(Collectors.toMap(NewAfterLeaseCheckReportTemplate::getId, e -> e));
//        }
//        // 请求数据按照是否有主键id分组
//        List<NewAfterLeaseCheckReportSummary> toUpdateList = new LinkedList<>();
//        List<NewAfterLeaseCheckReportSummary> toInsertList = new LinkedList<>();
//        for (AfterLeaseCheckReportCSREQ.Content content : req.getContentList()) {
//            if (Objects.nonNull(content.getId())) {
//                // 更新操作不更新历史模板冗余数据（保留当初保存时候的快照）
//                NewAfterLeaseCheckReportSummary updateModel = checkReportSummaryMap.get(content.getId());
//                if (Objects.nonNull(updateModel)) {
//                    updateModel.setContent(content.getContent());
//                    toUpdateList.add(updateModel);
//                }
//            } else {
//                NewAfterLeaseCheckReportSummary insertModel = new NewAfterLeaseCheckReportSummary();
//                NewAfterLeaseCheckReportTemplate template = reportSummaryTemplateMap.get(content.getTemplateId());
//                if (Objects.nonNull(template)) {
//                    insertModel.setCheckPlanClientId(req.getCheckPlanClientId());
//                    insertModel.setTemplateId(template.getId());
//                    insertModel.setTemplateTitle(template.getTitle());
//                    insertModel.setTemplateCode(template.getCode());
//                    insertModel.setTemplateGroupName(template.getGroupName());
//                    insertModel.setTemplateContentInputLabel(template.getContentInputLabel());
//                    insertModel.setTemplateContentInputType(template.getContentInputType());
//                    insertModel.setTemplateContentInputOption(template.getContentInputOption());
//                    insertModel.setTemplateOrderNum(template.getOrderNum());
//                    insertModel.setContent(content.getContent());
//                    toInsertList.add(insertModel);
//                }
//            }
//        }
//        if (CollectionUtil.isNotEmpty(toInsertList)) {
//            this.saveBatch(toInsertList);
//        }
//        if (CollectionUtil.isNotEmpty(toUpdateList)) {
//            this.updateBatchById(toUpdateList);
//        }
    }

    @Override
    public List<NewAfterLeaseCheckReportSummary> listByCheckPlanClientId(Long checkPlanClientId) {
        LambdaQueryWrapper<NewAfterLeaseCheckReportSummary> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckReportSummary::getCheckPlanClientId, checkPlanClientId);
        return this.list(query);
    }

    @Override
    public List<AfterLeaseCheckReportCSRSP> listReportSummary(Long checkPlanClientId, String version) {
        NewAfterLeaseCheckPlanClient checkPlanClient = afterLeaseCheckPlanClientService.getById(checkPlanClientId);
        Assert.notNull(checkPlanClient, () -> MithrasException.newException("检查计划客户记录不存在"));
        List<NewAfterLeaseCheckReportSummary> reportSummaryList;
        if (StrUtil.isBlank(version)) {
            reportSummaryList = this.listByCheckPlanClientId(checkPlanClientId);
        } else {
            List<NewAfterLeaseCheckReportSummaryLib> checkReportSummaryLibList = afterLeaseCheckReportSummaryLibService.listByCheckClientId(checkPlanClientId, version);
            reportSummaryList = checkReportSummaryLibList.stream().map(afterLeaseCheckReportSummaryLibHandler::actualLib2Entity).collect(Collectors.toList());
        }
        if (CollectionUtil.isNotEmpty(reportSummaryList)) {
            // 已经保存过，直接使用历史数据
            return AfterLeaseCheckReportConvert.toAfterLeaseCheckReportCSRSPListFromSummaryData(reportSummaryList);
        }
        // 没保存过，使用模板数据
        NewAfterLeaseCheckReportMeta checkReportMeta = afterLeaseCheckReportMetaService.getByCheckPlanClientId(checkPlanClientId);
        Assert.notNull(checkReportMeta, () -> MithrasException.newException("检查报告元数据不存在"));
        List<NewAfterLeaseCheckReportTemplate> summaryTemplateList = afterLeaseCheckReportTemplateService.listBy(checkReportMeta.getReportType(), AfterLeaseCheckReportAreaTypeEnum.SUMMARY.name());
        Assert.notEmpty(summaryTemplateList, () -> MithrasException.newException("没有找到对应类型的模板数据"));
        List<NewAfterLeaseCheckReportSummary> toInsertList = summaryTemplateList.stream()
                .map(item -> AfterLeaseCheckReportConvert.toAfterLeaseCheckReportSummary(checkPlanClientId, item))
                .collect(Collectors.toList());
        this.saveBatch(toInsertList);
        return AfterLeaseCheckReportConvert.toAfterLeaseCheckReportCSRSPListFromSummaryData(toInsertList);
    }
}
