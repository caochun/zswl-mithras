//package cn.zswltech.mithras.others.service.third;
//
//import cn.hutool.core.lang.Assert;
//import cn.hutool.http.HttpUtil;
//import cn.zswl.oss.core.minio.MinioOssClient;
//import cn.zswltech.mithras.others.service.ApplicationTest;
//import cn.zswltech.mithras.message.config.qiyuesuo.QysConfig;
//import cn.zswltech.mithras.message.config.qiyuesuo.QysProperties;
//import cn.zswltech.mithras.document.mapper.model.MaterialsList;
//import cn.zswltech.mithras.foundation.exception.MithrasException;
//import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
//import lombok.extern.slf4j.Slf4j;
//import net.qiyuesuo.sdk.api.ContractService;
//import net.qiyuesuo.sdk.api.SignService;
//import net.qiyuesuo.sdk.bean.company.CompanySignRequest;
//import net.qiyuesuo.sdk.bean.company.TenantType;
//import net.qiyuesuo.sdk.bean.contract.*;
//import net.qiyuesuo.sdk.bean.document.CreateDocumentRequest;
//import net.qiyuesuo.sdk.bean.document.CreateDocumentResult;
//import net.qiyuesuo.sdk.bean.sign.SignUrlRequest;
//import net.qiyuesuo.sdk.bean.sign.Signatory;
//import net.qiyuesuo.sdk.common.http.StreamFile;
//import net.qiyuesuo.v2sdk.SdkV2Client;
//import net.qiyuesuo.v2sdk.request.CategoryDetailRequest;
//import net.qiyuesuo.v2sdk.response.CategoryDetailResponse;
//import net.qiyuesuo.v2sdk.response.SdkResponse;
//import net.qiyuesuo.v2sdk.utils.JSONUtils;
//import org.junit.Test;
//
//import javax.annotation.Resource;
//import java.util.Collections;
//
///**
// * @author dingqi
// * @date 2022/9/13
// * @description
// */
//@Slf4j
//public class QysServiceTest extends ApplicationTest {
//    @Resource
//    private QysProperties qysProperties;
//    @Resource
//    private MinioOssClient ossClient;
//    @Resource
//    private MaterialsListService materialsListService;
//    @Resource
//    private SdkV2Client sdkV2Client;
//    @Resource(name = QysConfig.CONTRACT_BEAN_NAME)
//    private ContractService contractService;
//    @Resource
//    private SignService signService;
//
//    @Test
//    public void completeContract() throws Exception {
//        contractService.complete(3007203414898880540L);
//        log.info("封存合同成功");
//    }
//
//    @Test
//    public void editContractTest() throws Exception {
//        CreateContractRequest createContractRequest = new CreateContractRequest();
//        createContractRequest.setId(3007203414898880540L);
//        createContractRequest.setBizId("213");
//        createContractRequest.setSn("浙商租【2022】租字第(A-0080)号");
//        Signatory signatory = new Signatory();
//        signatory.setTenantType(TenantType.COMPANY);
//        signatory.setTenantName("浙江浙商融资租赁有限公司");
//        Action action = new Action();
//        action.setType(ActionType.CORPORATE);
//        signatory.setActions(Collections.singletonList(action));
//        createContractRequest.setSignatories(Collections.singletonList(signatory));
//        contractService.edit(createContractRequest);
//        log.info("编辑合同成功");
//    }
//
//    @Test
//    public void deleteContractTest() throws Exception {
//        contractService.delete(3007203414898880540L);
//    }
//
//    @Test
//    public void createDocumentTest() throws Exception {
//        Long materialsId = 2614L;
//        MaterialsList materialsList = Assert.notNull(materialsListService.getById(materialsId), () -> MithrasException.newException("文件不存在"));
//        String url = ossClient.getPreviewUrl(materialsList.getOssFilename(), 60 * 60);
//        CreateDocumentRequest createDocumentRequest = new CreateDocumentRequest();
//        createDocumentRequest.setFile(new StreamFile(materialsList.getFilename(), HttpUtil.createGet(url).execute().bodyStream()));
//        createDocumentRequest.setTitle(materialsList.getFilename());
//        createDocumentRequest.setFileType(materialsList.getSuffix());
//        CreateDocumentResult createDocumentResult = contractService.createByFile(createDocumentRequest);
//        log.info("创建合同文档成功[documentId: {}]", createDocumentResult.getDocumentId());
//    }
//
//    @Test
//    public void createContractTest() throws Exception {
//        Long materialsId = 2614L;
//        MaterialsList materialsList = Assert.notNull(materialsListService.getById(materialsId), () -> MithrasException.newException("文件不存在"));
//        CreateContractRequest createContractRequest = new CreateContractRequest();
//        createContractRequest.setSubject(materialsList.getFilename());
//        createContractRequest.setCategoryId(qysProperties.getFlowId());
//        createContractRequest.setTenantName("浙江浙商融资租赁有限公司");
//        createContractRequest.setDocuments(Collections.singletonList(3007201899375210522L));
//        Long contractId = contractService.createContractByCategory(createContractRequest);
//        log.info("创建合同成功[contractId: {}]", contractId);
//    }
//
//    @Test
//    public void viewContractUrlTest() throws Exception {
//        ViewUrlRequest viewUrlRequest = new ViewUrlRequest();
//        viewUrlRequest.setContractId(3007203414898880540L);
//        viewUrlRequest.setPageType(ViewUrlRequest.PageType.CONTENT);
//        String viewUrl = contractService.viewUrl(viewUrlRequest);
//        log.info("获取合同浏览地址成功[viewUrl: {}]", viewUrl);
//    }
//
//    @Test
//    public void signByCompanyTest() throws Exception {
//        CompanySignRequest companySignRequest = new CompanySignRequest();
//        companySignRequest.setContractId(3007203414898880540L);
//        companySignRequest.setTenantName("浙江浙商融资租赁有限公司");
//        Stamper stamper = new Stamper();
//        stamper.setDocumentId(3007201899375210522L);
//        stamper.setType(StamperType.SEAL_CORPORATE);
//        stamper.setSealId(3004682883133137086L);
//        stamper.setKeyword("浙江浙商融资租赁有限公司（盖章）");
//        stamper.setPage(-1);
//        companySignRequest.setStampers(Collections.singletonList(stamper));
//        signService.signByCompany(companySignRequest);
//        log.info("静默签署（内部公章）成功");
//    }
//
//    @Test
//    public void signContractUrlTest() throws Exception {
//        SignUrlRequest signUrlRequest = new SignUrlRequest();
//        signUrlRequest.setContractId(3007203414898880540L);
//        signUrlRequest.setTenantType(TenantType.COMPANY);
//        signUrlRequest.setTenantName("浙江浙商融资租赁有限公司");
//        String signUrl = contractService.signUrl(signUrlRequest);
//        log.info("获取合同签署地址成功[viewUrl: {}]", signUrl);
//    }
//
//    @Test
//    public void categoryDetailTest() throws Exception {
//        CategoryDetailRequest categoryDetailRequest = new CategoryDetailRequest(qysProperties.getFlowId());
//        String response = sdkV2Client.service(categoryDetailRequest);
//        SdkResponse<CategoryDetailResponse> sdkResponse = JSONUtils.toQysResponse(response, CategoryDetailResponse.class);
//        if(sdkResponse.getCode() == 0) {
//            CategoryDetailResponse result = sdkResponse.getResult();
//            log.info("查询用印流程详情成功[{}]", JSONUtils.toJson(result));
//        } else {
//            log.info("查询用印流程详情失败[{}]", JSONUtils.toJson(sdkResponse));
//        }
//    }
//}
