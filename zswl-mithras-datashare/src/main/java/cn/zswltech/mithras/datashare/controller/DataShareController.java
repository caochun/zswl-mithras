package cn.zswltech.mithras.datashare.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.DataShareApi;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.client.share.DataShareREQ;
import cn.zswltech.mithras.dto.client.share.DataShareRSP;
import cn.zswltech.mithras.dto.client.share.DataShareUserREQ;
import cn.zswltech.mithras.dto.client.share.DataShareUserRSP;
import cn.zswltech.mithras.datashare.service.DataShareMerchantsService;
import cn.zswltech.mithras.datashare.service.DataShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 数据共享接口
 * @ClassName DataShareController
 * @Description
 * @Author jackerhe
 * @Date 2022/8/1 3:26 下午
 * @Version 1.0
 **/
@RestController
public class DataShareController implements DataShareApi {

    @Autowired
    private DataShareMerchantsService dataShareMerchantsService;

    @Autowired
    private DataShareService dataShareService;

    /**
     * 获取客商信息
     * @author: jackerhe
     * @date: 2022/8/1 3:27 下午
     **/
    @Override
    public R<DataShareRSP> getMerchants(@Valid DataShareREQ req) {
        DataShareRSP rsp = new DataShareRSP();
        BeanUtil.copyProperties(dataShareMerchantsService.getMerchants(req), rsp);
        return R.ok(rsp);
    }

    /**
     *同步客商信息
     * @author: jackerhe
     * @date: 2022/8/1 3:27 下午
     **/
    @Override
    public R<Boolean> syncMerchants() {
        dataShareService.syscMainCode();
        return R.ok(dataShareService.syncMerchants());
    }

    @Override
    public R<DataShareUserRSP> syscMainCode(@Valid DataShareUserREQ req) {
        return R.ok(dataShareService.getMainCode(req));
    }

    @Override
    public R<List<SelectRSP>> syscMainOrg() {
        return R.ok(dataShareService.getMainOrg());
    }


}
