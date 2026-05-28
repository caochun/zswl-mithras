package cn.zswltech.mithras.service.controller.associationreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationReportDataAccessApi;
import cn.zswltech.mithras.dto.associationreport.AssociationReportDataAccessAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationReportDataAccessModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationReportDataAccessListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationReportDataAccessListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationReportDataAccessRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationReportDataAccessService;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationReportDataAccess;

import java.util.List;

/**
* @description 金融局报表数据权限
* @author hspcadmin
* @date 2025-09-25
*/
@RestController
public class AssociationReportDataAccessController implements AssociationReportDataAccessApi {

    @Resource
    private AssociationReportDataAccessService associationReportDataAccessService;

    @Override
    public R<Void> add(AssociationReportDataAccessAddREQ req) {
        associationReportDataAccessService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationReportDataAccessModifyREQ req){
        associationReportDataAccessService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationReportDataAccessListRSP>> list(AssociationReportDataAccessListREQ req){
        Page<AssociationReportDataAccess> data = associationReportDataAccessService.list(req);
        List<AssociationReportDataAccessListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationReportDataAccessListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationReportDataAccessRemoveREQ req){
        associationReportDataAccessService.remove(req);
        return R.ok();
    }

}