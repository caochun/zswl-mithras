package cn.zswltech.mithras.service.controller.associationreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationMajorMattersBasicReportLibApi;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersBasicReportLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersBasicReportLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersBasicReportLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersBasicReportLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersBasicReportLibRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationMajorMattersBasicReportLibService;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationMajorMattersBasicReportLib;


/**
* @description 重大事项报告表-基本信息(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@RestController
public class AssociationMajorMattersBasicReportLibController implements AssociationMajorMattersBasicReportLibApi {

    @Resource
    private AssociationMajorMattersBasicReportLibService associationMajorMattersBasicReportLibService;

    @Override
    public R<Void> add(AssociationMajorMattersBasicReportLibAddREQ req) {
        associationMajorMattersBasicReportLibService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationMajorMattersBasicReportLibModifyREQ req){
        associationMajorMattersBasicReportLibService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationMajorMattersBasicReportLibListRSP>> list(AssociationMajorMattersBasicReportLibListREQ req){
        Page<AssociationMajorMattersBasicReportLib> data = associationMajorMattersBasicReportLibService.list(req);
        List<AssociationMajorMattersBasicReportLibListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationMajorMattersBasicReportLibListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationMajorMattersBasicReportLibRemoveREQ req){
        associationMajorMattersBasicReportLibService.remove(req);
        return R.ok();
    }

}