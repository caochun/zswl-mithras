package cn.zswltech.mithras.associationreport.service;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.api.common.PageR;
import javax.annotation.Resource;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.mapper.associationreport.AssociationMajorMattersEventReportLibMapper;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationMajorMattersEventReportLib;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationRelation;
import cn.zswltech.mithras.service.mapper.model.associationreport.BasicAssociationReport;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.service.constant.ResultMsg;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.mapper.associationreport.AssociationRelationLibMapper;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationRelationLib;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
* @description 金融协会报送-关联方信息汇总表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Service
public class AssociationRelationLibService extends ServiceImpl<AssociationRelationLibMapper, AssociationRelationLib> {

    @Resource
    private AssociationRelationLibMapper associationRelationLibMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationRelationLibAddREQ req) {
        AssociationRelationLib info = BeanUtil.copyProperties(req, AssociationRelationLib.class);
        associationRelationLibMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationRelationLibModifyREQ req) {
        AssociationRelationLib originalInfo = associationRelationLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        AssociationRelationLib info = BeanUtil.copyProperties(req, AssociationRelationLib.class);
        associationRelationLibMapper.updateById(info);
    }

    public Page<AssociationRelationLib> list(AssociationRelationLibListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationRelationLibRemoveREQ req) {
        AssociationRelationLib originalInfo = associationRelationLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        associationRelationLibMapper.deleteById(req.getId());
    }

    public List<AssociationDetailRelationRSP> listByReportInstanceIdAndVersion(String reportInstanceId, String version) {
        LambdaQueryWrapper<AssociationRelationLib> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        query.eq(BasicAssociationReport::getVersion, version);
        query.orderByAsc(BasicAssociationReport::getRowNum);
        List<AssociationRelationLib> dbResult = this.list(query);
        List<AssociationDetailRelationRSP> result = BeanUtil.copyToList(dbResult, AssociationDetailRelationRSP.class);
        if (CollectionUtil.isEmpty(result)) {
            return Collections.emptyList();
        }
        for (AssociationDetailRelationRSP rsp : result) {
            if (StrUtil.isNotBlank(rsp.getCorpShahRelpFlag())) {
                rsp.setCorpShahRelpFlagDisplay(Optional.ofNullable(YesOrNoNumberEnum.findByCodeStr(rsp.getCorpShahRelpFlag())).map(YesOrNoNumberEnum::getChinese).orElse(null));
            }
        }
        return result;
    }

}