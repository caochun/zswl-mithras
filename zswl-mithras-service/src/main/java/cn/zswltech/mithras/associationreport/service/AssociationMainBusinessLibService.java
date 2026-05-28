package cn.zswltech.mithras.associationreport.service;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.api.common.PageR;
import javax.annotation.Resource;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.associationreport.AssociationDictionaryCategoryEnum;
import cn.zswltech.mithras.service.mapper.associationreport.AssociationLawInvolvedVisitRelatedInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationLawInvolvedVisitRelatedInfoLib;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationMainBusiness;
import cn.zswltech.mithras.service.mapper.model.associationreport.BasicAssociationReport;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.service.constant.ResultMsg;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.mapper.associationreport.AssociationMainBusinessLibMapper;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationMainBusinessLib;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
* @description 金融协会报送-主要业务清单表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Service
public class AssociationMainBusinessLibService extends ServiceImpl<AssociationMainBusinessLibMapper, AssociationMainBusinessLib> {

    @Resource
    private AssociationMainBusinessLibMapper associationMainBusinessLibMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationMainBusinessLibAddREQ req) {
        AssociationMainBusinessLib info = BeanUtil.copyProperties(req, AssociationMainBusinessLib.class);
        associationMainBusinessLibMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationMainBusinessLibModifyREQ req) {
        AssociationMainBusinessLib originalInfo = associationMainBusinessLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        AssociationMainBusinessLib info = BeanUtil.copyProperties(req, AssociationMainBusinessLib.class);
        associationMainBusinessLibMapper.updateById(info);
    }

    public Page<AssociationMainBusinessLib> list(AssociationMainBusinessLibListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationMainBusinessLibRemoveREQ req) {
        AssociationMainBusinessLib originalInfo = associationMainBusinessLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        associationMainBusinessLibMapper.deleteById(req.getId());
    }

    public List<AssociationDetailMainBusinessRSP> getByReportInstanceIdAndVersion(String reportInstanceId,String version) {
        LambdaQueryWrapper<AssociationMainBusinessLib> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        query.eq(BasicAssociationReport::getVersion, version);
        query.orderByAsc(BasicAssociationReport::getRowNum);
        List<AssociationMainBusinessLib> dbResult = this.list(query);
        if (CollectionUtil.isEmpty(dbResult)) {
            return Collections.emptyList();
        }
        List<AssociationDetailMainBusinessRSP> result = BeanUtil.copyToList(dbResult, AssociationDetailMainBusinessRSP.class);
        // 枚举转译
        Map<String, Map<String, String>> dictMap = SpringUtil.getBean(AssociationDictionaryService.class).getCode2DisplayMap();
        for (AssociationDetailMainBusinessRSP rsp : result) {
            if (StrUtil.isNotBlank(rsp.getAgmtTypeCode())) {
                rsp.setAgmtTypeDisplay(dictMap.get(AssociationDictionaryCategoryEnum.DIMLS079.name()).get(rsp.getAgmtTypeCode()));
            }
            if (StrUtil.isNotBlank(rsp.getProjIndtClasCode())) {
                rsp.setProjIndtClasDisplay(dictMap.get(AssociationDictionaryCategoryEnum.PUB00234.name()).get(rsp.getProjIndtClasCode()));
            }
            if (StrUtil.isNotBlank(rsp.getCustScalCode())) {
                rsp.setCustScalDisplay(dictMap.get(AssociationDictionaryCategoryEnum.PTY00019.name()).get(rsp.getCustScalCode()));
            }
            if (StrUtil.isNotBlank(rsp.getUdpnSituCode())) {
                rsp.setUdpnSituDisplay(dictMap.get(AssociationDictionaryCategoryEnum.PTY00212.name()).get(rsp.getUdpnSituCode()));
            }
            if (StrUtil.isNotBlank(rsp.getOvduDaysCode())) {
                rsp.setOvduDaysDisplay(dictMap.get(AssociationDictionaryCategoryEnum.EVT00051.name()).get(rsp.getOvduDaysCode()));
            }
            if (StrUtil.isNotBlank(rsp.getNpFlag())) {
                rsp.setNpFlagDisplay(Optional.ofNullable(YesOrNoNumberEnum.findByCodeStr(rsp.getNpFlag())).map(YesOrNoNumberEnum::getChinese).orElse(null));
            }
        }
        return result;
    }

}