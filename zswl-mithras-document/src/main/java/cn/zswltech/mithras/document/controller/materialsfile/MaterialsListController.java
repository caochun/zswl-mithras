package cn.zswltech.mithras.document.controller.materialsfile;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.materialsfile.MaterialsListApi;
import cn.zswltech.mithras.document.materialsfile.MaterialsListApplicationService;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.file.FileDownLoadRSP;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.materialsfile.ContractMaterialListREQ;
import cn.zswltech.mithras.dto.materialsfile.ContractMaterialListRSP;
import cn.zswltech.mithras.dto.materialsfile.GroupCreditEstablishMaterialsListListREQ;
import cn.zswltech.mithras.dto.materialsfile.GroupCreditReviewMaterialsListListREQ;
import cn.zswltech.mithras.dto.materialsfile.MaterialsListDownloadREQ;
import cn.zswltech.mithras.dto.materialsfile.MaterialsListListREQ;
import cn.zswltech.mithras.dto.materialsfile.MaterialsListListRSP;
import cn.zswltech.mithras.dto.materialsfile.MaterialsListPreviewREQ;
import cn.zswltech.mithras.dto.materialsfile.MaterialsListRemoveREQ;
import cn.zswltech.mithras.dto.materialsfile.MaterialsPreviewRSP;
import cn.zswltech.mithras.dto.materialsfile.PaymentMaterialListREQ;
import cn.zswltech.mithras.dto.materialsfile.PaymentMaterialListRSP;
import cn.zswltech.mithras.dto.materialsfile.ProjMaterialsListListREQ;
import cn.zswltech.mithras.dto.materialsfile.ProjMaterialsListListRSP;
import cn.zswltech.mithras.dto.materialsfile.ProjPricingMaterialsListREQ;
import cn.zswltech.mithras.dto.materialsfile.ProjReviewMaterialCommentsREQ;
import cn.zswltech.mithras.dto.materialsfile.ProjReviewMaterialsListREQ;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
public class MaterialsListController implements MaterialsListApi {

    @Resource
    private MaterialsListApplicationService materialsListApplicationService;

    @Override
    public R<Void> upload(MultipartFile file, Long belongId, String materialsType, String businessType, String materialsSubType) {
        return materialsListApplicationService.upload(file, belongId, materialsType, businessType, materialsSubType);
    }

    @Override
    public R<List<MaterialsListListRSP>> list(@Valid List<MaterialsListListREQ> req) {
        return materialsListApplicationService.list(req);
    }

    @Override
    public R<Void> remove(@Valid MaterialsListRemoveREQ req) {
        return materialsListApplicationService.remove(req);
    }

    @Override
    public R<FileDownLoadRSP> download(List<Long> ids) {
        return materialsListApplicationService.download(ids);
    }

    @Override
    public R<List<FileDownLoadRSP>> downloadQuery(List<Long> ids) {
        return materialsListApplicationService.downloadQuery(ids);
    }

    @Override
    public R<FileDownLoadRSP> downloadCommon(@Valid MaterialsListDownloadREQ req) throws IOException {
        return materialsListApplicationService.downloadCommon(req);
    }

    @Override
    public void projDownload(@Valid MaterialsListDownloadREQ req) throws Exception {
        materialsListApplicationService.projDownload(req);
    }

    @Override
    public R<List<ProjMaterialsListListRSP>> projList(@Valid ProjMaterialsListListREQ req) {
        return materialsListApplicationService.projList(req);
    }

    @Override
    public R<List<ProjMaterialsListListRSP>> projReviewList(@Valid ProjReviewMaterialsListREQ req) {
        return materialsListApplicationService.projReviewList(req);
    }

    @Override
    public R<String> projReviewMaterialComments(@Valid ProjReviewMaterialCommentsREQ req) {
        return materialsListApplicationService.projReviewMaterialComments(req);
    }

    @Override
    public R<List<ProjMaterialsListListRSP>> projPricingList(@Valid ProjPricingMaterialsListREQ req) {
        return materialsListApplicationService.projPricingList(req);
    }

    @Override
    public R<List<ContractMaterialListRSP>> contractMaterialList(@Valid ContractMaterialListREQ contractMaterialListREQ) {
        return materialsListApplicationService.contractMaterialList(contractMaterialListREQ);
    }

    @Override
    public R<List<ContractMaterialListRSP>> contractLeaseMaterialList(@Valid ContractMaterialListREQ contractMaterialListREQ) {
        return materialsListApplicationService.contractLeaseMaterialList(contractMaterialListREQ);
    }

    @Override
    public R<List<PaymentMaterialListRSP>> paymentMaterialList(@Valid PaymentMaterialListREQ paymentMaterialListREQ) {
        return materialsListApplicationService.paymentMaterialList(paymentMaterialListREQ);
    }

    @Override
    public R<MaterialsPreviewRSP> preview(@Valid MaterialsListPreviewREQ req) {
        return materialsListApplicationService.preview(req);
    }

    @Override
    public void previewPdf(Long id, Integer idType, String version) {
        materialsListApplicationService.previewPdf(id, idType, version);
    }

    @Override
    public R<List<ProjMaterialsListListRSP>> groupCreditEstablishList(@Valid GroupCreditEstablishMaterialsListListREQ req) {
        return materialsListApplicationService.groupCreditEstablishList(req);
    }

    @Override
    public R<List<ProjMaterialsListListRSP>> groupCreditReviewList(@Valid GroupCreditReviewMaterialsListListREQ req) {
        return materialsListApplicationService.groupCreditReviewList(req);
    }

    @Override
    public R<List<Pair<String, List<FileListRSP>>>> materialsFundReceiptRepay(@Valid SinglePkREQ req) {
        return materialsListApplicationService.materialsFundReceiptRepay(req);
    }
}
