package cn.zswltech.mithras.associationreport.service;
import cn.zswltech.mithras.api.common.PageR;
import javax.annotation.Resource;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.associationreport.mapper.AssociationCompanyProfitStatementLibMapper;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationCompanyProfitStatementLib;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationEntityEconomyService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.service.constant.ResultMsg;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.associationreport.mapper.AssociationEntityEconomyServiceLibMapper;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationEntityEconomyServiceLib;

/**
* @description 实体经济服务数据(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Service
public class AssociationEntityEconomyServiceLibService extends ServiceImpl<AssociationEntityEconomyServiceLibMapper, AssociationEntityEconomyServiceLib> {

    @Resource
    private AssociationEntityEconomyServiceLibMapper associationEntityEconomyServiceLibMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationEntityEconomyServiceLibAddREQ req) {
        AssociationEntityEconomyServiceLib info = BeanUtil.copyProperties(req, AssociationEntityEconomyServiceLib.class);
        associationEntityEconomyServiceLibMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationEntityEconomyServiceLibModifyREQ req) {
        AssociationEntityEconomyServiceLib originalInfo = associationEntityEconomyServiceLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        AssociationEntityEconomyServiceLib info = BeanUtil.copyProperties(req, AssociationEntityEconomyServiceLib.class);
        associationEntityEconomyServiceLibMapper.updateById(info);
    }

    public Page<AssociationEntityEconomyServiceLib> list(AssociationEntityEconomyServiceLibListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationEntityEconomyServiceLibRemoveREQ req) {
        AssociationEntityEconomyServiceLib originalInfo = associationEntityEconomyServiceLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        associationEntityEconomyServiceLibMapper.deleteById(req.getId());
    }

    public AssociationDetailEntityEconomyServiceRSP getByReportInstanceIdAndVersion(String reportInstanceId,String version) {
        LambdaQueryWrapper<AssociationEntityEconomyServiceLib> query = Wrappers.lambdaQuery();
        query.eq(AssociationEntityEconomyServiceLib::getReportInstanceId, reportInstanceId);
        query.eq(AssociationEntityEconomyServiceLib::getVersion, version);
        query.last("LIMIT 1");
        AssociationEntityEconomyServiceLib dbResult = this.getOne(query);
        return BeanUtil.copyProperties(dbResult, AssociationDetailEntityEconomyServiceRSP.class);
    }


}