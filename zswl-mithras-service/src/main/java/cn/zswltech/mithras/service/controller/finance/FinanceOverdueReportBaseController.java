package cn.zswltech.mithras.service.controller.finance;
import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.finance.FinanceOverdueReportBaseApi;
import cn.zswltech.mithras.dto.finance.overdue.*;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceOverdueReportBase;
import cn.zswltech.mithras.service.service.finance.FinanceOverdueReportBaseService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 逾期报送计划表
* @author vico
* @date 2025-09-15
*/
@RestController
public class FinanceOverdueReportBaseController implements FinanceOverdueReportBaseApi {

    @Resource
    private FinanceOverdueReportBaseService financeOverdueReportBaseService;

    @Override
    public R<Long> add(FinanceOverdueReportBaseAddREQ req) {
        return R.ok(financeOverdueReportBaseService.add(req));
    }

    @Override
    public R<PageR<FinanceOverdueReportBaseListRSP>> list(FinanceOverdueReportBaseListREQ req){
        Page<FinanceOverdueReportBase> data = financeOverdueReportBaseService.list(req);
        List<FinanceOverdueReportBaseListRSP> list = BeanUtil.copyToList(data.getRecords(), FinanceOverdueReportBaseListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> close(FinanceOverdueReportBaseRemoveREQ req){
        financeOverdueReportBaseService.close(req);
        return R.ok();
    }

}