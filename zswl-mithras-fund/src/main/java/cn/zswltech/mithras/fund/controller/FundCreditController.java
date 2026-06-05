package cn.zswltech.mithras.fund.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.FundCreditApi;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.fund.*;
import cn.zswltech.mithras.dto.materialsfile.FundMaterialListRSP;
import cn.zswltech.mithras.fund.application.FundCreditApplicationService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
public class FundCreditController implements FundCreditApi {
    @Resource
    private FundCreditApplicationService fundCreditApplicationService;

    @Override
    public R<Long> add(FundCreditAddREQ req) {
        return fundCreditApplicationService.add(req);
    }

    @Override
    public R<Void> modify(FundCreditModifyREQ req) {
        return fundCreditApplicationService.modify(req);
    }

    @Override
    public R<FundCreditListRSP> list(FundCreditListREQ req) {
        return fundCreditApplicationService.list(req);
    }

    @Override
    public R<FundCreditDetailRSP> detail(FundCreditDetailREQ req) {
        return fundCreditApplicationService.detail(req);
    }

    @Override
    public R<Void> remove(FundCreditRemoveREQ req) {
        return fundCreditApplicationService.remove(req);
    }

    @Override
    public R<CreditLimitRsp> limit(CreditLimitReq req) {
        return fundCreditApplicationService.limit(req);
    }

    @Override
    public R<List<FundMaterialListRSP>> fileList(FundCreditDetailREQ req) {
        return fundCreditApplicationService.fileList(req);
    }

    @Override
    public R<Void> fileUpload(MultipartFile file, Long belongId) {
        return fundCreditApplicationService.fileUpload(file, belongId);
    }

    @Override
    public R<Void> fileRemove(FundCreditRemoveREQ req) {
        return fundCreditApplicationService.fileRemove(req);
    }

    @Override
    public R<FileListRSP> fileDownload(FundCreditRemoveREQ req) throws IOException {
        return fundCreditApplicationService.fileDownload(req);
    }

    @Override
    public R<List<FundCreditListRSP.FundCreditList>> listEffect(@Valid FundCreditListREQ req) {
        return fundCreditApplicationService.listEffect(req);
    }

    @Override
    public R<Void> invalid(FundCreditSingletonIdREQ req) {
        return fundCreditApplicationService.invalid(req);
    }

    @Override
    public R<FundCreditLimitDetailRSP> limitDetail(FundCreditSingletonIdREQ req) {
        return fundCreditApplicationService.limitDetail(req);
    }
}
