package cn.zswltech.mithras.service.controller.budget;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.budget.BudgetExamineApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.IdREQ;
import cn.zswltech.mithras.dto.budget.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetExamine;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.service.service.budget.BudgetExamineService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @description 预算管理-预算考核
* @author vico
* @date 2025-04-11
*/
@RestController
public class BudgetExamineController implements BudgetExamineApi {

    @Resource
    private BudgetExamineService budgetExamineService;
    @Resource
    private Id2NameService id2NameService;

    @Override
    public R<Void> add(BudgetExamineAddREQ req) {
        budgetExamineService.add(req);
        return R.ok();
    }


    @Override
    public R<PageR<BudgetExamineListRSP>> list(BudgetExamineListREQ req){
        Page<BudgetExamine> data = budgetExamineService.list(req);
        List<BudgetExamineListRSP> list = BeanUtil.copyToList(data.getRecords(), BudgetExamineListRSP.class);
        if (ObjectUtil.isNotEmpty(list)) {
            Map<Long, String> userId2Name = id2NameService.sysUserId2Name(list.stream().map(BudgetExamineListRSP::getSubmitUserId).collect(Collectors.toList()));
            list.forEach(e -> {
                e.setSubmitUserName(userId2Name.get(e.getSubmitUserId()));
            });
        }
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(BudgetExamineRemoveREQ req){
        budgetExamineService.remove(req);
        return R.ok();
    }

    @Override
    public R<Void> submit(@Valid IdREQ req) {
        budgetExamineService.submit(req);
        return R.ok();
    }

    @Override
    public R<BudgetExamineListRSP> detail(@Valid IdREQ req) {
        BudgetExamine byId = budgetExamineService.getById(req.getId());
        if (ObjectUtil.isEmpty(byId)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        return R.ok(BeanUtil.copyProperties(byId, BudgetExamineListRSP.class));
    }

}