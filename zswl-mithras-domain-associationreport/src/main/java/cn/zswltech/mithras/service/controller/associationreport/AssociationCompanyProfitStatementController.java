package cn.zswltech.mithras.service.controller.associationreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationCompanyProfitStatementApi;
import cn.zswltech.mithras.dto.associationreport.AssociationCompanyProfitStatementAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationCompanyProfitStatementModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationCompanyProfitStatementListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationCompanyProfitStatementListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationCompanyProfitStatementRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationCompanyProfitStatementService;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationCompanyProfitStatement;

/**
* @description 公司利润表数据表
* @author vico
* @date 2025-04-18
*/
@RestController
public class AssociationCompanyProfitStatementController implements AssociationCompanyProfitStatementApi {

    @Resource
    private AssociationCompanyProfitStatementService associationCompanyProfitStatementService;

    @Override
    public R<Void> add(AssociationCompanyProfitStatementAddREQ req) {
        associationCompanyProfitStatementService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationCompanyProfitStatementModifyREQ req){
        associationCompanyProfitStatementService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationCompanyProfitStatementListRSP>> list(AssociationCompanyProfitStatementListREQ req){
        Page<AssociationCompanyProfitStatement> data = associationCompanyProfitStatementService.list(req);
        List<AssociationCompanyProfitStatementListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationCompanyProfitStatementListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationCompanyProfitStatementRemoveREQ req){
        associationCompanyProfitStatementService.remove(req);
        return R.ok();
    }

}