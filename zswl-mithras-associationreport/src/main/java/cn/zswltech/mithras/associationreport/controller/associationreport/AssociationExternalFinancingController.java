package cn.zswltech.mithras.associationreport.controller;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationExternalFinancingApi;
import cn.zswltech.mithras.dto.associationreport.AssociationExternalFinancingAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationExternalFinancingModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationExternalFinancingListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationExternalFinancingListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationExternalFinancingRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationExternalFinancingService;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationExternalFinancing;

/**
* @description 金融局报送-对外融资信息清单表
* @author vico
* @date 2025-04-18
*/
@RestController
public class AssociationExternalFinancingController implements AssociationExternalFinancingApi {

    @Resource
    private AssociationExternalFinancingService associationExternalFinancingService;

    @Override
    public R<Void> add(AssociationExternalFinancingAddREQ req) {
        associationExternalFinancingService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationExternalFinancingModifyREQ req){
        associationExternalFinancingService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationExternalFinancingListRSP>> list(AssociationExternalFinancingListREQ req){
        Page<AssociationExternalFinancing> data = associationExternalFinancingService.list(req);
        List<AssociationExternalFinancingListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationExternalFinancingListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationExternalFinancingRemoveREQ req){
        associationExternalFinancingService.remove(req);
        return R.ok();
    }

}