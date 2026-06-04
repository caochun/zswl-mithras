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
import cn.zswltech.mithras.associationreport.mapper.AssociationEntityEconomyServiceLibMapper;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationEntityEconomyServiceLib;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationExternalFinancing;
import cn.zswltech.mithras.associationreport.mapper.model.BasicAssociationReport;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.service.constant.ResultMsg;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.associationreport.mapper.AssociationExternalFinancingLibMapper;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationExternalFinancingLib;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
* @description 金融局报送-对外融资信息清单表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Service
public class AssociationExternalFinancingLibService extends ServiceImpl<AssociationExternalFinancingLibMapper, AssociationExternalFinancingLib> {

    @Resource
    private AssociationExternalFinancingLibMapper associationExternalFinancingLibMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationExternalFinancingLibAddREQ req) {
        AssociationExternalFinancingLib info = BeanUtil.copyProperties(req, AssociationExternalFinancingLib.class);
        associationExternalFinancingLibMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationExternalFinancingLibModifyREQ req) {
        AssociationExternalFinancingLib originalInfo = associationExternalFinancingLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        AssociationExternalFinancingLib info = BeanUtil.copyProperties(req, AssociationExternalFinancingLib.class);
        associationExternalFinancingLibMapper.updateById(info);
    }

    public Page<AssociationExternalFinancingLib> list(AssociationExternalFinancingLibListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationExternalFinancingLibRemoveREQ req) {
        AssociationExternalFinancingLib originalInfo = associationExternalFinancingLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        associationExternalFinancingLibMapper.deleteById(req.getId());
    }

    public List<AssociationDetailExternalFinancingRSP> listByReportInstanceIdAndVersion(String reportInstanceId, String version) {
        LambdaQueryWrapper<AssociationExternalFinancingLib> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        query.eq(BasicAssociationReport::getVersion, version);
        query.orderByAsc(BasicAssociationReport::getRowNum);
        List<AssociationExternalFinancingLib> dbResult = this.list(query);
        if (CollectionUtil.isEmpty(dbResult)) {
            return Collections.emptyList();
        }
        List<AssociationDetailExternalFinancingRSP> result = BeanUtil.copyToList(dbResult, AssociationDetailExternalFinancingRSP.class);
        // 枚举转译
        Map<String, Map<String, String>> dictMap = SpringUtil.getBean(AssociationDictionaryService.class).getCode2DisplayMap();
        for (AssociationDetailExternalFinancingRSP rsp : result) {
            if (StrUtil.isNotBlank(rsp.getFinBusiTypeCode())) {
                rsp.setFinBusiTypeCodeDisplay(dictMap.get(AssociationDictionaryCategoryEnum.EVT00052.name()).get(rsp.getFinBusiTypeCode()));
            }
        }
        return result;
    }

}