package cn.zswltech.mithras.service.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.materialsfile.MaterialsListApi;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.file.FileDownLoadRSP;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.materialsfile.*;
import cn.zswltech.mithras.service.CommonFileSortComparator;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.convert.FileConvert;
import cn.zswltech.mithras.service.convert.materialslist.MaterialsListConverter;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.customer.domain.enums.client.ClientMaterialsDisplayEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.leaseholdproperty.domain.enums.LeaseFileTypeEnums;
import cn.zswltech.mithras.service.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.service.enums.projreview.ReviewRelationDataType;
import cn.zswltech.mithras.service.factory.file.impl.FundFinancingFileListProvider;
import cn.zswltech.mithras.service.factory.onlyoffice.impl.ContractOoBizHandlerImpl;
import cn.zswltech.mithras.service.mapper.NewestMaterialsDto;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.receiptrepay.FundReceiptRepayBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.MaterialsListLib;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.model.LeaseItemInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewMaterial;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.CorpCommerceInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.groupcreditestablish.GroupCreditEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.service.service.leaseholdproperty.LeaseItemInfoService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListLibService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.projfms.ProjProcessState;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewMaterialService;
import cn.zswltech.mithras.service.util.FileUriUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static cn.hutool.core.text.CharSequenceUtil.join;
import static cn.zswltech.mithras.dto.MaterialsListIdType.VERSIONED;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * @create: 2022-07-21
 **/

@Slf4j
@RestController
public class MaterialsListController implements MaterialsListApi {

    @Resource
    private HttpServletResponse response;

    @Resource
    private MaterialsListService materialsListService;

    @Resource
    private MaterialsListLibService materialsListLibService;

    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;

    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;

    @Resource
    private ProjPricingBaseInfoService projPricingBaseInfoService;

    @Resource
    private ClientService clientService;

    @Resource
    private ClientMapper clientMapper;

    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Resource
    private ContractOoBizHandlerImpl contractOoBizHandler;

    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;

    @Resource
    private OssClient ossClient;

    @Resource
    private GroupCreditEstablishBaseInfoService groupCreditEstablishBaseInfoService;

    @Resource
    private GroupCreditReviewBaseInfoService groupCreditReviewBaseInfoService;

    @Resource
    private FileConvert fileConvert;
    @Resource
    private FundFinancingFileListProvider fundFinancingFileListProvider;
    @Resource
    private FundReceiptRepayBaseInfoMapper fundReceiptRepayBaseInfoMapper;

    @Resource
    private LeaseItemInfoService leaseItemInfoService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ProjReviewMaterialService projReviewMaterialService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> upload(MultipartFile file, Long belongId, String materialsType, String businessType, String materialsSubType) {
        if (BusinessModuleEnum.CLIENT.name().equals(businessType)) {
            Client info = clientMapper.selectById(belongId);
            /*if (ObjectUtil.isNotEmpty(info.getBelongSponsorId()) && !ObjectUtil.equal(info.getBelongSponsorId(), AccountUtil.getLoginInfo().getId())) {
                throw new MithrasException("无权操作，请联系项目主办！");
            }*/
            info.setLatestUserId(AccountUtil.getLoginInfo().getId());
            clientMapper.updateById(info);
            SpringContextHolder.getBean(ClientService.class).checkClientOccupy(belongId);
            SpringContextHolder.getBean(CorpCommerceInfoService.class).recordClientStatusByClientId(belongId);
        }
        try {
            materialsListService.add(file.getInputStream(), file.getOriginalFilename(), belongId, materialsType, materialsSubType, businessType);
        } catch (Exception e) {
            log.error("上传文件发生异常", e);
            throw new MithrasException("上传文件发生异常");
        }
        return R.ok();
    }

    @Override
    public R<List<MaterialsListListRSP>> list(@Valid List<MaterialsListListREQ> req) {
        List<MaterialsListListRSP> materialsLists = new LinkedList<>();
        for (MaterialsListListREQ mreq : req) {
            materialsLists.addAll(materialsListService.buildRspList(mreq.getBusinessType(), mreq.getBelongIds()));
        }
        return R.ok(materialsLists);
    }

    @Override
    public R<Void> remove(@Valid MaterialsListRemoveREQ req) {
        List<MaterialsList> lists = materialsListService.getByIds(req.getIds());
        for (MaterialsList materials : lists) {
            if (!req.getBusinessType().equals(materials.getBusinessType())) {
                return R.fail("不允许删除：" + materials.getFilename());
            }
        }
        List<Long> ids = lists.stream().map(MaterialsList::getBelongId).distinct().collect(toList());
        if (BusinessModuleEnum.CLIENT.name().equals(req.getBusinessType())) {
            List<Client> clients = clientMapper.selectBatchIds(ids);
            for (Client client : clients) {
                if (ObjectUtil.isNotEmpty(client.getBelongSponsorId()) && !ObjectUtil.equal(client.getBelongSponsorId(), AccountUtil.getLoginInfo().getId())) {
                    throw new MithrasException("权限校验失败: 只有项目主办可以操作删除！");
                }
            }
        }
        materialsListService.remove(req.getIds());
        return R.ok();
    }

