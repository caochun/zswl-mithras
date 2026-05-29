package cn.zswltech.mithras.service.controller.associationreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationShahChangeInfoLibApi;
import cn.zswltech.mithras.dto.associationreport.AssociationShahChangeInfoLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationShahChangeInfoLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationShahChangeInfoLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationShahChangeInfoLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationShahChangeInfoLibRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationShahChangeInfoLibService;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationShahChangeInfoLib;


/**
* @description 股东股权信息一览表-股东变更记录(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@RestController
public class AssociationShahChangeInfoLibController implements AssociationShahChangeInfoLibApi {

    @Resource
    private AssociationShahChangeInfoLibService associationShahChangeInfoLibService;

    @Override
    public R<Void> add(AssociationShahChangeInfoLibAddREQ req) {
        associationShahChangeInfoLibService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationShahChangeInfoLibModifyREQ req){
        associationShahChangeInfoLibService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationShahChangeInfoLibListRSP>> list(AssociationShahChangeInfoLibListREQ req){
        Page<AssociationShahChangeInfoLib> data = associationShahChangeInfoLibService.list(req);
        List<AssociationShahChangeInfoLibListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationShahChangeInfoLibListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationShahChangeInfoLibRemoveREQ req){
        associationShahChangeInfoLibService.remove(req);
        return R.ok();
    }

}