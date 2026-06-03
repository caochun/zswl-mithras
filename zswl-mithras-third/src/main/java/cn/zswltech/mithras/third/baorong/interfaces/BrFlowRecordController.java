package cn.zswltech.mithras.third.baorong.interfaces;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.capital.BrFlowRecordApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.capital.BrFlowRecordCountRSP;
import cn.zswltech.mithras.dto.capital.BrFlowRecordListREQ;
import cn.zswltech.mithras.dto.capital.BrFlowRecordListRSP;
import cn.zswltech.mithras.dto.capital.BrFlowRecordRemoveREQ;
import cn.zswltech.mithras.third.mapper.model.BrFlowRecord;
import cn.zswltech.mithras.third.baorong.application.BrFlowRecordService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author vico
 * @description 保融流水表
 * @date 2024-06-17
 */
@RestController
public class BrFlowRecordController implements BrFlowRecordApi {

    @Resource
    private BrFlowRecordService brFlowRecordService;

    @Override
    public R<PageR<BrFlowRecordListRSP>> list(BrFlowRecordListREQ req) {
        Page<BrFlowRecord> data = brFlowRecordService.list(req);
        List<BrFlowRecordListRSP> list = BeanUtil.copyToList(data.getRecords(), BrFlowRecordListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<BrFlowRecordCountRSP> listCount() {
        return R.ok(brFlowRecordService.listCount());
    }

    @Override
    public R<Void> remove(BrFlowRecordRemoveREQ req) {
        brFlowRecordService.remove(req);
        return R.ok();
    }

    @Override
    public R<Void> ignore(BrFlowRecordRemoveREQ req) {
        brFlowRecordService.ignore(req);
        return R.ok();
    }

}