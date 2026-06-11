package cn.zswltech.mithras.application.orchestration.projectprocess.projestablish;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Pair;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.service.impl.FlowAddSignRecordService;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.projestablish.report.*;
import cn.zswltech.mithras.foundation.util.CommonFileSortComparator;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.projectprocess.enums.projestablish.ProjEstablishMaterialsEnum;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.ProjEstablishReportRender;
import cn.zswltech.mithras.document.mapper.MaterialsListMapper;
import cn.zswltech.mithras.document.mapper.model.MaterialsList;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.application.projestablish.ProjEstablishUpdateAdvice;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.workflow.flow.util.FlowUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 立项报告文件
 *
 * @author wangchuanhao
 * @date 2022/7/22 10:50 AM
 */
@Service
public class ProjEstablishReportService implements ProjEstablishUpdateAdvice {

    private static final String MATERIALS_TYPE = "REPORT";
    private static final String TMP_BUSINESS_TYPE = "TMP";

    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private MaterialsListMapper materialsListMapper;
    @Resource
    private ProjEstablishReportRender projEstablishReportRender;
    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ProjEstablishService projEstablishService;
    @Resource
    private FlowAddSignRecordService addSignRecordService;

    @Transactional(rollbackFor = Exception.class)
    public void upload(MultipartFile file, ProjEstablishReportUploadREQ req) {
        ProjEstablishBaseInfo projEstablishBaseInfo = projEstablishBaseInfoMapper.selectById(req.getProjEstablishId());
        authCheck(projEstablishBaseInfo);
        materialsListService.add(file, req.getProjEstablishId(), req.getMaterialsType(), BusinessModuleEnum.PROJ_ESTABLISH.name());
        recordStatus(req.getProjEstablishId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void remove(ProjEstablishReportRemoveREQ req) {
        MaterialsList materialsList = materialsListService.getById(req.getId());
        if (Objects.isNull(materialsList)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        ProjEstablishBaseInfo projEstablishBaseInfo = projEstablishBaseInfoMapper.selectById(materialsList.getBelongId());
        authCheck(projEstablishBaseInfo);
        materialsListService.remove(Collections.singletonList(req.getId()));
        recordStatus(projEstablishBaseInfo.getId());
    }

    public FileListRSP download(Long recordId) {
        return materialsListService.download(recordId);
    }

    public List<Pair<String, List<ProjEstablishReportListRSP>>> list(ProjEstablishReportListREQ req) {
        Page<MaterialsList> page = materialsListMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<MaterialsList>lambdaQuery()
                        .in(MaterialsList::getMaterialsType,new ArrayList<>(ProjEstablishMaterialsEnum.listAll()))
                        .eq(MaterialsList::getBusinessType, BusinessModuleEnum.PROJ_ESTABLISH.name())
                        .eq(MaterialsList::getBelongId, req.getProjEstablishId())
                        .orderByDesc(MaterialsList::getCreateTime)
        );
        List<Long> creatorIdList = page.getRecords().stream().map(MaterialsList::getCreateBy).distinct().collect(Collectors.toList());
        Map<Long, String> creatorNameMap = id2NameService.sysUserId2Name(creatorIdList);
        List<ProjEstablishReportListRSP> rspList = page.getRecords().stream().map(m -> {
            ProjEstablishReportListRSP rsp = new ProjEstablishReportListRSP();
            rsp.setId(m.getId());
            rsp.setMaterialsType(m.getMaterialsType());
            rsp.setTypeName(Optional.ofNullable(m.getMaterialsType()).map(ProjEstablishMaterialsEnum::getByName).map(pem -> pem.getDisplay()).orElse(""));
            rsp.setSort(Optional.ofNullable(m.getMaterialsType()).map(ProjEstablishMaterialsEnum::getByName).map(pem -> pem.getSort()).orElse(null));
            rsp.setFileName(m.getFilename());
            rsp.setCreateTimestamp(Optional.ofNullable(m.getCreateTime()).map(LocalDateTimeUtil::toEpochMilli).orElse(0L));
            rsp.setCreateTime(Optional.ofNullable(m.getCreateTime()).map(LocalDateTimeUtil::formatNormal).orElse(""));
            rsp.setCreator(creatorNameMap.get(m.getCreateBy()));
            return rsp;
        }).collect(Collectors.toList());
        rspList.sort(new CommonFileSortComparator());

        List<List<ProjEstablishReportListRSP>> groupRspList = rspList.stream()
                .collect(Collectors.groupingBy(ProjEstablishReportListRSP::getMaterialsType))
                .values().stream()
                .sorted(Comparator.comparing(rs -> Optional.ofNullable(ProjEstablishMaterialsEnum.getByName(rs.get(0).getMaterialsType())).map(e -> e.getSort()).orElse(Integer.MAX_VALUE)))
                .collect(Collectors.toList());
        List<Pair<String, List<ProjEstablishReportListRSP>>> resList = new ArrayList<>();
        for (List<ProjEstablishReportListRSP> groupRsp : groupRspList) {
            resList.add(new Pair<>(groupRsp.get(0).getMaterialsType(), groupRsp));
        }
        return resList;
    }

    public void generate(OutputStream outputStream, ProjEstablishReportGenerateREQ req) throws IOException {
//        ClassPathResource classPathResource = new ClassPathResource("doc/立项报告模版.docx");
//        byte[] bs = IoUtil.readBytes(classPathResource.getInputStream());
//        outputStream.write(bs);
        projEstablishReportRender.render(outputStream, req.getProjEstablishId());
    }

    @Transactional(rollbackFor = Exception.class)
    public Long generateOnline(ProjEstablishReportGenerateREQ req) {
        ProjEstablishBaseInfo projEstablishBaseInfo = projEstablishBaseInfoMapper.selectById(req.getProjEstablishId());
        if (Objects.isNull(projEstablishBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (RecordStatus.CLOSED.name().equals(projEstablishBaseInfo.getProjEstablishStatus()) || RecordStatus.EXPIRE.name().equals(projEstablishBaseInfo.getProjEstablishStatus())) {
            throw new MithrasException("该立项已关闭/失效，不允许再生成报告");
        }
        if (!ProjectBizType.ZL.name().equals(projEstablishBaseInfo.getBizType())
                && !ProjectBizType.ZZ.name().equals(projEstablishBaseInfo.getBizType())) {
            throw new MithrasException("暂只支持租赁/转租赁生成立项报告！");
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        String fileName = projEstablishReportRender.render(os, req.getProjEstablishId());
        recordStatus(req.getProjEstablishId());
        return materialsListService.add(IoUtil.toStream(os.toByteArray()), fileName,
                req.getProjEstablishId(), MATERIALS_TYPE, TMP_BUSINESS_TYPE);
    }

    private void authCheck(ProjEstablishBaseInfo establishBaseInfo) {
        if (Objects.isNull(establishBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!AccountUtil.getLoginInfo().getId().equals(establishBaseInfo.getProjSponsorUserId())) {
            throw new AuthCheckException("非数据主办，不支持该种操作");
        }
        if (RecordStatus.CLOSED.name().equals(establishBaseInfo.getProjEstablishStatus()) || RecordStatus.EXPIRE.name().equals(establishBaseInfo.getProjEstablishStatus())) {
            throw new MithrasException("该立项已关闭，不允许再修改有关信息");
        }
        // 判断是否在流程中 且 是否在发起人节点
        ProcessResp processResp = projEstablishService.findRelatedProcess(establishBaseInfo.getId());
        if (Objects.nonNull(processResp)) {
            boolean isStartUserNode = FlowUtil.isStartUserNode(processResp);
            if (!isStartUserNode) {
                String[] ids = processResp.getCurTaskIds().split(",");
                boolean ok = false;
                for (String id : ids) {
                    boolean collaborateFlag = Objects.nonNull(addSignRecordService.findByTaskId(id));
                    if (collaborateFlag) {
                        ok = collaborateFlag;
                        break;
                    }
                }
                if (!ok) {
                    throw new AuthCheckException("该数据处于流程中，且流程不在发起人节点，不允许修改数据");
                }
            }
        }
    }
}
