package cn.zswltech.mithras.associationreport.service;
import cn.zswltech.mithras.api.common.PageR;
import javax.annotation.Resource;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.associationreport.mapper.AssociationShahStorInfoLibMapper;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationCompanyProfitStatement;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationShahStorInfoLib;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.service.constant.ResultMsg;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.associationreport.mapper.AssociationCompanyProfitStatementLibMapper;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationCompanyProfitStatementLib;

/**
* @description 公司利润表数据表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Service
public class AssociationCompanyProfitStatementLibService extends ServiceImpl<AssociationCompanyProfitStatementLibMapper, AssociationCompanyProfitStatementLib> {

    @Resource
    private AssociationCompanyProfitStatementLibMapper associationCompanyProfitStatementLibMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationCompanyProfitStatementLibAddREQ req) {
        AssociationCompanyProfitStatementLib info = BeanUtil.copyProperties(req, AssociationCompanyProfitStatementLib.class);
        associationCompanyProfitStatementLibMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationCompanyProfitStatementLibModifyREQ req) {
        AssociationCompanyProfitStatementLib originalInfo = associationCompanyProfitStatementLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        AssociationCompanyProfitStatementLib info = BeanUtil.copyProperties(req, AssociationCompanyProfitStatementLib.class);
        associationCompanyProfitStatementLibMapper.updateById(info);
    }

    public Page<AssociationCompanyProfitStatementLib> list(AssociationCompanyProfitStatementLibListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationCompanyProfitStatementLibRemoveREQ req) {
        AssociationCompanyProfitStatementLib originalInfo = associationCompanyProfitStatementLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        associationCompanyProfitStatementLibMapper.deleteById(req.getId());
    }

    public AssociationDetailProfitRSP getByReportInstanceIdAndVersion(String reportInstanceId,String version) {
        LambdaQueryWrapper<AssociationCompanyProfitStatementLib> query = Wrappers.lambdaQuery();
        query.eq(AssociationCompanyProfitStatementLib::getReportInstanceId, reportInstanceId);
        query.eq(AssociationCompanyProfitStatementLib::getVersion, version);
        query.last("LIMIT 1");
        AssociationCompanyProfitStatementLib dbResult = this.getOne(query);
        return BeanUtil.copyProperties(dbResult, AssociationDetailProfitRSP.class);
    }

}