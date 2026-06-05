package cn.zswltech.mithras.service.service.afterlese.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportNonPublicExtraREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportNonPublicExtraRSP;
import cn.zswltech.mithras.afterlease.application.convert.AfterLeaseCheckReportConvert;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.afterlease.domain.enums.AfterLeaseCheckReportAreaTypeEnum;
import cn.zswltech.mithras.afterlease.domain.enums.AfterLeaseCheckReportTypeEnum;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.NewAfterLeaseCheckReportExtraMapper;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckReportExtra;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckReportExtraLib;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckReportTemplate;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckReportExtraService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportTemplateService;
import cn.zswltech.mithras.afterlease.application.lib.AfterLeaseCheckReportExtraLibService;
import cn.zswltech.mithras.afterlease.application.lib.handler.impl.AfterLeaseCheckReportExtraLibHandler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/11/18
 * @description
 */
@Service
public class AfterLeaseCheckReportExtraServiceImpl extends ServiceImpl<NewAfterLeaseCheckReportExtraMapper, NewAfterLeaseCheckReportExtra> implements AfterLeaseCheckReportExtraService {
    @Resource
    private AfterLeaseCheckReportTemplateService afterLeaseCheckReportTemplateService;
    @Resource
    private AfterLeaseCheckReportExtraLibService afterLeaseCheckReportExtraLibService;
    @Resource
    private AfterLeaseCheckReportExtraLibHandler afterLeaseCheckReportExtraLibHandler;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void saveNonPublicExtra(AfterLeaseCheckReportNonPublicExtraREQ req) {
        // 找到补充信息的模板
        List<NewAfterLeaseCheckReportTemplate> templateList = afterLeaseCheckReportTemplateService.listBy(AfterLeaseCheckReportTypeEnum.NON_PUBLIC.name(), AfterLeaseCheckReportAreaTypeEnum.EXTRA_CONTENT.name());
        Map<Long, NewAfterLeaseCheckReportTemplate> templateMap = templateList.stream().collect(Collectors.toMap(NewAfterLeaseCheckReportTemplate::getId, e -> e));
        // 处理请求参数
        Long checkPlanClientId = req.getCheckPlanClientId();
        List<NewAfterLeaseCheckReportExtra> toInsertList = new LinkedList<>();
        List<NewAfterLeaseCheckReportExtra> toUpdateList = new LinkedList<>();
        for (AfterLeaseCheckReportNonPublicExtraREQ.Data data : req.getContentList()) {
            if (Objects.equals(data.getCheckResult(), YesOrNoNumberEnum.YES.getCode())) {
                Assert.notBlank(data.getRemark(), () -> MithrasException.newException("检查结果为是的，备注不能为空"));
            }
            NewAfterLeaseCheckReportExtra reportExtra = AfterLeaseCheckReportConvert.toAfterLeaseCheckReportExtra(checkPlanClientId, data);
            // 填充剩余数据
            NewAfterLeaseCheckReportTemplate template = templateMap.get(data.getTemplateId());
            if (Objects.nonNull(template)) {
                reportExtra.setTemplateTitle(template.getTitle());
                reportExtra.setTemplateCode(template.getCode());
            }
            if (Objects.nonNull(data.getId())) {
                toUpdateList.add(reportExtra);
            } else {
                toInsertList.add(reportExtra);
            }
        }
        if (CollectionUtil.isNotEmpty(toInsertList)) {
            this.saveBatch(toInsertList);
        }
        if (CollectionUtil.isNotEmpty(toUpdateList)) {
            this.updateBatchById(toUpdateList);
        }
    }

    @Override
    public List<AfterLeaseCheckReportNonPublicExtraRSP> listNonPublicExtraRSP(Long checkPlanClientId, String version) {
        List<NewAfterLeaseCheckReportExtra> dbList;
        if (StrUtil.isBlank(version)) {
            dbList = this.listNonPublicExtra(checkPlanClientId);
        } else {
            List<NewAfterLeaseCheckReportExtraLib> checkProjectReportExtraLibList = afterLeaseCheckReportExtraLibService.listByCheckProjectIdAndVersion(checkPlanClientId, version);
            dbList = checkProjectReportExtraLibList.stream().map(afterLeaseCheckReportExtraLibHandler::actualLib2Entity).collect(Collectors.toList());
        }
        if (CollectionUtil.isNotEmpty(dbList)) {
            // 已有数据则返回已有的
            return dbList.stream().map(AfterLeaseCheckReportConvert::toAfterLeaseCheckReportNonPublicExtraRSP).collect(Collectors.toList());
        }
        // 没有数据则查询模板返回默认的
        List<NewAfterLeaseCheckReportTemplate> templateList = afterLeaseCheckReportTemplateService.listBy(AfterLeaseCheckReportTypeEnum.NON_PUBLIC.name(), AfterLeaseCheckReportAreaTypeEnum.EXTRA_CONTENT.name());
        Assert.notEmpty(templateList, () -> MithrasException.newException("没有找到对应的模板信息"));
        List<NewAfterLeaseCheckReportExtra> toInsertList = templateList.stream()
                .map(item -> AfterLeaseCheckReportConvert.toAfterLeaseCheckReportExtra(checkPlanClientId, item))
                .collect(Collectors.toList());
        this.saveBatch(toInsertList);
        return toInsertList.stream().map(AfterLeaseCheckReportConvert::toAfterLeaseCheckReportNonPublicExtraRSP).collect(Collectors.toList());
    }

    @Override
    public void removeByCheckPlanClientId(Long checkPlanClientId) {
        LambdaQueryWrapper<NewAfterLeaseCheckReportExtra> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckReportExtra::getCheckPlanClientId, checkPlanClientId);
        this.remove(query);
    }

    @Override
    public List<NewAfterLeaseCheckReportExtra> listNonPublicExtra(Long checkPlanClientId) {
        LambdaQueryWrapper<NewAfterLeaseCheckReportExtra> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckReportExtra::getCheckPlanClientId, checkPlanClientId);
        query.orderByAsc(NewAfterLeaseCheckReportExtra::getTemplateOrderNum);
        return this.list(query);
    }
}
