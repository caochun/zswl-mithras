package cn.zswltech.mithras.associationreport.service;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.api.common.PageR;
import javax.annotation.Resource;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.associationreport.enums.AssociationDictionaryCategoryEnum;
import cn.zswltech.mithras.associationreport.mapper.AssociationRelationLibMapper;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationRelationLib;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationSeniorExecutiveInfo;
import cn.zswltech.mithras.associationreport.mapper.model.BasicAssociationReport;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.service.constant.ResultMsg;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.associationreport.mapper.AssociationSeniorExecutiveInfoLibMapper;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationSeniorExecutiveInfoLib;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
* @description 高管信息一览表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Service
public class AssociationSeniorExecutiveInfoLibService extends ServiceImpl<AssociationSeniorExecutiveInfoLibMapper, AssociationSeniorExecutiveInfoLib> {

    @Resource
    private AssociationSeniorExecutiveInfoLibMapper associationSeniorExecutiveInfoLibMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationSeniorExecutiveInfoLibAddREQ req) {
        AssociationSeniorExecutiveInfoLib info = BeanUtil.copyProperties(req, AssociationSeniorExecutiveInfoLib.class);
        associationSeniorExecutiveInfoLibMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationSeniorExecutiveInfoLibModifyREQ req) {
        AssociationSeniorExecutiveInfoLib originalInfo = associationSeniorExecutiveInfoLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        AssociationSeniorExecutiveInfoLib info = BeanUtil.copyProperties(req, AssociationSeniorExecutiveInfoLib.class);
        associationSeniorExecutiveInfoLibMapper.updateById(info);
    }

    public Page<AssociationSeniorExecutiveInfoLib> list(AssociationSeniorExecutiveInfoLibListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationSeniorExecutiveInfoLibRemoveREQ req) {
        AssociationSeniorExecutiveInfoLib originalInfo = associationSeniorExecutiveInfoLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        associationSeniorExecutiveInfoLibMapper.deleteById(req.getId());
    }

    public List<AssociationDetailSeniorExecutiveInfoRSP> listByReportInstanceIdAndVersion(String reportInstanceId, String version) {
        LambdaQueryWrapper<AssociationSeniorExecutiveInfoLib> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        query.eq(BasicAssociationReport::getVersion, version);
        query.orderByAsc(BasicAssociationReport::getRowNum);
        List<AssociationSeniorExecutiveInfoLib> dbResult = this.list(query);
        if (CollectionUtil.isEmpty(dbResult)) {
            return Collections.emptyList();
        }
        List<AssociationDetailSeniorExecutiveInfoRSP> result = BeanUtil.copyToList(dbResult, AssociationDetailSeniorExecutiveInfoRSP.class);
        // 枚举转译
        Map<String, Map<String, String>> dictMap = SpringUtil.getBean(AssociationDictionaryService.class).getCode2DisplayMap();
        for (AssociationDetailSeniorExecutiveInfoRSP rsp : result) {
            if (StrUtil.isNotBlank(rsp.getCurrDutyCode())) {
                rsp.setCurrDutyDisplay(dictMap.get(AssociationDictionaryCategoryEnum.PUB00247.name()).get(rsp.getCurrDutyCode()));
            }
            if (StrUtil.isNotBlank(rsp.getHighEduCode())) {
                rsp.setHighEduDisplay(dictMap.get(AssociationDictionaryCategoryEnum.DIMLS803.name()).get(rsp.getHighEduCode()));
            }

        }
        return result;
    }

}