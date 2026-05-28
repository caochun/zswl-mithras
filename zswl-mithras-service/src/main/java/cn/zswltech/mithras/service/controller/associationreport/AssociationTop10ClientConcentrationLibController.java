package cn.zswltech.mithras.service.controller.associationreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationTop10ClientConcentrationLibApi;
import cn.zswltech.mithras.dto.associationreport.AssociationTop10ClientConcentrationLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationTop10ClientConcentrationLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationTop10ClientConcentrationLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationTop10ClientConcentrationLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationTop10ClientConcentrationLibRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationTop10ClientConcentrationLibService;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationTop10ClientConcentrationLib;

import java.util.List;

/**
* @description 金融协会报送-最大10家客户（含集团）集中度统计表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@RestController
public class AssociationTop10ClientConcentrationLibController implements AssociationTop10ClientConcentrationLibApi {

    @Resource
    private AssociationTop10ClientConcentrationLibService associationTop10ClientConcentrationLibService;

    @Override
    public R<Void> add(AssociationTop10ClientConcentrationLibAddREQ req) {
        associationTop10ClientConcentrationLibService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationTop10ClientConcentrationLibModifyREQ req){
        associationTop10ClientConcentrationLibService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationTop10ClientConcentrationLibListRSP>> list(AssociationTop10ClientConcentrationLibListREQ req){
        Page<AssociationTop10ClientConcentrationLib> data = associationTop10ClientConcentrationLibService.list(req);
        List<AssociationTop10ClientConcentrationLibListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationTop10ClientConcentrationLibListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationTop10ClientConcentrationLibRemoveREQ req){
        associationTop10ClientConcentrationLibService.remove(req);
        return R.ok();
    }

}