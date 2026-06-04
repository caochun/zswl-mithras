package cn.zswltech.mithras.associationreport.controller;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationBalanceSheetPartialLibApi;
import cn.zswltech.mithras.dto.associationreport.AssociationBalanceSheetPartialLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBalanceSheetPartialLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBalanceSheetPartialLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBalanceSheetPartialLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationBalanceSheetPartialLibRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationBalanceSheetPartialLibService;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationBalanceSheetPartialLib;

import java.util.List;

/**
* @description 资产负债表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@RestController
public class AssociationBalanceSheetPartialLibController implements AssociationBalanceSheetPartialLibApi {

    @Resource
    private AssociationBalanceSheetPartialLibService associationBalanceSheetPartialLibService;

    @Override
    public R<Void> add(AssociationBalanceSheetPartialLibAddREQ req) {
        associationBalanceSheetPartialLibService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationBalanceSheetPartialLibModifyREQ req){
        associationBalanceSheetPartialLibService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationBalanceSheetPartialLibListRSP>> list(AssociationBalanceSheetPartialLibListREQ req){
        Page<AssociationBalanceSheetPartialLib> data = associationBalanceSheetPartialLibService.list(req);
        List<AssociationBalanceSheetPartialLibListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationBalanceSheetPartialLibListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationBalanceSheetPartialLibRemoveREQ req){
        associationBalanceSheetPartialLibService.remove(req);
        return R.ok();
    }

}