    @SneakyThrows
    @Override
    public R<FileDownLoadRSP> download(List<Long> ids) {
        MaterialsListDownloadREQ req = new MaterialsListDownloadREQ();
        if (CollectionUtils.isEmpty(req.getIds())) {
            req.setIds(ids);
        }
        List<MaterialsList> materials = materialsListService.getByIds(req.getIds());
        if (CollectionUtils.isEmpty(req.getIds())) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        // 下载权限校验
//        checkBizPermission(materials);
        if (materials.size() == 1) {
            return R.ok(materialsListService.download(ids.get(0)));
        }
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("资料清单.zip", "UTF-8"));
        materialsListService.download(outputStream, req.getIds());
        return null;
    }

    @SneakyThrows
    @Override
    public R<List<FileDownLoadRSP>> downloadQuery(List<Long> ids) {
        MaterialsListDownloadREQ req = new MaterialsListDownloadREQ();
        if (CollectionUtils.isEmpty(req.getIds())) {
            req.setIds(ids);
        }
        if (CollectionUtils.isEmpty(req.getIds())) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        // 下载权限校验
//        checkBizPermission(materials);
        List<FileDownLoadRSP> fileDownLoadRSPList = new ArrayList<>();
        for (Long id : ids) {
            FileDownLoadRSP download = materialsListService.download(id);
            fileDownLoadRSPList.add(download);
        }
        return R.ok(fileDownLoadRSPList);

    }

    @Override
    public R<FileDownLoadRSP> downloadCommon(@Valid MaterialsListDownloadREQ req) throws IOException {
        if (CollectionUtils.isEmpty(req.getIds())) {
            throw new MithrasException("下载项为空");
        }
        List<MaterialsList> materials = materialsListService.getByIds(req.getIds());
        if (CollectionUtils.isEmpty(req.getIds())) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (materials.size() == 1) {
            return R.ok(materialsListService.download(materials.get(0).getId()));
        }
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("资料清单.zip", "UTF-8"));
        materialsListService.download(outputStream, req.getIds());
        return null;
    }

    @Override
    public void projDownload(@Valid MaterialsListDownloadREQ req) throws IOException {
        // 不使用通用下载方法是因为立项资料批量下载需要根据客户、资料类型进行多级目录的打包压缩
        List<MaterialsList> materialsListList = materialsListService.listByIds(req.getIds());
        Assert.notEmpty(materialsListList, () -> MithrasException.newException("没有找到任何文件记录"));
        Map<Long, List<MaterialsList>> clientMaterialMap = new HashMap<>();
        StopWatch st = new StopWatch("立项资料批量下载");
        st.start("数据查询");
        for (MaterialsList materialsList : materialsListList) {
            if (!Objects.equals(materialsList.getBusinessType(), BusinessModuleEnum.CLIENT.name())) {
                // 理论上立项的资料清单都是客户资料，此处容错处理
                continue;
            }
            List<MaterialsList> list = clientMaterialMap.computeIfAbsent(materialsList.getBelongId(), v -> new LinkedList<>());
            list.add(materialsList);
        }
        // 批量查询客户信息，客户名称会作为下级目录
        List<Client> clientList = clientService.listByClientIds(clientMaterialMap.keySet());
        Map<Long, Client> clientMap = clientList.stream().collect(Collectors.toMap(Client::getId, v -> v));
        // 压缩文件
        List<String> pathList = new LinkedList<>();
        //List<InputStream> inputStreamList = new LinkedList<>();
        long timestamp = System.currentTimeMillis();
        String rootPath = "立项资料_" + timestamp;
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("资料清单.zip"));
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
        Set<String> pathSet = new LinkedHashSet<>();
        st.stop();
        st.start("文件压缩下载");
        for (Map.Entry<Long, List<MaterialsList>> entry : clientMaterialMap.entrySet()) {
            Long clientId = entry.getKey();
            List<MaterialsList> clientMaterials = entry.getValue();
            Client client = clientMap.get(clientId);
            if (Objects.isNull(client)) {
                log.warn("通过客户资料中的客户id没有找到客户信息[clientId: {}]", clientId);
                continue;
            }
            String clientPath = "/" + client.getClientName();
            for (MaterialsList material : clientMaterials) {
                String typePath;
                // 确定类型
                MaterialsType materialsType = MaterialsType.of(material.getMaterialsType());
                if (Objects.isNull(materialsType)) {
                    NormalMaterialsType normalMaterialsType = NormalMaterialsType.of(material.getMaterialsType());
                    if (Objects.isNull(normalMaterialsType)) {
                        log.warn("未知类型的客户资料[fileId: {}, materialType: {}]", material.getId(), material.getMaterialsType());
                        continue;
                    } else {
                        typePath = "/" + normalMaterialsType.display;
                    }
                } else {
                    typePath = "/" + materialsType.display;
                }
                // 拼接文件路径
                String filePath = rootPath + clientPath + typePath + "/" + material.getFilename();
                // 获取文件流
                //InputStream inputStream = ossClient.downLoad(material.getOssFilename());
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ossClient.downLoad(byteArrayOutputStream, join("/", material.getOssFilename()));
                byte[] buffer = byteArrayOutputStream.toByteArray();
                pathList.add(filePath);
                filePath = FileUriUtil.fileNameDeduplication(pathSet, filePath);
                ZipEntry zEntry = new ZipEntry(filePath);
                zipOut.putNextEntry(zEntry);
                zipOut.write(buffer);
                zipOut.closeEntry();
                zipOut.flush();
                //inputStreamList.add(inputStream);
            }
        }
        st.stop();
        log.info("MaterialsListController projDownload {}", st.prettyPrint(TimeUnit.MILLISECONDS));
        zipOut.close();
       /* String[] paths = new String[pathList.size()];
        pathList.toArray(paths);
        InputStream[] inputStreams = new InputStream[inputStreamList.size()];
        inputStreamList.toArray(inputStreams);
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("资料清单.zip"));
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            // 执行压缩后写入到输出流中
            ZipUtil.zip(response.getOutputStream(), paths, inputStreams);
        } finally {
            for (InputStream inputStream : inputStreams) {
                if (Objects.nonNull(inputStream)) {
                    inputStream.close();
                }
            }
        }*/
    }

    /**
     * 针对文件校验下载权限
     *
     * @param materials
     */
    private void checkBizPermission(List<MaterialsList> materials) {
        List<MaterialsList> contractMeterialList = materials.stream().filter(f -> BusinessModuleEnum.CONTRACT.name().equals(f.getBusinessType())).collect(toList());
        if (CollectionUtils.isNotEmpty(contractMeterialList)) {
            // 合同模块文件下载权限控制
            for (MaterialsList contractMeterials : contractMeterialList) {
                if (!contractOoBizHandler.canDownload(contractMeterials)) {
                    throw new MithrasException("只有项目经理可在流程审批通过后下载文件");
                }
            }
        }
    }

    @Override
    public R<List<ProjMaterialsListListRSP>> projList(@Valid ProjMaterialsListListREQ req) {
        ProjEstablishBaseInfo baseInfo = projEstablishBaseInfoService.getById(req.getProjId());
        if (baseInfo == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
//        List<MaterialsListListRSP> materialsListListRSPS = new LinkedList<>();
        Set<Long> clientIds = new HashSet<>();
        Map<Long, String> typeName = new HashMap<>();
        if (baseInfo.getCreditorInfo() != null) {
            List<ClientInfo> clientInfoList = JSONUtil.toList(baseInfo.getCreditorInfo(), ClientInfo.class);
            for (ClientInfo clientInfo : clientInfoList) {
                clientIds.add(clientInfo.getClientId());
                typeName.put(clientInfo.getClientId(), ClientMaterialsDisplayEnum.CREDITORCLIENTID.display);
            }
        }
        getFieldFromJson(baseInfo.getLesseeInfo(), ClientMaterialsDisplayEnum.LESSEEINFO.display, "clientId", clientIds, typeName);
        getFieldFromJson(baseInfo.getGuaranteeInfo(), ClientMaterialsDisplayEnum.GUARANTEEINFO.display, "clientId", clientIds, typeName);
        getFieldFromJson(baseInfo.getPledgorInfo(), ClientMaterialsDisplayEnum.PLEDGORINFO.display, "clientId", clientIds, typeName);
        getFieldFromJson(baseInfo.getMortgagorInfo(), ClientMaterialsDisplayEnum.MORTGAGORINFO.display, "clientId", clientIds, typeName);
        getFieldFromJson(baseInfo.getDebtorInfo(), ClientMaterialsDisplayEnum.DEBTORINFO.display, "clientId", clientIds, typeName);
//        if (CollectionUtils.isNotEmpty(clientIds)) {
////            materialsListListRSPS.addAll(materialsListService.newestMaterialsList(BusinessModuleEnum.CLIENT.name(), new ArrayList<>(clientIds)));
//            // 客户资料改查编辑区
//            materialsListListRSPS.addAll(materialsListService.listClientMaterial(new LinkedList<>(clientIds), String.format("%s@%s", BusinessModuleEnum.PROJ_ESTABLISH.name(), baseInfo.getId())));
//        }
//        List<ProjMaterialsListListRSP> sortprsp = materialsRsp2projRsp(materialsListListRSPS, typeName);
//        return R.ok(sortprsp);
        LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
        query.eq(MaterialsList::getBelongId, baseInfo.getId());
        query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.PROJ_ESTABLISH_CLIENT.name());
        query.in(MaterialsList::getSourceBusinessKey, clientIds.stream().map(Object::toString).collect(toList()));
        List<MaterialsList> materialsList = materialsListService.list(query);
        return R.ok(materialsListService.toProjMaterialsListListRSP(clientIds, materialsList, typeName, baseInfo.getId(), BusinessModuleEnum.PROJ_ESTABLISH_CLIENT.name()));
    }


    @Override
    public R<List<ProjMaterialsListListRSP>> projReviewList(@Valid ProjReviewMaterialsListREQ req) {
        ProjReviewBaseInfo reviewBaseInfo = projReviewBaseInfoService.getById(req.getProjReviewId());
        if (reviewBaseInfo == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        Set<Long> clientIds = new HashSet<>();
        Map<Long, String> typeName = new HashMap<>();
        String clientMaterialBusinessType = "UNKNOWN";
        List<MaterialsListListRSP> materialsListListRSPS = new ArrayList<>();
        if (ReviewRelationDataType.PROJ_ESTABLISH.name().equals(reviewBaseInfo.getRelationDataType())) {
            clientMaterialBusinessType = BusinessModuleEnum.PROJ_REVIEW_CLIENT.name();
            ProjEstablishBaseInfo projEstablishBaseInfo = projEstablishBaseInfoService.getById(reviewBaseInfo.getProjEstablishId());
            materialsListListRSPS.addAll(materialsListService.newestMaterialsList(BusinessModuleEnum.PROJ_ESTABLISH.name(), Collections.singletonList(projEstablishBaseInfo.getId())));
        } else if (ReviewRelationDataType.GROUP_CREDIT_REVIEW.name().equals(reviewBaseInfo.getRelationDataType())) {
            clientMaterialBusinessType = BusinessModuleEnum.PROJ_REVIEW_CLIENT.name();
        }
        List<ProjMaterialsListListRSP> sortprsp = materialsRsp2projRsp(materialsListListRSPS, typeName);
        // 补全客户资料
        if (ProjectBizType.BL.name().equals(reviewBaseInfo.getBizType()) || ProjectBizType.ZR.name().equals(reviewBaseInfo.getBizType())) {
            getFieldFromJson(reviewBaseInfo.getCreditorInfo(), ClientMaterialsDisplayEnum.CREDITORCLIENTID.display, "clientId", clientIds, typeName);
            getFieldFromJson(reviewBaseInfo.getDebtorInfo(), ClientMaterialsDisplayEnum.DEBTORINFO.display, "clientId", clientIds, typeName);
        } else {
            getFieldFromJson(reviewBaseInfo.getLesseeInfo(), ClientMaterialsDisplayEnum.LESSEEINFO.display, "clientId", clientIds, typeName);
            getFieldFromJson(reviewBaseInfo.getGuaranteeInfo(), ClientMaterialsDisplayEnum.GUARANTEEINFO.display, "clientId", clientIds, typeName);
            getFieldFromJson(reviewBaseInfo.getPledgorInfo(), ClientMaterialsDisplayEnum.PLEDGORINFO.display, "clientId", clientIds, typeName);
            getFieldFromJson(reviewBaseInfo.getMortgagorInfo(), ClientMaterialsDisplayEnum.MORTGAGORINFO.display, "clientId", clientIds, typeName);
            getFieldFromJson(reviewBaseInfo.getCreditorInfo(), ClientMaterialsDisplayEnum.CREDITORCLIENTID.display, "clientId", clientIds, typeName);
            getFieldFromJson(reviewBaseInfo.getDebtorInfo(), ClientMaterialsDisplayEnum.DEBTORINFO.display, "clientId", clientIds, typeName);
        }
        if (CollectionUtil.isNotEmpty(clientIds)) {
            LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
            query.eq(MaterialsList::getBelongId, reviewBaseInfo.getId());
            query.eq(MaterialsList::getBusinessType, clientMaterialBusinessType);
            query.in(MaterialsList::getSourceBusinessKey, clientIds.stream().map(Object::toString).collect(toList()));
            List<MaterialsList> materialsList = materialsListService.list(query);
            List<ProjMaterialsListListRSP> clientMaterialList = materialsListService.toProjMaterialsListListRSP(clientIds, materialsList, typeName, reviewBaseInfo.getId(), BusinessModuleEnum.PROJ_REVIEW_CLIENT.name());
            sortprsp.addAll(clientMaterialList);
        }
        // 若风控经理审批
        // 初次或变更涉及资料主体变化会初始化数据
        boolean isRiskManager = sysUserService.currentUserIsSpecificJob(JobEnum.riskmanager.name());
        List<ProjMaterialsListListRSP> materialsList = sortprsp.stream().filter(item -> ReviewRelationDataType.PROJ_ESTABLISH.name().equals(item.getBusinessType())).collect(toList());
        if (CollectionUtil.isNotEmpty(materialsList) && isRiskManager ) {
            projReviewMaterialService.fillReviewMaterial(reviewBaseInfo,sortprsp);
        }

        List<ProjMaterialsListListRSP> result = sortprsp.stream().sorted(Comparator.comparing(r -> ClientMaterialsDisplayEnum.ofWithDefault(r.getClientTypeName()).order)).collect(toList());
        return R.ok(result);
    }

    @Override
    public R<String> projReviewMaterialComments(ProjReviewMaterialCommentsREQ req) {
        boolean result = projReviewMaterialService.updateAnnotationIncludeNullById(BeanUtil.copyProperties(req, ProjReviewMaterial.class));
        return R.ok(result ? "success" : "fail");
    }


    @Override
    public R<List<ProjMaterialsListListRSP>> projPricingList(@Valid ProjPricingMaterialsListREQ req) {
        ProjPricingBaseInfo pricingBaseInfo = projPricingBaseInfoService.getById(req.getProjPricingId());
        if (pricingBaseInfo == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        // 定价的资料清单复用评审的
        if (Objects.isNull(pricingBaseInfo.getProjReviewId())) {
            return R.ok(Collections.emptyList());
        }
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(pricingBaseInfo.getProjReviewId());
        ProjReviewMaterialsListREQ reviewReq = new ProjReviewMaterialsListREQ();
        reviewReq.setProjReviewId(projReviewBaseInfo.getId());
        return projReviewList(reviewReq);
//        Set<Long> clientIds = new HashSet<>();
//        Map<Long, String> typeName = new HashMap<>();
//        List<MaterialsListListRSP> materialsListListRSPS = new ArrayList<>();
//        if (ReviewRelationDataType.PROJ_ESTABLISH.name().equals(pricingBaseInfo.getRelationDataType())) {
//            ProjEstablishBaseInfo projEstablishBaseInfo = projEstablishBaseInfoService.getById(pricingBaseInfo.getProjEstablishId());
//            materialsListListRSPS.addAll(materialsListService.newestMaterialsList(BusinessModuleEnum.PROJ_ESTABLISH.name(), Collections.singletonList(projEstablishBaseInfo.getId())));
//        } else if (ReviewRelationDataType.GROUP_CREDIT_REVIEW.name().equals(pricingBaseInfo.getRelationDataType())) {
//            // 集团授信发起的项目评审 不展示之前的资料
//        }
//        if (ProjectBizType.BL.name().equals(pricingBaseInfo.getBizType()) || ProjectBizType.ZR.name().equals(pricingBaseInfo.getBizType())) {
//            getFieldFromJson(pricingBaseInfo.getCreditorInfo(), ClientMaterialsDisplayEnum.CREDITORCLIENTID.display, "clientId", clientIds, typeName);
//            getFieldFromJson(pricingBaseInfo.getDebtorInfo(), ClientMaterialsDisplayEnum.DEBTORINFO.display, "clientId", clientIds, typeName);
//        } else {
//            getFieldFromJson(pricingBaseInfo.getLesseeInfo(), ClientMaterialsDisplayEnum.LESSEEINFO.display, "clientId", clientIds, typeName);
//            getFieldFromJson(pricingBaseInfo.getGuaranteeInfo(), ClientMaterialsDisplayEnum.GUARANTEEINFO.display, "clientId", clientIds, typeName);
//            getFieldFromJson(pricingBaseInfo.getPledgorInfo(), ClientMaterialsDisplayEnum.PLEDGORINFO.display, "clientId", clientIds, typeName);
//            getFieldFromJson(pricingBaseInfo.getMortgagorInfo(), ClientMaterialsDisplayEnum.MORTGAGORINFO.display, "clientId", clientIds, typeName);
//            getFieldFromJson(pricingBaseInfo.getCreditorInfo(), ClientMaterialsDisplayEnum.CREDITORCLIENTID.display, "clientId", clientIds, typeName);
//            getFieldFromJson(pricingBaseInfo.getDebtorInfo(), ClientMaterialsDisplayEnum.DEBTORINFO.display, "clientId", clientIds, typeName);
//        }
//        if (CollectionUtils.isNotEmpty(clientIds)) {
////            materialsListListRSPS.addAll(materialsListService.newestMaterialsList(BusinessModuleEnum.CLIENT.name(), new ArrayList<>(clientIds)));
//            // 客户资料改查编辑区
//            materialsListListRSPS.addAll(materialsListService.listClientMaterial(new LinkedList<>(clientIds), String.format("%s@%s", BusinessModuleEnum.PROJ_PRICING.name(), pricingBaseInfo.getId())));
//        }
//        List<ProjMaterialsListListRSP> sortprsp = materialsRsp2projRsp(materialsListListRSPS, typeName);
//        return R.ok(sortprsp);
    }


    @Resource
    private MaterialsListConverter materialsListConverter;

    @Override
    public R<List<ContractMaterialListRSP>> contractMaterialList(ContractMaterialListREQ contractMaterialListREQ) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractMaterialListREQ.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("没有找到合同信息");
        }
        NewestMaterialsDto dto = new NewestMaterialsDto();
        dto.setBusinessType(BusinessModuleEnum.PROJ_REVIEW.name());
        dto.setBelongIds(Collections.singletonList(contractBaseInfo.getProjReviewId()));
        dto.setMaterialsTypes(ProjReviewMaterialsEnum.listAll());
        List<MaterialsListLib> materialsListLibs = materialsListLibService.newestMaterials(dto);
//        List<MaterialsList> materialsListList = materialsListConverter.lib2Entity(materialsListLibs);

        if (CollectionUtils.isEmpty(materialsListLibs)) {
            return R.ok(Collections.emptyList());
        }
        Map<String, List<MaterialsListLib>> map = materialsListLibs.stream().collect(groupingBy(MaterialsListLib::getMaterialsType));
        List<ContractMaterialListRSP> result = new LinkedList<>();
        for (Map.Entry<String, List<MaterialsListLib>> entry : map.entrySet()) {
            String type = entry.getKey();
            List<MaterialsListLib> list = entry.getValue();
            ProjReviewMaterialsEnum item = ProjReviewMaterialsEnum.getByName(type);
            ContractMaterialListRSP rsp = new ContractMaterialListRSP();
            rsp.setGroupType(type);
            rsp.setGroupTypeName(Optional.ofNullable(item).map(ProjReviewMaterialsEnum::getDisplay).orElse("未知类型"));
            rsp.setSort(Optional.ofNullable(item).map(ProjReviewMaterialsEnum::getSort).orElse(0));
            List<ContractMaterialListRSP.FileData> fileDataList = list.stream()
                    .map(m -> {
                        ContractMaterialListRSP.FileData fileData = new ContractMaterialListRSP.FileData();
                        fileData.setMaterialType(m.getMaterialsType());
                        fileData.setFileId(m.getId());
                        fileData.setIdType(VERSIONED);
                        fileData.setFileName(m.getFilename());
                        fileData.setMaterialTypeName(rsp.getGroupTypeName());
                        fileData.setCreateTimestamp(Optional.ofNullable(m.getCreateTime()).map(LocalDateTimeUtil::toEpochMilli).orElse(0L));
                        return fileData;
                    }).sorted(new CommonFileSortComparator()).collect(Collectors.toList());
            rsp.setFileDataList(fileDataList);
            result.add(rsp);
        }
        // 排序
        result.sort(Comparator.comparingInt(ContractMaterialListRSP::getSort));
        return R.ok(result);
    }

    @Override
    public R<List<ContractMaterialListRSP>> contractLeaseMaterialList(ContractMaterialListREQ contractMaterialListREQ) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractMaterialListREQ.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("没有找到合同信息");
        }
        Long projReviewId = contractBaseInfo.getProjReviewId();
        List<LeaseItemInfo> leaseItemInfos = leaseItemInfoService.lambdaQuery()
                .eq(LeaseItemInfo::getProjReviewId, projReviewId)
                .eq(LeaseItemInfo::getApprovalStatus, ProcessStatus.APPROVAL_PASS.name())
                .orderByAsc(LeaseItemInfo::getId).list();
        List<ContractMaterialListRSP> result = new LinkedList<>();

        //  初始化响应信息
        ContractMaterialListRSP rspInsert = new ContractMaterialListRSP();
        rspInsert.setGroupType(LeaseFileTypeEnums.LEASE_INSERT.name());
        rspInsert.setGroupTypeName(LeaseFileTypeEnums.LEASE_INSERT.display);
        rspInsert.setSort(0);
        rspInsert.setFileDataList(new ArrayList<>());
        ContractMaterialListRSP rspUpdate = new ContractMaterialListRSP();
        rspUpdate.setGroupType(LeaseFileTypeEnums.LEASE_UPDATE.name());
        rspUpdate.setGroupTypeName(LeaseFileTypeEnums.LEASE_UPDATE.display);
        rspUpdate.setSort(1);
        rspUpdate.setFileDataList(new ArrayList<>());
        result.add(rspInsert);
        result.add(rspUpdate);

        if (Objects.isNull(leaseItemInfos)) {
            return R.ok(result);
        }
        //  添加租赁物文件
        for (int i = 0; i < leaseItemInfos.size(); i++) {
            boolean flag = i == 0;
            LeaseItemInfo leaseItemInfo = leaseItemInfos.get(i);
            Long id = leaseItemInfo.getId();
            List<MaterialsList> list = materialsListService.lambdaQuery()
                    .eq(MaterialsList::getBelongId, id)
                    .eq(MaterialsList::getBusinessType, BusinessModuleEnum.LEASE_DATA_LIST.name())
                    .eq(MaterialsList::getMaterialsType, LeaseFileTypeEnums.OPERATION_MANAGER_REVIEW_SUBMISSION.name()).list();

            //  租赁文件信息赋值
            if (!ObjectUtils.isEmpty(list)) {
                List<ContractMaterialListRSP.FileData> fileDataList = new ArrayList<>();
                for (MaterialsList materialsList : list) {
                    ContractMaterialListRSP.FileData fileData = new ContractMaterialListRSP.FileData();
                    fileData.setMaterialType(materialsList.getMaterialsType());
                    fileData.setFileId(materialsList.getId());
                    fileData.setFileName(materialsList.getFilename());
                    fileData.setMaterialTypeName(flag ? LeaseFileTypeEnums.LEASE_INSERT.display : LeaseFileTypeEnums.LEASE_UPDATE.display);
                    fileData.setCreateTimestamp(Optional.ofNullable(materialsList.getCreateTime()).map(LocalDateTimeUtil::toEpochMilli).orElse(0L));
                    fileData.setCreateByName(sysUserService.getUserName(materialsList.getCreateBy()));
                    LocalDateTime createTime = materialsList.getCreateTime() != null ? materialsList.getCreateTime() : LocalDateTime.now();
                    fileData.setCreateTime(createTime);
                    fileDataList.add(fileData);
                }
                for (ContractMaterialListRSP contractMaterialListRSP : result) {
                    if (flag && contractMaterialListRSP.getGroupType().equals(LeaseFileTypeEnums.LEASE_INSERT.name())) {
                        contractMaterialListRSP.getFileDataList().addAll(fileDataList);
                    } else if (!flag && contractMaterialListRSP.getGroupType().equals(LeaseFileTypeEnums.LEASE_UPDATE.name())) {
                        contractMaterialListRSP.getFileDataList().addAll(fileDataList);
                    }
                }
            }

        }
        return R.ok(result);
    }

    @Override
    public R<List<PaymentMaterialListRSP>> paymentMaterialList(PaymentMaterialListREQ paymentMaterialListREQ) {
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(paymentMaterialListREQ.getPaymentId());
        if (Objects.isNull(paymentBaseInfo)) {
            return R.fail(ResultMsg.RECORD_NOT_EXIST);
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("没有找到合同信息");
        }

        NewestMaterialsDto dto = new NewestMaterialsDto();
        dto.setBusinessType(BusinessModuleEnum.PROJ_REVIEW.name());
        dto.setBelongIds(Collections.singletonList(contractBaseInfo.getProjReviewId()));
        List<MaterialsListLib> materialsListLibs = materialsListLibService.newestMaterials(dto);
        if (CollectionUtils.isEmpty(materialsListLibs)) {
            return R.ok(Collections.emptyList());
        }
        Map<String, List<MaterialsListLib>> map = materialsListLibs.stream()
                .collect(groupingBy(MaterialsListLib::getMaterialsType));
        List<PaymentMaterialListRSP> result = new LinkedList<>();
        for (Map.Entry<String, List<MaterialsListLib>> entry : map.entrySet()) {
            String type = entry.getKey();
            List<MaterialsListLib> list = entry.getValue();
            ProjReviewMaterialsEnum item = ProjReviewMaterialsEnum.getByName(type);
            PaymentMaterialListRSP rsp = new PaymentMaterialListRSP();
            rsp.setGroupType(type);
            rsp.setGroupTypeName(Optional.ofNullable(item).map(ProjReviewMaterialsEnum::getDisplay).orElse("未知类型"));
            rsp.setSort(Optional.ofNullable(item).map(ProjReviewMaterialsEnum::getSort).orElse(0));
            List<PaymentMaterialListRSP.FileData> fileDataList = list.stream()
                    .map(m -> {
                        PaymentMaterialListRSP.FileData fileData = new PaymentMaterialListRSP.FileData();
                        fileData.setMaterialType(m.getMaterialsType());
                        fileData.setFileId(m.getId());
                        fileData.setIdType(VERSIONED);
                        fileData.setFileName(m.getFilename());
                        fileData.setCreateTimestamp(Optional.ofNullable(m.getCreateTime()).map(LocalDateTimeUtil::toEpochMilli).orElse(0L));
                        fileData.setMaterialTypeName(rsp.getGroupTypeName());
                        return fileData;
                    }).sorted(new CommonFileSortComparator()).collect(Collectors.toList());
            rsp.setFileDataList(fileDataList);
            result.add(rsp);
        }
        // 排序
        result.sort(Comparator.comparingInt(PaymentMaterialListRSP::getSort));
        return R.ok(result);
    }

    @Override
    public R<List<ProjMaterialsListListRSP>> groupCreditEstablishList(@Valid GroupCreditEstablishMaterialsListListREQ req) {
        GroupCreditEstablishBaseInfo baseInfo = groupCreditEstablishBaseInfoService.getById(req.getGroupCreditEstablishId());
        if (baseInfo == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
//        List<MaterialsListListRSP> materialsListListRSPS = new LinkedList<>();
        Set<Long> clientIds = new HashSet<>();
        clientIds.add(baseInfo.getClientId());
        Map<Long, String> typeName = new HashMap<>();
        typeName.put(baseInfo.getClientId(), ClientMaterialsDisplayEnum.GROUPCREDITCLIENTID.display);
//        if (CollectionUtils.isNotEmpty(clientIds)) {
//            materialsListListRSPS.addAll(materialsListService.newestMaterialsList(BusinessModuleEnum.CLIENT.name(), new ArrayList<>(clientIds)));
//        }
//        List<ProjMaterialsListListRSP> sortprsp = materialsRsp2projRsp(materialsListListRSPS, typeName);
//        return R.ok(sortprsp);
        LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
        query.eq(MaterialsList::getBelongId, baseInfo.getId());
        query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.GROUP_CREDIT_ESTABLISH_CLIENT.name());
        query.in(MaterialsList::getSourceBusinessKey, clientIds.stream().map(Object::toString).collect(toList()));
        List<MaterialsList> materialsList = materialsListService.list(query);
        return R.ok(materialsListService.toProjMaterialsListListRSP(clientIds, materialsList, typeName, baseInfo.getId(), BusinessModuleEnum.GROUP_CREDIT_ESTABLISH_CLIENT.name()));
    }

    @Override
    public R<List<ProjMaterialsListListRSP>> groupCreditReviewList(@Valid GroupCreditReviewMaterialsListListREQ req) {
        GroupCreditReviewBaseInfo reviewBaseInfo = groupCreditReviewBaseInfoService.getById(req.getGroupCreditReviewId());
        if (reviewBaseInfo == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        GroupCreditEstablishBaseInfo groupCreditEstablishBaseInfo = groupCreditEstablishBaseInfoService.getById(reviewBaseInfo.getGroupCreditEstablishId());
        List<MaterialsListListRSP> materialsListListRSPS = new LinkedList<>(materialsListService.newestMaterialsList(BusinessModuleEnum.GROUP_CREDIT_ESTABLISH.name(), Collections.singletonList(groupCreditEstablishBaseInfo.getId())));
        Set<Long> clientIds = new HashSet<>();
        clientIds.add(reviewBaseInfo.getClientId());
        Map<Long, String> typeName = new HashMap<>();
        typeName.put(reviewBaseInfo.getClientId(), ClientMaterialsDisplayEnum.GROUPCREDITCLIENTID.display);

//        if (CollectionUtils.isNotEmpty(clientIds)) {
//            materialsListListRSPS.addAll(materialsListService.newestMaterialsList(BusinessModuleEnum.CLIENT.name(), new ArrayList<>(clientIds)));
//        }
        List<ProjMaterialsListListRSP> sortprsp = materialsRsp2projRsp(materialsListListRSPS, typeName);
        // 补客户资料
        LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
        query.eq(MaterialsList::getBelongId, reviewBaseInfo.getId());
        query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.GROUP_CREDIT_REVIEW_CLIENT.name());
        query.in(MaterialsList::getSourceBusinessKey, clientIds.stream().map(Object::toString).collect(toList()));
        List<MaterialsList> materialsList = materialsListService.list(query);
        List<ProjMaterialsListListRSP> clientMaterialList = materialsListService.toProjMaterialsListListRSP(clientIds, materialsList, typeName, reviewBaseInfo.getId(), BusinessModuleEnum.GROUP_CREDIT_REVIEW_CLIENT.name());
        sortprsp.addAll(clientMaterialList);
        List<ProjMaterialsListListRSP> result = sortprsp.stream().sorted(Comparator.comparing(r -> ClientMaterialsDisplayEnum.ofWithDefault(r.getClientTypeName()).order)).collect(toList());
        return R.ok(result);
    }

    @Override
    public R<List<Pair<String, List<FileListRSP>>>> materialsFundReceiptRepay(SinglePkREQ req) {
        FundReceiptRepayBaseInfo baseInfo = fundReceiptRepayBaseInfoMapper.selectById(req.getId());
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FileListREQ fileListREQ = new FileListREQ();
        fileListREQ.setMainId(baseInfo.getFinancingId());
        fileListREQ.setModuleType(BusinessModuleEnum.FUND_FINANCING.name());
        return R.ok(fundFinancingFileListProvider.listGroup(fileListREQ));
    }


    @Override
    public void previewPdf(Long id, Integer idType, String version) {
        MaterialsList materials;
        if (ObjectUtil.equal(VERSIONED, idType)) {
            materials = fileConvert.actualLib2Entity(materialsListLibService.getById(id));
        } else if (StringUtils.isBlank(version)) {
            materials = materialsListService.getById(id);
        } else {
            materials = fileConvert.actualLib2Entity(materialsListLibService.getBaseMapper().selectOne(Wrappers.<MaterialsListLib>lambdaQuery()
                    .eq(MaterialsListLib::getVersion, version)
                    .eq(MaterialsListLib::getOriginId, id)
                    .last("LIMIT 1")
            ));
        }
        if (materials != null) {
            FileTypeEnum fileTypeEnum = FileTypeEnum.getByExName(materials.getSuffix());
            if (fileTypeEnum == FileTypeEnum.PDF) {
                try {
                    String downloadFileName = URLEncoder.encode(materials.getFilename(), "UTF-8");
                    response.setHeader("Content-Type", "application/pdf");
                    response.setHeader("Content-Disposition", "inline;filename=" + downloadFileName);
                    ossClient.downLoad(response.getOutputStream(), materials.getOssFilename());
                } catch (Exception e) {
                    log.error("生成pdf预览流失败.", e);
                }
            }
        }
    }

    @Override
    public R<MaterialsPreviewRSP> preview(@Valid MaterialsListPreviewREQ req) {
        MaterialsList materials;
        if (ObjectUtil.equal(VERSIONED, req.getIdType())) {
            materials = fileConvert.actualLib2Entity(materialsListLibService.getById(req.getId()));
        } else if (StringUtils.isBlank(req.getVersion())) {
            materials = materialsListService.getById(req.getId());
        } else {
            materials = fileConvert.actualLib2Entity(materialsListLibService.getBaseMapper().selectOne(Wrappers.<MaterialsListLib>lambdaQuery()
                    .eq(MaterialsListLib::getVersion, req.getVersion())
                    .eq(MaterialsListLib::getOriginId, req.getId())
                    .last("LIMIT 1")
            ));
        }
        if (materials == null) {
            return R.fail("文件不存在！");
        }
        MaterialsPreviewRSP rsp = new MaterialsPreviewRSP();
        FileTypeEnum fileTypeEnum = FileTypeEnum.getByExName(materials.getSuffix());
        if (fileTypeEnum == FileTypeEnum.UNKNOWN) {
            ImageTypeEnum imageTypeEnum = ImageTypeEnum.getByExName(materials.getSuffix());
            if (imageTypeEnum == ImageTypeEnum.UNKNOWN) {
                VideoTypeEnum videoTypeEnum = VideoTypeEnum.getByExName(materials.getSuffix());
                if (videoTypeEnum == VideoTypeEnum.UNKNOWN) {
                    rsp.setPreviewType(PreviewTypeEnum.UNKNOWN.name());
                } else {
                    rsp.setPreviewType(PreviewTypeEnum.VIDEO.name());
                    String url = materialsListService.getPreviewUrl(materials.getOssFilename(), 60 * 60 * 24);
                    rsp.setUrl(url);
                }
            } else {
                rsp.setPreviewType(PreviewTypeEnum.IMAGE.name());
                String url = materialsListService.getPreviewUrl(materials.getOssFilename(), 60 * 60 * 24);
//                rsp.setUrl(url.replace("http://172.16.200.7:9001",domainUrl));
                rsp.setUrl(url);
            }
        } else {
            rsp.setPreviewType(PreviewTypeEnum.ONLYOFFICE.name());
        }

        return R.ok(rsp);
    }

    @Deprecated
    private List<ProjMaterialsListListRSP> materialsRsp2projRsp(List<MaterialsListListRSP> materialsListListRSPS, Map<Long, String> typeName) {
        List<ProjMaterialsListListRSP> prsp = new LinkedList<>();
        materialsListListRSPS.forEach(o -> {
            ProjMaterialsListListRSP projMaterials = new ProjMaterialsListListRSP();
            BeanUtil.copyProperties(o, projMaterials);
            prsp.add(projMaterials);
        });
        for (ProjMaterialsListListRSP materials : prsp) {
            if (BusinessModuleEnum.CLIENT.name().equals(materials.getBusinessType())) {
                Client client = clientService.getById(materials.getId());
                materials.setName(client.getClientName());
                materials.setClientType(client.getClientType());
                materials.setClientTypeName(typeName.get(materials.getId()));
            }
        }
        return prsp.stream()
                .sorted(Comparator.comparing(r -> ClientMaterialsDisplayEnum.ofWithDefault(r.getClientTypeName()).order))
                .collect(toList());
    }

    private void getFieldFromJson(String jsonArray, String fieldName, String
            field, Set<Long> set, Map<Long, String> typeName) {
        if (Strings.isNotEmpty(jsonArray)) {
            JSONArray json = JSONUtil.parseArray(jsonArray);
            for (int i = 0; i < json.size(); i++) {
                Object clientId = json.getJSONObject(i).get(field);
                if (clientId != null) {
                    Long l = Long.parseLong(String.valueOf(clientId));
                    if (set.contains(l)) {
                        typeName.put(l, typeName.get(l) + "、" + fieldName);
                    } else {
                        typeName.put(l, fieldName);
                    }
                    set.add(l);
                }
            }
        }
    }
}
