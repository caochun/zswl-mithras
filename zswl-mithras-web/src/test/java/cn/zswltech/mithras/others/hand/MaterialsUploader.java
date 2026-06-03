package cn.zswltech.mithras.others.hand;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.MaterialsType;
import cn.zswltech.mithras.service.enums.projestablish.ProjEstablishMaterialsEnum;
import cn.zswltech.mithras.service.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.web.MithrasApplication;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.zswltech.mithras.others.hand.projRv.ProjRv;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;
import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static java.util.Objects.isNull;

/**
 * @author yibin
 */
@Slf4j
@ActiveProfiles("pre")
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MaterialsUploader {

    @Resource
    private ProjReviewBaseInfoService rvService;
    @Resource
    private ProjEstablishBaseInfoService etbService;
    @Resource
    private ClientService clientService;
    @Resource
    private MaterialsListService materialsListService;


    @Test
    public void uploadEtb() {
        log.info("开始上传立项资料");
        Path filePath = Paths.get(System.getProperty("user.home"), "DeskTop", "cn/zswltech/mithras/others/hand", "etb");
        File dir = filePath.toFile();
        File[] files = dir.listFiles();
        List<String> notFoundRv = new ArrayList<>();
        List<String> errorList = new ArrayList<>();
        List<Long> notClientList = new ArrayList<>();
        for (File etbDir : files) {
            try {
                List<File> allFiles = listAll(etbDir);
                String etbCode = etbDir.getName();

                ProjEstablishBaseInfo etbBaseInfo = etbService.getOne(Wrappers.<ProjEstablishBaseInfo>lambdaQuery().eq(ProjEstablishBaseInfo::getProjCode, etbCode));
                if (isNull(etbBaseInfo)) {
                    notFoundRv.add(etbCode);
                    continue;
                }
                log.info("导入立项：{}", etbCode);
                File[] subDirs = etbDir.listFiles();
                File zlqdDir = Arrays.stream(subDirs).filter(f -> f.getName().equals("资料清单")).findFirst().orElse(null);
                File fjDir = Arrays.stream(subDirs).filter(f -> f.getName().equals("附件")).findFirst().orElse(null);
                File[] zlqgSubDirs = zlqdDir == null ? new File[]{} : zlqdDir.listFiles();
                File[] fjSubDirs = fjDir == null ? new File[]{} : fjDir.listFiles();
                //
                File lxspb = Arrays.stream(zlqgSubDirs).parallel().filter(f -> f.getName().equals("立项审批单")).findFirst().orElse(null);
                if (!isNull(lxspb)) {
                    List<File> subFiles = listAll(lxspb);
                    for (File subFile : subFiles) {
                        try (InputStream in = new FileInputStream(subFile)) {
                            materialsListService.addIfNotExist(
                                    new MockMultipartFile(subFile.getName(), subFile.getName(), ContentType.APPLICATION_OCTET_STREAM.toString(), in),
                                    etbBaseInfo.getId(),
                                    ProjEstablishMaterialsEnum.REPORT.name(),
                                    BusinessModuleEnum.PROJ_ESTABLISH.name()
                            );
                        }
                        allFiles.remove(subFile);
                    }
                }
                File ywsqs = Arrays.stream(zlqgSubDirs).parallel().filter(f -> f.getName().equals("租赁业务申请书")).findFirst().orElse(null);
                if (!isNull(ywsqs)) {
                    List<File> subFiles = listAll(ywsqs);
                    for (File subFile : subFiles) {
                        try (InputStream in = new FileInputStream(subFile)) {
                            materialsListService.addIfNotExist(
                                    new MockMultipartFile(subFile.getName(), subFile.getName(), ContentType.APPLICATION_OCTET_STREAM.toString(), in),
                                    etbBaseInfo.getId(),
                                    ProjEstablishMaterialsEnum.PROJ_INFORMATION.name(),
                                    BusinessModuleEnum.PROJ_ESTABLISH.name()
                            );
                        }
                        allFiles.remove(subFile);
                    }
                }

                //上传主承租人信息
                Long clientId = etbBaseInfo.getClientId();
                Client client = clientService.getById(clientId);
                if (isNull(client)) {
                    notClientList.add(clientId);
                    continue;
                }

                //基础信息
                //取 对应的 附件名称 所在列中的 文件"
                List<String> inList = StrUtil.split("营业执照副本，法人代表/实际控制人身份证，公司章程及章程修正案，企业简介、主要股东和管理层简历", "，");
                List<File> jcxxFileList = Arrays.stream(zlqgSubDirs).parallel().filter(f -> inList.contains(f.getName())).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(jcxxFileList)) {
                    //上传主承租人基础信息
                    List<File> subFiles = new ArrayList<>();
                    jcxxFileList.forEach(e -> {
                        subFiles.addAll(listAll(e));
                    });
                    for (File subFile : subFiles) {
                        try (InputStream in = new FileInputStream(subFile)) {
                            materialsListService.addIfNotExist(
                                    new MockMultipartFile(subFile.getName(), subFile.getName(), ContentType.APPLICATION_OCTET_STREAM.toString(), in),
                                    clientId,
                                    MaterialsType.BASIC_INFORMATION.name(),
                                    BusinessModuleEnum.CLIENT.name()
                            );
                        }
                        allFiles.remove(subFile);
                    }
                }
                //财务资料
                //取 对应的 附件名称 所在列中的 文件"
                List<String> cwInList = StrUtil.split("近三年财务报表或近三年审计报告（含附注说明）复印件及最近一期的资产负债表及利润表，最近一个会计年度及最近一期财务报表科目余额表，成立不足三年的,提供自成立至授信申请日的年度财务报表或审计报告（含附注说明）和最近一期的资产负债表及利润表", "，");
                List<File> cwzlFileList = Arrays.stream(zlqgSubDirs).parallel().filter(f -> cwInList.contains(f.getName())).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(cwzlFileList)) {
                    List<File> subFiles = new ArrayList<>();
                    cwzlFileList.forEach(e -> {
                        subFiles.addAll(listAll(e));
                    });
                    for (File subFile : subFiles) {
                        try (InputStream in = new FileInputStream(subFile)) {
                            materialsListService.addIfNotExist(
                                    new MockMultipartFile(subFile.getName(), subFile.getName(), ContentType.APPLICATION_OCTET_STREAM.toString(), in),
                                    clientId,
                                    MaterialsType.FINANCIAL_INFORMATION.name(),
                                    BusinessModuleEnum.CLIENT.name()
                            );
                        }
                        allFiles.remove(subFile);
                    }
                }

                if (!allFiles.isEmpty()) {
                    //其他
                    for (File subFile : allFiles) {
                        try (InputStream in = new FileInputStream(subFile)) {
                            materialsListService.addIfNotExist(
                                    new MockMultipartFile(subFile.getName(), subFile.getName(), ContentType.APPLICATION_OCTET_STREAM.toString(), in),
                                    etbBaseInfo.getId(),
                                    ProjReviewMaterialsEnum.OTHER.name(),
                                    BusinessModuleEnum.PROJ_ESTABLISH.name()
                            );
                        }
                    }
                }


            } catch (Exception e) {
                log.error("FAILED. {}", etbDir.getName());
                errorList.add(etbDir.getName());
            }
        }
        System.out.println("error list:" + JSONUtil.toJsonStr(errorList));
        System.out.println("not found list:" + JSONUtil.toJsonStr(notFoundRv));
        System.out.println("not client list:" + JSONUtil.toJsonStr(notClientList));
    }


    @Test
    public void uploadRv() {
        log.info("开始上传评审资料");
        List<ProjRv> rvList = new ArrayList<>();
        Path filePath = Paths.get(System.getProperty("user.home"), "DeskTop", "评审.xls");
        ExcelReader reader = new ExcelReader(filePath.toFile(), 0);
        reader.setIgnoreEmptyRow(true);
        for (int i = 1; i < reader.getRowCount(); i++) {
            List<Object> cellValues = reader.readRow(i);
            ProjRv projRv = JSONUtil.toBean(cellValues.get(cellValues.size() - 1).toString(), ProjRv.class);
            rvList.add(projRv);
        }

        Map<String, String> codeMap = new HashMap<>();
        for (ProjRv projRv : rvList) {
            String rvCode = projRv.get列表信息().get项目编号();
            String etbCode = projRv.get基本信息().get立项编号();
            codeMap.put(rvCode, etbCode);
        }


        filePath = Paths.get(System.getProperty("user.home"), "DeskTop", "cn/zswltech/mithras/others/hand", "rv");
        File dir = filePath.toFile();
        File[] files = dir.listFiles();
        List<String> notFoundRv = new ArrayList<>();
        List<String> errorList = new ArrayList<>();
        List<Long> notClientList = new ArrayList<>();
        for (File rvDir : files) {
            try {
                List<File> allFiles = listAll(rvDir);
                String rvCode = rvDir.getName();
                String etbCode = codeMap.get(rvCode);
                if (isBlank(etbCode)) {
                    notFoundRv.add(rvCode);
                    continue;
                }
                ProjReviewBaseInfo rvBaseInfo = rvService.getOne(Wrappers.<ProjReviewBaseInfo>lambdaQuery().eq(ProjReviewBaseInfo::getProjCode, etbCode));
                if (isNull(rvBaseInfo)) {
                    notFoundRv.add(rvCode);
                    continue;
                }
                log.info("导入评审：{}", rvCode);
                File[] subDirs = rvDir.listFiles();
                File zlqdDir = Arrays.stream(subDirs).filter(f -> f.getName().equals("资料清单")).findFirst().orElse(null);
                File fjDir = Arrays.stream(subDirs).filter(f -> f.getName().equals("附件")).findFirst().orElse(null);
                File[] zlqgSubDirs = zlqdDir == null ? new File[]{} : zlqdDir.listFiles();
                File[] fjSubDirs = fjDir == null ? new File[]{} : fjDir.listFiles();

                File xmjdbg = Arrays.stream(zlqgSubDirs).parallel().filter(f -> f.getName().equals("项目尽调报告")).findFirst().orElse(null);
                if (!isNull(xmjdbg)) {
                    //上传项目尽调报告
                    List<File> subFiles = listAll(xmjdbg);
                    for (File subFile : subFiles) {
                        try (InputStream in = new FileInputStream(subFile)) {
                            materialsListService.addIfNotExist(
                                    new MockMultipartFile(subFile.getName(), subFile.getName(), ContentType.APPLICATION_OCTET_STREAM.toString(), in),
                                    rvBaseInfo.getId(),
                                    ProjReviewMaterialsEnum.DUE_DILIGENCE_REPORT.name(),
                                    BusinessModuleEnum.PROJ_REVIEW.name()
                            );
                        }
                        allFiles.remove(subFile);
                    }
                }
                File xmscbg = Arrays.stream(zlqgSubDirs).parallel().filter(f -> f.getName().equals("项目审查报告")).findFirst().orElse(null);
                if (!isNull(xmscbg)) {
                    //上传项目审查报告
                    List<File> subFiles = listAll(xmscbg);
                    for (File subFile : subFiles) {
                        try (InputStream in = new FileInputStream(subFile)) {
                            materialsListService.addIfNotExist(
                                    new MockMultipartFile(subFile.getName(), subFile.getName(), ContentType.APPLICATION_OCTET_STREAM.toString(), in),
                                    rvBaseInfo.getId(),
                                    ProjReviewMaterialsEnum.RISK_REVIEW_REPORT.name(),
                                    BusinessModuleEnum.PROJ_REVIEW.name()
                            );
                        }
                        allFiles.remove(subFile);
                    }
                }

                File flhgscyjb = Arrays.stream(zlqgSubDirs).parallel().filter(f -> f.getName().equals("法律合规审查意见表")).findFirst().orElse(null);
                if (!isNull(flhgscyjb)) {
                    List<File> subFiles = listAll(flhgscyjb);
                    for (File subFile : subFiles) {
                        try (InputStream in = new FileInputStream(subFile)) {
                            materialsListService.addIfNotExist(
                                    new MockMultipartFile(subFile.getName(), subFile.getName(), ContentType.APPLICATION_OCTET_STREAM.toString(), in),
                                    rvBaseInfo.getId(),
                                    ProjReviewMaterialsEnum.LEGAL_COMPLIANCE_REPORT.name(),
                                    BusinessModuleEnum.PROJ_REVIEW.name()
                            );
                        }
                        allFiles.remove(subFile);
                    }
                }
                List<File> pshhyjyList = Arrays.stream(fjSubDirs).parallel().filter(f -> f.getName().equals("评审会记录") || f.getName().equals("评审会纪要")).collect(Collectors.toList());
                if (isNotEmpty(pshhyjyList)) {
                    List<File> subFiles = new ArrayList<>();
                    pshhyjyList.forEach(e -> {
                        subFiles.addAll(listAll(e));
                    });
                    for (File subFile : subFiles) {
                        try (InputStream in = new FileInputStream(subFile)) {
                            materialsListService.addIfNotExist(
                                    new MockMultipartFile(subFile.getName(), subFile.getName(), ContentType.APPLICATION_OCTET_STREAM.toString(), in),
                                    rvBaseInfo.getId(),
                                    ProjReviewMaterialsEnum.MEETING_REVIEW_REPORT.name(),
                                    BusinessModuleEnum.PROJ_REVIEW.name()
                            );
                        }
                        allFiles.remove(subFile);
                    }
                }
                //上传主承租人信息
                Long clientId = rvBaseInfo.getClientId();
                Client client = clientService.getById(clientId);
                if (isNull(client)) {
                    notClientList.add(clientId);
                    continue;
                }

                //基础信息
                //取 对应的 附件名称 所在列中的 文件"
                List<String> inList = StrUtil.split("营业执照副本，法人代表/实际控制人身份证，公司章程及章程修正案，企业简介、主要股东和管理层简历", "，");
                List<File> jcxxFileList = Arrays.stream(zlqgSubDirs).parallel().filter(f -> inList.contains(f.getName())).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(jcxxFileList)) {
                    //上传主承租人基础信息
                    List<File> subFiles = new ArrayList<>();
                    jcxxFileList.forEach(e -> {
                        subFiles.addAll(listAll(e));
                    });
                    for (File subFile : subFiles) {
                        try (InputStream in = new FileInputStream(subFile)) {
                            materialsListService.addIfNotExist(
                                    new MockMultipartFile(subFile.getName(), subFile.getName(), ContentType.APPLICATION_OCTET_STREAM.toString(), in),
                                    clientId,
                                    MaterialsType.BASIC_INFORMATION.name(),
                                    BusinessModuleEnum.CLIENT.name()
                            );
                        }
                        allFiles.remove(subFile);
                    }
                }
                //财务资料
                //取 对应的 附件名称 所在列中的 文件"
                List<String> cwInList = StrUtil.split("近三年财务报表或近三年审计报告（含附注说明）复印件及最近一期的资产负债表及利润表，最近一个会计年度及最近一期财务报表科目余额表，成立不足三年的,提供自成立至授信申请日的年度财务报表或审计报告（含附注说明）和最近一期的资产负债表及利润表", "，");
                List<File> cwzlFileList = Arrays.stream(zlqgSubDirs).parallel().filter(f -> cwInList.contains(f.getName())).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(cwzlFileList)) {
                    List<File> subFiles = new ArrayList<>();
                    cwzlFileList.forEach(e -> {
                        subFiles.addAll(listAll(e));
                    });
                    for (File subFile : subFiles) {
                        try (InputStream in = new FileInputStream(subFile)) {
                            materialsListService.addIfNotExist(
                                    new MockMultipartFile(subFile.getName(), subFile.getName(), ContentType.APPLICATION_OCTET_STREAM.toString(), in),
                                    clientId,
                                    MaterialsType.FINANCIAL_INFORMATION.name(),
                                    BusinessModuleEnum.CLIENT.name()
                            );
                        }
                        allFiles.remove(subFile);
                    }
                }

                if (!allFiles.isEmpty()) {
                    //其他
                    for (File subFile : allFiles) {
                        try (InputStream in = new FileInputStream(subFile)) {
                            materialsListService.addIfNotExist(
                                    new MockMultipartFile(subFile.getName(), subFile.getName(), ContentType.APPLICATION_OCTET_STREAM.toString(), in),
                                    rvBaseInfo.getId(),
                                    ProjReviewMaterialsEnum.OTHER.name(),
                                    BusinessModuleEnum.PROJ_REVIEW.name()
                            );
                        }
                    }
                }

            } catch (Exception e) {
                log.error("FAILED. {}", rvDir.getName());
                errorList.add(rvDir.getName());
            }
        }
        System.out.println("error list:" + JSONUtil.toJsonStr(errorList));
        System.out.println("not found list:" + JSONUtil.toJsonStr(notFoundRv));
        System.out.println("not client list:" + JSONUtil.toJsonStr(notClientList));
    }

    private static List<File> listAll(File dir) {
        List<File> allFile = new ArrayList<>();
        //根目录，前面不用打印空格
        File[] files = dir.listFiles();//将dir下的文件夹封装成一个一个的file对象
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                //说明还是目录，接着向下遍历
                allFile.addAll(listAll(files[i]));//自己调用自己，为递归
            } else {
                allFile.add(files[i]);
            }
        }
        return allFile;
    }
}
