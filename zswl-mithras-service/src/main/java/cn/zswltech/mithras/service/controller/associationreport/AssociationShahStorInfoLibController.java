package cn.zswltech.mithras.service.controller.associationreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationShahStorInfoLibApi;
import cn.zswltech.mithras.dto.associationreport.AssociationShahStorInfoLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationShahStorInfoLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationShahStorInfoLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationShahStorInfoLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationShahStorInfoLibRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationShahStorInfoLibService;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationShahStorInfoLib;


/**
* @description 股东股权信息一览表-股东股权信息(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@RestController
public class AssociationShahStorInfoLibController implements AssociationShahStorInfoLibApi {

    @Resource
    private AssociationShahStorInfoLibService associationShahStorInfoLibService;

    @Override
    public R<Void> add(AssociationShahStorInfoLibAddREQ req) {
        associationShahStorInfoLibService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationShahStorInfoLibModifyREQ req){
        associationShahStorInfoLibService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationShahStorInfoLibListRSP>> list(AssociationShahStorInfoLibListREQ req){
        Page<AssociationShahStorInfoLib> data = associationShahStorInfoLibService.list(req);
        List<AssociationShahStorInfoLibListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationShahStorInfoLibListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationShahStorInfoLibRemoveREQ req){
        associationShahStorInfoLibService.remove(req);
        return R.ok();
    }

}