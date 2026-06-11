package cn.zswltech.mithras.others.service.third;

import cn.hutool.extra.spring.SpringUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.third.config.QiyuesuoConfig;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTextSignInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.contract.text.ContractTextManageService;
import cn.zswltech.mithras.application.orchestration.contract.text.ContractTextSignInfoService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.FileService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.third.service.QiyuesuoService;
import cn.zswltech.mithras.third.service.model.qiyuesuo.*;
import cn.zswltech.mithras.third.util.WatermarkUtil;
import cn.zswltech.mithras.web.MithrasApplication;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.ByteStreams;
import lombok.extern.slf4j.Slf4j;
import net.qiyuesuo.v3sdk.model.common.Signatory;
import net.qiyuesuo.v3sdk.model.contract.request.ContractCreatebycategoryRequest;
import net.qiyuesuo.v3sdk.model.contract.request.ContractDetailRequest;
import net.qiyuesuo.v3sdk.model.document.request.DocumentDownloadRequest;
import net.qiyuesuo.v3sdk.model.seal.request.SealListRequest;
import net.qiyuesuo.v3sdk.model.v2contract.request.V2ContractSignbycompanyRequest;
import net.qiyuesuo.v3sdk.model.v2contract.request.V2ContractSignbylegalpersonRequest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Files;
import java.util.List;

import static cn.zswltech.mithras.application.orchestration.contract.text.ContractTextSignInfoService.containsKeyword;

/**
 * @author bigbear
 * @date 2024/11/21 10:14
 * @description
 */
