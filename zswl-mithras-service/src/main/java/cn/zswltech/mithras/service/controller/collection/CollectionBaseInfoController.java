package cn.zswltech.mithras.service.controller.collection;

import cn.zswltech.mithras.api.collection.CollectionBaseInfoApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.collection.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonViewMainAuthCheckerNew;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionOverdueRecordInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @create: 2022-08-17
 **/
@RestController
@Slf4j
public class CollectionBaseInfoController implements CollectionBaseInfoApi {
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private CollectionOverdueRecordInfoService collectionOverdueRecordInfoService;
    @Resource
    private HttpServletResponse httpServletResponse;

    @Override
    public R<PageR<CollectionBaseInfoListRSP>> list(@Valid CollectionBaseInfoREQ req) {
        return R.ok(collectionBaseInfoService.list(req));
    }

    @Override
    public void exportList(CollectionBaseInfoREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("收款核销列表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            collectionBaseInfoService.exportList(req, httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出收款核销列表发生未知异常", e);
            throw new MithrasException("导出收款核销列表发生未知异常");
        }
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = CommonViewMainAuthCheckerNew.class, businessModule = BusinessModuleEnum.COLLECTION)
    public R<CollectionBaseInfoRSP> detail(@Valid CollectionBaseInfoDetailREQ req) {
        return R.ok(collectionBaseInfoService.detail(req));
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = CommonViewMainAuthCheckerNew.class, businessModule = BusinessModuleEnum.COLLECTION)
    public R<CollectionPenaltyInterestRSP> penaltyInterestDetail(@Valid CollectionBaseInfoDetailREQ req) {
        return R.ok(collectionOverdueRecordInfoService.detail(req));
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = CommonViewMainAuthCheckerNew.class, businessModule = BusinessModuleEnum.COLLECTION)
    public R<PageR<PenaltyInterestListRSP>> penaltyInterestList(@Valid CollectionPenaltyInterestREQ req) {
        return R.ok(collectionOverdueRecordInfoService.list(req));
    }

//    @Override
//    @DataAuthCheck(checkerClass = CollectionWriteOffAuthChecker.class, businessModule = BusinessModuleEnum.PAYMENT)
//    public R<Void> updateRecord(@Valid CollectionBaseInfoUpdateREQ req) {
//        collectionOverdueRecordInfoService.update(req);
//        return R.ok();
//    }

}
