package cn.zswltech.mithras.associationreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.mapper.associationreport.AssociationBalanceSheetPartialLibMapper;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationBalanceSheetPartial;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationBalanceSheetPartialLib;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
* @description 资产负债表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Service
public class AssociationBalanceSheetPartialLibService extends ServiceImpl<AssociationBalanceSheetPartialLibMapper, AssociationBalanceSheetPartialLib> {

    @Resource
    private AssociationBalanceSheetPartialLibMapper associationBalanceSheetPartialLibMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationBalanceSheetPartialLibAddREQ req) {
        AssociationBalanceSheetPartialLib info = BeanUtil.copyProperties(req, AssociationBalanceSheetPartialLib.class);
        associationBalanceSheetPartialLibMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationBalanceSheetPartialLibModifyREQ req) {
        AssociationBalanceSheetPartialLib originalInfo = associationBalanceSheetPartialLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        AssociationBalanceSheetPartialLib info = BeanUtil.copyProperties(req, AssociationBalanceSheetPartialLib.class);
        associationBalanceSheetPartialLibMapper.updateById(info);
    }

    public Page<AssociationBalanceSheetPartialLib> list(AssociationBalanceSheetPartialLibListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationBalanceSheetPartialLibRemoveREQ req) {
        AssociationBalanceSheetPartialLib originalInfo = associationBalanceSheetPartialLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        associationBalanceSheetPartialLibMapper.deleteById(req.getId());
    }

    public AssociationDetailBalanceSheetPartialRSP getByReportInstanceIdAndVersion(String reportInstanceId,String version) {
        LambdaQueryWrapper<AssociationBalanceSheetPartialLib> query = Wrappers.lambdaQuery();
        query.eq(AssociationBalanceSheetPartialLib::getReportInstanceId, reportInstanceId);
        query.eq(AssociationBalanceSheetPartialLib::getVersion, version);
        query.last("LIMIT 1");
        AssociationBalanceSheetPartialLib dbResult = this.getOne(query);
        return BeanUtil.copyProperties(dbResult, AssociationDetailBalanceSheetPartialRSP.class);
    }

}