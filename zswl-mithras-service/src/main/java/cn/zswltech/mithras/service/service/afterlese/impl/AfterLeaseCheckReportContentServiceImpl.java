package cn.zswltech.mithras.service.service.afterlese.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportCSREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportCSRSP;
import cn.zswltech.mithras.service.convert.afterlease.AfterLeaseCheckReportConvert;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.afterlease.domain.enums.AfterLeaseCheckReportAreaTypeEnum;
import cn.zswltech.mithras.afterlease.domain.enums.AfterLeaseCheckReportTypeEnum;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.NewAfterLeaseCheckReportContentMapper;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckPlanClientService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckReportContentService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportMetaService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportTemplateService;
import cn.zswltech.mithras.afterlease.application.lib.AfterLeaseCheckReportContentLibService;
import cn.zswltech.mithras.afterlease.application.lib.handler.impl.AfterLeaseCheckReportContentLibHandler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/11/17
 * @description
 */
@Slf4j
@Service
public class AfterLeaseCheckReportContentServiceImpl extends ServiceImpl<NewAfterLeaseCheckReportContentMapper, NewAfterLeaseCheckReportContent> implements AfterLeaseCheckReportContentService {
    @Resource
    private AfterLeaseCheckPlanClientService afterLeaseCheckPlanClientService;
    @Resource
    private AfterLeaseCheckReportTemplateService afterLeaseCheckReportTemplateService;
    @Resource
    private AfterLeaseCheckReportMetaService afterLeaseCheckReportMetaService;
    @Resource
    private AfterLeaseCheckReportContentLibService afterLeaseCheckReportContentLibService;
    @Resource
    private AfterLeaseCheckReportContentLibHandler afterLeaseCheckReportContentLibHandler;

    @Override
    public void removeByCheckPlanClientId(Long checkPlanClientId) {
        LambdaQueryWrapper<NewAfterLeaseCheckReportContent> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckReportContent::getCheckPlanClientId, checkPlanClientId);
        this.remove(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void saveReportContent(AfterLeaseCheckReportCSREQ req) {
//        NewAfterLeaseCheckPlanClient checkPlanClient = afterLeaseCheckPlanClientService.getById(req.getCheckPlanClientId());
//        Assert.notNull(checkPlanClient, () -> MithrasException.newException("检查计划客户数据不存在"));
//        NewAfterLeaseCheckReportMeta checkReportMeta = afterLeaseCheckReportMetaService.getByCheckPlanClientId(req.getCheckPlanClientId());
//        Assert.notNull(checkPlanClient, () -> MithrasException.newException("检查报告元数据不存在"));
////        if (Objects.equals(checkReportMeta.getReportType(), AfterLeaseCheckReportTypeEnum.PUBLIC.name())) {
////            for (AfterLeaseCheckReportCSREQ.Content reqContent : req.getContentList()) {
////                Assert.notBlank(reqContent.getContent(), () -> MithrasException.newException("检查内容选项必填"));
////            }
////        }
//        // 查询一下已有数据
//        List<NewAfterLeaseCheckReportContent> checkProjectReportContentList = this.listByCheckPlanClientId(checkPlanClient.getId());
//        // 按照内容id分组
//        Map<Long, NewAfterLeaseCheckReportContent> checkReportContentMap;
//        if (CollectionUtil.isEmpty(checkProjectReportContentList)) {
//            checkReportContentMap = new HashMap<>();
//        } else {
//            checkReportContentMap = checkProjectReportContentList.stream().collect(Collectors.toMap(NewAfterLeaseCheckReportContent::getId, e -> e));
//        }
//        // 查询模板数据
//        List<NewAfterLeaseCheckReportTemplate> reportContentTemplateList = afterLeaseCheckReportTemplateService.listBy(checkReportMeta.getReportType(), AfterLeaseCheckReportAreaTypeEnum.CONTENT.name());
//        // 按照模板id分组
//        Map<Long, NewAfterLeaseCheckReportTemplate> reportContentTemplateMap;
//        if (CollectionUtil.isEmpty(reportContentTemplateList)) {
//            reportContentTemplateMap = new HashMap<>();
//        } else {
//            reportContentTemplateMap = reportContentTemplateList.stream().collect(Collectors.toMap(NewAfterLeaseCheckReportTemplate::getId, e -> e));
//        }
//        // 请求数据按照是否有主键id分组
//        List<NewAfterLeaseCheckReportContent> toUpdateList = new LinkedList<>();
//        List<NewAfterLeaseCheckReportContent> toInsertList = new LinkedList<>();
//        for (AfterLeaseCheckReportCSREQ.Content content : req.getContentList()) {
//            if (Objects.nonNull(content.getId())) {
//                // 更新操作不更新历史模板冗余数据（保留当初保存时候的快照）
//                NewAfterLeaseCheckReportContent updateModel = checkReportContentMap.get(content.getId());
//                if (Objects.nonNull(updateModel)) {
//                    updateModel.setContent(content.getContent());
//                    toUpdateList.add(updateModel);
//                }
//            } else {
//                NewAfterLeaseCheckReportContent insertModel = new NewAfterLeaseCheckReportContent();
//                NewAfterLeaseCheckReportTemplate template = reportContentTemplateMap.get(content.getTemplateId());
//                if (Objects.nonNull(template)) {
//                    insertModel.setCheckPlanClientId(req.getCheckPlanClientId());
//                    insertModel.setTemplateId(template.getId());
//                    insertModel.setTemplateGroupName(template.getGroupName());
//                    insertModel.setTemplateTitle(template.getTitle());
//                    insertModel.setTemplateCode(template.getCode());
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
//            this.check(checkReportMeta.getReportType(), toInsertList);
//            this.saveBatch(toInsertList);
//        }
//        if (CollectionUtil.isNotEmpty(toUpdateList)) {
//            this.check(checkReportMeta.getReportType(), toUpdateList);
//            this.updateBatchById(toUpdateList);
//        }
    }

    @Override
    public List<NewAfterLeaseCheckReportContent> listByCheckPlanClientId(Long checkPlanClientId) {
        LambdaQueryWrapper<NewAfterLeaseCheckReportContent> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckReportContent::getCheckPlanClientId, checkPlanClientId);
        query.orderByAsc(NewAfterLeaseCheckReportContent::getTemplateOrderNum);
        return this.list(query);
    }

