package cn.zswltech.mithras.report.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.report.CrFiveClassApi;
import cn.zswltech.mithras.dto.report.fiveclass.*;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.report.auth.ReportAuthCheck;
import cn.zswltech.mithras.report.service.agg.CrFiveClassAggSerivce;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 征信报送-五级分类表接口
 *
 * @author wangchuanhao
 * @date 2023/1/11 3:55 PM
 */
@RestController
public class CrFiveClassController implements CrFiveClassApi {

    @Resource
    private CrFiveClassAggSerivce crFiveClassAggSerivce;

    @Override
    @ReportAuthCheck(type = ReportAuthCheck.AuthType.VIEW)
    public R<List<Map<String, DiffValue>>> list(FiveClassListREQ req) {
        return R.ok(crFiveClassAggSerivce.pageList(req));
    }

    @Override
    @ReportAuthCheck(type = ReportAuthCheck.AuthType.EDIT)
    public R<Void> add(FiveClassAddREQ req) {
        crFiveClassAggSerivce.add(req);
        return R.ok();
    }

    @Override
    @ReportAuthCheck(type = ReportAuthCheck.AuthType.EDIT)
    public R<Void> modify(FiveClassModifyREQ req) {
        crFiveClassAggSerivce.modify(req);
        return R.ok();
    }

    @Override
    @ReportAuthCheck(type = ReportAuthCheck.AuthType.EDIT)
    public R<Void> remove(FiveClassRemoveREQ req) {
        crFiveClassAggSerivce.remove(req);
        return R.ok();
    }

    @Override
    @ReportAuthCheck(type = ReportAuthCheck.AuthType.EDIT)
    public R<Void> cancelRemove(FiveClassRemoveREQ req) {
        crFiveClassAggSerivce.cancelRemove(req);
        return R.ok();
    }

}
