package cn.zswltech.mithras.service.controller.ep;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.ep.EpCaseinfoApi;
import cn.zswltech.mithras.dto.ep.EpCaseInfoRSP;
import cn.zswltech.mithras.service.service.ep.EpCaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 张欣
 */
@Slf4j
@RestController
public class EpCaseinController implements EpCaseinfoApi {

    @Resource
    private EpCaseInfoService epCaseInfoService;

    @Override
    public R<EpCaseInfoRSP> getCaseInfo(@RequestParam Long id) {
        log.debug("getCaseInfo id:{}", id);
        return epCaseInfoService.getCaseInfo(id);
    }
}
