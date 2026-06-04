package cn.zswltech.mithras.associationreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.associationreport.DeleteData;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.associationreport.enums.AssociationDictionaryCategoryEnum;
import cn.zswltech.mithras.associationreport.mapper.AssociationBasicSituationLibMapper;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationBasicSituationLib;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

/**
* @description 基本情况统计((流程节点记录版本表))
* @author hspcadmin
* @date 2025-09-14
*/
@Service
public class AssociationBasicSituationLibService extends ServiceImpl<AssociationBasicSituationLibMapper, AssociationBasicSituationLib> implements DeleteData {

    @Resource
    private AssociationBasicSituationLibMapper associationBasicSituationLibMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationBasicSituationLibAddREQ req) {
        AssociationBasicSituationLib info = BeanUtil.copyProperties(req, AssociationBasicSituationLib.class);
        associationBasicSituationLibMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationBasicSituationLibModifyREQ req) {
        AssociationBasicSituationLib originalInfo = associationBasicSituationLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        AssociationBasicSituationLib info = BeanUtil.copyProperties(req, AssociationBasicSituationLib.class);
        associationBasicSituationLibMapper.updateById(info);
    }

    public Page<AssociationBasicSituationLib> list(AssociationBasicSituationLibListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationBasicSituationLibRemoveREQ req) {
        AssociationBasicSituationLib originalInfo = associationBasicSituationLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        associationBasicSituationLibMapper.deleteById(req.getId());
    }

    @Override
    public void deleteByReportInstanceId(String reportInstanceId) {

    }

    public AssociationDetailBasicSituationRSP getByReportInstanceIdAndVersion(String reportInstanceId,String version) {
        LambdaQueryWrapper<AssociationBasicSituationLib> query = Wrappers.lambdaQuery();
        query.eq(AssociationBasicSituationLib::getReportInstanceId, reportInstanceId);
        query.eq(AssociationBasicSituationLib::getVersion, version);
        query.last("LIMIT 1");
        AssociationBasicSituationLib dbResult = this.baseMapper.selectOne(query);
        AssociationDetailBasicSituationRSP rsp = BeanUtil.copyProperties(dbResult, AssociationDetailBasicSituationRSP.class);
        // 枚举转译
        Map<String, Map<String, String>> dictMap = SpringUtil.getBean(AssociationDictionaryService.class).getCode2DisplayMap();
        if (StrUtil.isNotBlank(rsp.getEconClasCode())) {
            rsp.setEconClasDisplay(dictMap.get(AssociationDictionaryCategoryEnum.PTY00003.name()).get(rsp.getEconClasCode()));//经济成分
        }

        if (StrUtil.isNotBlank(rsp.getCtarCorpHoldFlag())) {
            rsp.setCtarCorpHoldFlagDisplay(Optional.ofNullable(YesOrNoNumberEnum.findByCodeStr(rsp.getCtarCorpHoldFlag())).map(YesOrNoNumberEnum::getChinese).orElse(null)); //是否中央企业控股
        }

        if (StrUtil.isNotBlank(rsp.getLcalSoeHoldFlag())) {
            rsp.setLcalSoeHoldFlagDisplay(Optional.ofNullable(YesOrNoNumberEnum.findByCodeStr(rsp.getLcalSoeHoldFlag())).map(YesOrNoNumberEnum::getChinese).orElse(null)); //是否地方国企控股
        }

        if (StrUtil.isNotBlank(rsp.getCorpClasCode())) {
            rsp.setCorpClasDisplay(dictMap.get(AssociationDictionaryCategoryEnum.PTY00221.name()).get(rsp.getCorpClasCode()));//企业类别
        }
        return rsp;
    }

}
