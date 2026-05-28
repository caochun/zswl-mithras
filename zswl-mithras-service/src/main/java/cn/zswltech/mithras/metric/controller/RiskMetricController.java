/*
package cn.zswltech.mithras.metric.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.metric.*;
import cn.zswltech.mithras.metric.mapper.model.RiskMetric;
import cn.zswltech.mithras.metric.service.RiskMetricService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

*/
/**
 * @author yibin
 *//*

@RestController
@MapperScan(value = "cn.zswltech.mithras.metric.mapper")
public class RiskMetricController implements RiskMetricApi {

    @Resource
    private RiskMetricService riskMetricService;

    @Override
    public R<Long> add(RiskMetricAddReq req) {
        return R.ok(riskMetricService.add(req));
    }

    @Override
    public R<Void> remove(RiskMetricIdReq req) {
        riskMetricService.remove(req.getId());
        return R.ok();
    }

    @Override
    public R<RiskMetricDetailRsp> detail(RiskMetricIdReq req) {
        return R.ok(BeanUtil.copyProperties(riskMetricService.getById(req.getId()), RiskMetricDetailRsp.class));
    }


    @Override
    public R<Void> modify(RiskMetricModifyReq req) {
        riskMetricService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<RiskMetricListRsp>> list(RiskMetricListReq req) {
        Page<RiskMetric> data = riskMetricService.list(req);
        List<RiskMetric> records = data.getRecords();
        List<RiskMetricListRsp> list = BeanUtil.copyToList(records, RiskMetricListRsp.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }
}
*/
