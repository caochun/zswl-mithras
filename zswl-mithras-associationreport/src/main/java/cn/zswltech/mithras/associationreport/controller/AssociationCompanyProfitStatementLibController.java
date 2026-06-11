package cn.zswltech.mithras.associationreport.controller;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationCompanyProfitStatementLibApi;
import cn.zswltech.mithras.dto.associationreport.AssociationCompanyProfitStatementLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationCompanyProfitStatementLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationCompanyProfitStatementLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationCompanyProfitStatementLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationCompanyProfitStatementLibRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationCompanyProfitStatementLibService;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationCompanyProfitStatementLib;

import java.util.List;

/**
* @description 公司利润表数据表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@RestController
public class AssociationCompanyProfitStatementLibController implements AssociationCompanyProfitStatementLibApi {

    @Resource
    private AssociationCompanyProfitStatementLibService associationCompanyProfitStatementLibService;

    @Override
    public R<Void> add(AssociationCompanyProfitStatementLibAddREQ req) {
        associationCompanyProfitStatementLibService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationCompanyProfitStatementLibModifyREQ req){
        associationCompanyProfitStatementLibService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationCompanyProfitStatementLibListRSP>> list(AssociationCompanyProfitStatementLibListREQ req){
        Page<AssociationCompanyProfitStatementLib> data = associationCompanyProfitStatementLibService.list(req);
        List<AssociationCompanyProfitStatementLibListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationCompanyProfitStatementLibListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationCompanyProfitStatementLibRemoveREQ req){
        associationCompanyProfitStatementLibService.remove(req);
        return R.ok();
    }

}