    @Override
    public List<AfterLeaseCheckReportCSRSP> listReportContent(Long checkPlanClientId, String version) {
//        NewAfterLeaseCheckPlanClient checkPlanProject = afterLeaseCheckPlanClientService.getById(checkPlanClientId);
//        Assert.notNull(checkPlanProject, () -> MithrasException.newException("检查计划客户记录不存在"));
//        List<NewAfterLeaseCheckReportContent> reportContentList;
//        if (StrUtil.isBlank(version)) {
//            reportContentList = this.listByCheckPlanClientId(checkPlanClientId);
//        } else {
//            List<NewAfterLeaseCheckReportContentLib> checkReportContentLibList = afterLeaseCheckReportContentLibService.listByCheckClientIdAndVersion(checkPlanClientId, version);
//            reportContentList = checkReportContentLibList.stream().map(afterLeaseCheckReportContentLibHandler::actualLib2Entity).collect(Collectors.toList());
//        }
//        if (CollectionUtil.isNotEmpty(reportContentList)) {
//            // 已经保存过，直接使用历史数据
//            return AfterLeaseCheckReportConvert.toAfterLeaseCheckReportCSRSPListFromContentData(reportContentList);
//        }
//        // 没保存过，使用模板数据初始化
//        NewAfterLeaseCheckReportMeta checkReportMeta = afterLeaseCheckReportMetaService.getByCheckPlanClientId(checkPlanClientId);
//        Assert.notNull(checkPlanProject, () -> MithrasException.newException("检查报告元数据不存在"));
//        List<NewAfterLeaseCheckReportTemplate> contentTemplateList = afterLeaseCheckReportTemplateService.listBy(checkReportMeta.getReportType(), AfterLeaseCheckReportAreaTypeEnum.CONTENT.name());
//        Assert.notEmpty(contentTemplateList, () -> MithrasException.newException("没有找到对应类型的模板数据"));
//        List<NewAfterLeaseCheckReportContent> toInsertList = contentTemplateList.stream()
//                .map(item -> AfterLeaseCheckReportConvert.toAfterLeaseCheckReportContent(checkPlanClientId, item))
//                .collect(Collectors.toList());
//        this.saveBatch(toInsertList);
//        return AfterLeaseCheckReportConvert.toAfterLeaseCheckReportCSRSPListFromContentData(toInsertList);
        return null;
    }


}
