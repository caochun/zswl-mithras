package cn.zswltech.mithras.associationreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.associationreport.storedata.DeleteData;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.associationreport.mapper.AssociationBusinessSituationLibMapper;
import cn.zswltech.mithras.associationreport.mapper.AssociationMainBusinessMapper;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationBusinessSituation;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationBusinessSituationLib;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationMainBusiness;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
* @description 业务情况表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Service
public class AssociationBusinessSituationLibService extends ServiceImpl<AssociationBusinessSituationLibMapper, AssociationBusinessSituationLib> implements DeleteData {

    @Resource
    private AssociationBusinessSituationLibMapper associationBusinessSituationLibMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationBusinessSituationLibAddREQ req) {
        AssociationBusinessSituationLib info = BeanUtil.copyProperties(req, AssociationBusinessSituationLib.class);
        associationBusinessSituationLibMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationBusinessSituationLibModifyREQ req) {
        AssociationBusinessSituationLib originalInfo = associationBusinessSituationLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        AssociationBusinessSituationLib info = BeanUtil.copyProperties(req, AssociationBusinessSituationLib.class);
        associationBusinessSituationLibMapper.updateById(info);
    }

    public Page<AssociationBusinessSituationLib> list(AssociationBusinessSituationLibListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationBusinessSituationLibRemoveREQ req) {
        AssociationBusinessSituationLib originalInfo = associationBusinessSituationLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        associationBusinessSituationLibMapper.deleteById(req.getId());
    }

    @Override
    public void deleteByReportInstanceId(String reportInstanceId) {

    }

    public AssociationDetailBusinessSituationRSP getByReportInstanceIdAndVersion(String reportInstanceId,String version) {
        LambdaQueryWrapper<AssociationBusinessSituationLib> query = Wrappers.lambdaQuery();
        query.eq(AssociationBusinessSituationLib::getReportInstanceId, reportInstanceId);
        query.eq(AssociationBusinessSituationLib::getVersion, version);
        query.last("LIMIT 1");
        AssociationBusinessSituationLib dbResult = this.getOne(query);
        return BeanUtil.copyProperties(dbResult, AssociationDetailBusinessSituationRSP.class);
    }


}