package cn.zswltech.mithras.service.controller.associationreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationBalanceSheetPartialApi;
import cn.zswltech.mithras.dto.associationreport.AssociationBalanceSheetPartialAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBalanceSheetPartialModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBalanceSheetPartialListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBalanceSheetPartialListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationBalanceSheetPartialRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationBalanceSheetPartialService;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationBalanceSheetPartial;

/**
* @description 资产负债表
* @author vico
* @date 2025-04-18
*/
@RestController
public class AssociationBalanceSheetPartialController implements AssociationBalanceSheetPartialApi {

    @Resource
    private AssociationBalanceSheetPartialService associationBalanceSheetPartialService;

    @Override
    public R<Void> add(AssociationBalanceSheetPartialAddREQ req) {
        associationBalanceSheetPartialService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationBalanceSheetPartialModifyREQ req){
        associationBalanceSheetPartialService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationBalanceSheetPartialListRSP>> list(AssociationBalanceSheetPartialListREQ req){
        Page<AssociationBalanceSheetPartial> data = associationBalanceSheetPartialService.list(req);
        List<AssociationBalanceSheetPartialListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationBalanceSheetPartialListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationBalanceSheetPartialRemoveREQ req){
        associationBalanceSheetPartialService.remove(req);
        return R.ok();
    }

}