@Slf4j
@RunWith(SpringRunner.class)
@ActiveProfiles("uat")
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QiyuesuoTest {

    @Resource
    private QiyuesuoService qiyuesuoService;

    @Test
    public void test() {
        SealListRequest request = new SealListRequest();
        request.setName("浙江浙商融资租赁有限公司");
        request.setSealCategoryName("公章");
        request.setSealAttribute("ELECTRONIC");
        try {
            GetUnitSealListResponse unitSealList = qiyuesuoService.getUnitSealList(request);
            log.info("印章列表：{}", unitSealList);
        } catch (IOException e) {
            log.error("获取印章列表失败", e);
            throw MithrasException.newException("获取印章列表失败, " + e.getMessage());
        }
    }
    @Test
    public void testDetailContract() throws IOException {
        ContractDetailRequest contractDetailRequest = new ContractDetailRequest();
        contractDetailRequest.setContractId(3299974859787260385L);
        ContractDetailResponse contractDetail = qiyuesuoService.contractDetail(contractDetailRequest);
        log.info("合同详情：{}", contractDetail);
    }

    @Test
    public void testUploadLocalFile() throws IOException {
        UploadLocalFileRequest v2Request = new UploadLocalFileRequest();
        File file = new File("/Users/bigbear/Downloads/contract.pdf");
        v2Request.setFile(new MockMultipartFile("file", Files.newInputStream(file.toPath())));
        v2Request.setFileType(UploadLocalFileRequest.FileTypeEnum.PDF);
        v2Request.setTitle("测试合同");
        try {
            UploadLocalFileResponse silentSealSignV2Response = qiyuesuoService.uploadLocalFile(v2Request);
            log.info("印章签署结果：{}", silentSealSignV2Response);
        } catch (IOException e) {
            log.error("印章签署失败", e);
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateContract() throws IOException {
        ContractCreatebycategoryRequest createContractRequest = JSONObject.parseObject("{\n" +
                "    \"subject\": \"关键字自动签署合同\",\n" +
                "    \"categoryId\": \"2688727307646734733\",\n" +
                "    \"send\": \"true\",\n" +
                "    \"tenantName\": \"浙江省交通投资集团有限公司\",\n" +
                "    \"documents\": [\n" +
                "        \"3301814255104741497\"\n" +
                "    ],\n" +
                "    \"creatorName\": \"朱金宇\",\n" +
                "    \"creatorContact\": \"18604421036\",\n" +
                "    \"signatories\": [\n" +
                "        {\n" +
                "            \"tenantType\": \"CORPORATE\",\n" +
                "            \"tenantName\": \"浙江省交通投资集团有限公司\",\n" +
                "            \"serialNo\": 1,\n" +
                "            \"actions\": [\n" +
                "                {\n" +
                "                    \"type\": \"CORPORATE\",\n" +
                "                    \"name\": \"关键字自动签署合同\",\n" +
                "                    \"serialNo\": \"1\",\n" +
                "                    \"sealId\": 3269943050277081099,\n" +
                "                    \"locations\": [\n" +
                "                        {\n" +
                "                            \"documentId\": \"3301814255104741497\",\n" +
                "                            \"rectType\": \"SEAL_CORPORATE\",\n" +
                "                            \"page\": 0,\n" +
                "                            \"keyword\": \"甲方签字122331\",\n" +
                "                            \"keywordIndex\": 0,\n" +
                "                            \"actionName\": \"企业签章1\"\n" +
                "                        }\n" +
                "                    ]\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}", ContractCreatebycategoryRequest.class);
        List<Signatory> signatories = createContractRequest.getSignatories();
        signatories.forEach(signatory -> {
            signatory.getActions().forEach(action -> {
                action.setAutoSign(true);
            });
        });
        try {
            CreateContractResponse createContract = qiyuesuoService.createContract(createContractRequest);
            log.info("创建合同结果：{}", createContract);
        } catch (IOException e) {
            log.error("创建合同失败", e);
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSilentSealSignV2() throws IOException {
        V2ContractSignbycompanyRequest v2ContractSignbycompanyRequest = new V2ContractSignbycompanyRequest();
        qiyuesuoService.silentSealSignV2(v2ContractSignbycompanyRequest);
    }

    @Test
    public void testDownloadContract() throws IOException {
        DocumentDownloadRequest downloadContractRequest = new DocumentDownloadRequest();
        downloadContractRequest.setDocumentId(3299603137863013325L);
        DownloadContractResponse response = qiyuesuoService.downloadContract(downloadContractRequest);
        MultipartFile file = response.getFile();
        String path = "/Users/bigbear/Downloads/download.pdf";
        file.transferTo(new File(path));
    }


    @Resource
    private OssClient ossClient;
    @Resource
    private FileService fileService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private ContractTextSignInfoService contractTextSignInfoService;
    @Test
    public void testTransferFile() throws Exception {
        MaterialsList material = materialsListService.getById(241152);
        InputStream inputStream = ossClient.downLoad(material.getOssFilename());
        ByteArrayOutputStream outputStream = WatermarkUtil.doc2Pdf(inputStream);
        InputStream inputStream1 = new ByteArrayInputStream(outputStream.toByteArray());
        // 将文件重新上传到OSS
        FileOutputStream outputStream1 = new FileOutputStream("/Users/bigbear/Downloads/4752.pdf");
        // 读取输入流的内容，并写入输出流
        int bytesRead;
        byte[] buffer = new byte[1024];
        while ((bytesRead = inputStream1.read(buffer)) != -1) {
            outputStream1.write(buffer, 0, bytesRead);
        }

        // 关闭输入流和输出流
        inputStream.close();
        outputStream.close();
    }

    public static void main(String[] args) throws Exception {
        FileInputStream fileInputStream = new FileInputStream("/Users/bigbear/Downloads/1-4.实际租金及租前息支付表.docx");
//        int numberOfPages = PDDocument.load(ByteStreams.toByteArray(fileInputStream)).getNumberOfPages();
//        System.out.println(numberOfPages);
//
//        boolean containsKeyword = containsKeyword(ByteStreams.toByteArray(fileInputStream), "%《浙商租赁合同章》%");
//        System.out.println(containsKeyword);
        ByteArrayOutputStream outputStream = WatermarkUtil.doc2Pdf(fileInputStream);
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        FileOutputStream outputStream1 = new FileOutputStream("/Users/bigbear/Downloads/accept1.pdf");
        // 读取输入流的内容，并写入输出流
        int bytesRead;
        byte[] buffer = new byte[2048];
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream1.write(buffer, 0, bytesRead);
        }

        // 关闭输入流和输出流
        inputStream.close();
        outputStream.close();
    }

    @Test
    public void legalPersonTest() throws Exception {
        // 开始静默签署
        try {
            ContractTextSignInfo textSignInfo = contractTextSignInfoService.getById(1);
            V2ContractSignbylegalpersonRequest legalPersonRequest = contractTextSignInfoService.getV2ContractSignbylegalpersonRequest(textSignInfo);
            qiyuesuoService.legalPersonSealSignV2(legalPersonRequest);
        } catch (IOException e) {
            log.error("网签静默签署失败", e);
            throw new RuntimeException(e);
        }
    }

    @Test
    public void transferTest() throws Exception{
        SpringUtil.getBean(ContractTextManageService.class).transformAllFile(1634L, 6L);
        // SpringUtil.getBean(ContractTextManageService.class).transformAllFile(4753L, 4L);
    }
}
