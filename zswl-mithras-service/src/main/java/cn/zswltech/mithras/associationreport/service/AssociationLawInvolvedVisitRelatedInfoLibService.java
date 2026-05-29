package cn.zswltech.mithras.associationreport.service;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.api.common.PageR;
import javax.annotation.Resource;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.service.enums.associationreport.AssociationDictionaryCategoryEnum;
import cn.zswltech.mithras.service.mapper.associationreport.AssociationExternalFinancingLibMapper;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationExternalFinancingLib;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationLawInvolvedVisitRelatedInfo;
import cn.zswltech.mithras.service.mapper.model.associationreport.BasicAssociationReport;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.common.constant.ResultMsg;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.mapper.associationreport.AssociationLawInvolvedVisitRelatedInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationLawInvolvedVisitRelatedInfoLib;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
* @description 涉法涉讼涉访信息表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Service
public class AssociationLawInvolvedVisitRelatedInfoLibService extends ServiceImpl<AssociationLawInvolvedVisitRelatedInfoLibMapper, AssociationLawInvolvedVisitRelatedInfoLib> {

    @Resource
    private AssociationLawInvolvedVisitRelatedInfoLibMapper associationLawInvolvedVisitRelatedInfoLibMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationLawInvolvedVisitRelatedInfoLibAddREQ req) {
        AssociationLawInvolvedVisitRelatedInfoLib info = BeanUtil.copyProperties(req, AssociationLawInvolvedVisitRelatedInfoLib.class);
        associationLawInvolvedVisitRelatedInfoLibMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationLawInvolvedVisitRelatedInfoLibModifyREQ req) {
        AssociationLawInvolvedVisitRelatedInfoLib originalInfo = associationLawInvolvedVisitRelatedInfoLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        AssociationLawInvolvedVisitRelatedInfoLib info = BeanUtil.copyProperties(req, AssociationLawInvolvedVisitRelatedInfoLib.class);
        associationLawInvolvedVisitRelatedInfoLibMapper.updateById(info);
    }

    public Page<AssociationLawInvolvedVisitRelatedInfoLib> list(AssociationLawInvolvedVisitRelatedInfoLibListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationLawInvolvedVisitRelatedInfoLibRemoveREQ req) {
        AssociationLawInvolvedVisitRelatedInfoLib originalInfo = associationLawInvolvedVisitRelatedInfoLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        associationLawInvolvedVisitRelatedInfoLibMapper.deleteById(req.getId());
    }

    public List<AssociationDetailLawInvolvedVisitRelatedInfoRSP> listByReportInstanceIdAndVersion(String reportInstanceId, String version) {
        LambdaQueryWrapper<AssociationLawInvolvedVisitRelatedInfoLib> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        query.eq(BasicAssociationReport::getVersion, version);
        query.orderByAsc(BasicAssociationReport::getRowNum);
        List<AssociationLawInvolvedVisitRelatedInfoLib> dbResult = this.list(query);
        if (CollectionUtil.isEmpty(dbResult)) {
            return Collections.emptyList();
        }
        List<AssociationDetailLawInvolvedVisitRelatedInfoRSP> result = BeanUtil.copyToList(dbResult, AssociationDetailLawInvolvedVisitRelatedInfoRSP.class);
        // 枚举转译
        Map<String, Map<String, String>> dictMap = SpringUtil.getBean(AssociationDictionaryService.class).getCode2DisplayMap();
        for (AssociationDetailLawInvolvedVisitRelatedInfoRSP rsp : result) {
            if (StrUtil.isNotBlank(rsp.getCaseClasCode())) {
                rsp.setCaseClasDisplay(dictMap.get(AssociationDictionaryCategoryEnum.PUB00250.name()).get(rsp.getCaseClasCode()));
            }
            //是否销号

        }
        return result;
    }

}