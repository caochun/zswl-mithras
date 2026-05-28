package cn.zswltech.mithras.service.controller;

import cn.zswltech.mithras.api.OnlyOfficeApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dto.onlyoffice.DocDetailRSP;
import cn.zswltech.mithras.api.dto.onlyoffice.GetDocDetailREQ;
import cn.zswltech.mithras.service.service.OnlyOfficeService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 对接onlyoffice组件 实现文档在线编辑
 *
 * @author wangchuanhao
 * @date 2022/7/8 11:32 AM
 */
@RestController
public class OnlyOfficeController implements OnlyOfficeApi {

    @Resource
    private OnlyOfficeService onlyOfficeService;

    @Override
    public R<DocDetailRSP> docDetail(GetDocDetailREQ req) {
        return R.ok(onlyOfficeService.docDetail(req));
    }

    @Override
    public void download(String key) {
        onlyOfficeService.download(key);
    }

    @Override
    public String callback(String callbackData) {
        onlyOfficeService.callback(callbackData);
        return "{\"error\": 0}";
    }

}
