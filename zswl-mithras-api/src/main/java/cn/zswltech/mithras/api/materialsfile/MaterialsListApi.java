package cn.zswltech.mithras.api.materialsfile;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.file.FileDownLoadRSP;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.materialsfile.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;


@Api(tags = "资料清单-接口")
public interface MaterialsListApi {

    @ApiOperation("资料清单-上传")
    @PostMapping("/materials/upload")
    R<Void> upload(@RequestParam("file") MultipartFile file,
                   @RequestParam("belongId") Long belongId,
                   @RequestParam("materialsType") String materialsType,
                   @RequestParam("businessType") String businessType,
                   @RequestParam(name = "materialsSubType", required = false) String materialsSubType
    );

    @ApiOperation("资料清单-列表")
    @PostMapping("/materials/list")
    R<List<MaterialsListListRSP>> list(@RequestBody @Valid List<MaterialsListListREQ> req);

    @ApiOperation("资料清单-删除")
    @PostMapping("/materials/remove")
    R<Void> remove(@RequestBody @Valid MaterialsListRemoveREQ req);

    @ApiOperation("资料清单-下载")
    @GetMapping("/materials/download")
    R<FileDownLoadRSP> download(@RequestParam("ids") List<Long> ids);

    @ApiOperation("资料清单-下载")
    @GetMapping("/materials/download/query")
    R<List<FileDownLoadRSP>> downloadQuery(@RequestParam("ids") List<Long> ids);

    @ApiOperation("资料清单（不做权限校验）-下载")
    @GetMapping("/materials/downloadCommon")
    R<FileDownLoadRSP> downloadCommon(@Valid MaterialsListDownloadREQ req) throws IOException;

    @ApiOperation("立项资料清单-批量下载")
    @GetMapping("/materials/proj/download")
    void projDownload(@Valid MaterialsListDownloadREQ req) throws Exception;

    @ApiOperation("项目资料清单-列表")
    @PostMapping("/materials/proj/list")
    R<List<ProjMaterialsListListRSP>> projList(@RequestBody @Valid ProjMaterialsListListREQ req);

    @ApiOperation("项目评审资料清单-列表")
    @PostMapping("/materials/proj/review/list")
    R<List<ProjMaterialsListListRSP>> projReviewList(@RequestBody @Valid ProjReviewMaterialsListREQ req);

    @ApiOperation("项目评审资料清单-审核意见")
    @PostMapping("/materials/proj/review/comments")
    R<String> projReviewMaterialComments(@RequestBody @Valid ProjReviewMaterialCommentsREQ req);

    @ApiOperation("项目定价资料清单-列表")
    @PostMapping("/materials/proj/pricing/list")
    R<List<ProjMaterialsListListRSP>> projPricingList(@RequestBody @Valid ProjPricingMaterialsListREQ req);

    @ApiOperation("合同管理-资料清单列表")
    @PostMapping("/materials/contract/list")
    R<List<ContractMaterialListRSP>> contractMaterialList(@RequestBody @Valid ContractMaterialListREQ contractMaterialListREQ);

    @ApiOperation("合同管理-资料清单-租赁物审核资料")
    @PostMapping("/materials/contract/lease/list")
    R<List<ContractMaterialListRSP>> contractLeaseMaterialList(@RequestBody @Valid ContractMaterialListREQ contractMaterialListREQ);

    /**
     * 由于/materials/payment/list已使用，所以此处只能使用/materials/payment/listOther
     * 逻辑和前面几个接口一样，展示历史模块的资料数据
     *
     * @param paymentMaterialListREQ
     * @return
     */
    @ApiOperation("付款申请-资料清单列表")
    @PostMapping("/materials/payment/listOther")
    R<List<PaymentMaterialListRSP>> paymentMaterialList(@RequestBody @Valid PaymentMaterialListREQ paymentMaterialListREQ);

    @ApiOperation("资料清单预览判断接口")
    @PostMapping("/materials/preview")
    R<MaterialsPreviewRSP> preview(@RequestBody @Valid MaterialsListPreviewREQ req);

    @ApiOperation("资料清单PDF预览接口")
    @GetMapping("/materials/pdf/preview")
    void previewPdf(Long id, Integer idType, String version);

    @ApiOperation("集团授信立项资料清单-列表")
    @PostMapping("/materials/group/credit/establish/list")
    R<List<ProjMaterialsListListRSP>> groupCreditEstablishList(@RequestBody @Valid GroupCreditEstablishMaterialsListListREQ req);

    @ApiOperation("集团授信评审资料清单-列表")
    @PostMapping("/materials/group/credit/review/list")
    R<List<ProjMaterialsListListRSP>> groupCreditReviewList(@RequestBody @Valid GroupCreditReviewMaterialsListListREQ req);

    @ApiOperation("资金收付款资料清单-列表")
    @PostMapping("/materials/fund/receipt/reapy/list")
    R<List<Pair<String, List<FileListRSP>>>> materialsFundReceiptRepay(@RequestBody @Valid SinglePkREQ req);

}
