package cn.zswltech.mithras.customer.infohistory.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.client.InfoHistoryApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.infohistory.InfoHistoryDetailREQ;
import cn.zswltech.mithras.dto.client.infohistory.InfoHistoryDetailRSP;
import cn.zswltech.mithras.dto.client.infohistory.InfoHistoryListREQ;
import cn.zswltech.mithras.dto.client.infohistory.InfoHistoryListRSP;
import cn.zswltech.mithras.customer.infohistory.persistence.model.InfoHistory;
import cn.zswltech.mithras.customer.infohistory.application.InfoHistoryService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

import static cn.hutool.core.bean.BeanUtil.copyProperties;

/**
 * @author luyi
 */
@RestController
public class InfoHistoryController implements InfoHistoryApi {

    @Resource
    private InfoHistoryService infoHistoryService;

    @Override
    public R<PageR<InfoHistoryListRSP>> list(InfoHistoryListREQ req) {
        Page<InfoHistory> data = infoHistoryService.list(req);
        List<InfoHistoryListRSP> list = BeanUtil.copyToList(data.getRecords(), InfoHistoryListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<InfoHistoryDetailRSP> detail(InfoHistoryDetailREQ req) {
        return R.ok(copyProperties(infoHistoryService.detail(req.getId()), InfoHistoryDetailRSP.class));
    }
}
