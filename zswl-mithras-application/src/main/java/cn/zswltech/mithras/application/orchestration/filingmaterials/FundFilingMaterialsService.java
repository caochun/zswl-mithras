package cn.zswltech.mithras.application.orchestration.filingmaterials;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.filingmaterials.*;
import cn.zswltech.mithras.foundation.cache.RedisDistLock;
import cn.zswltech.mithras.filingmaterials.constant.FilingMaterialsConstants;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.workflow.flow.convert.FlowProcessConvert;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.CacheEnum;
import cn.zswltech.mithras.workflow.enums.CommonProcessPrepareStatus;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.filingmaterials.enums.*;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.document.mapper.MaterialsListMapper;
import cn.zswltech.mithras.filingmaterials.mapper.FilingMaterialsMapper;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.filingmaterials.model.FilingMaterials;
import cn.zswltech.mithras.fund.model.FundOrganization;
import cn.zswltech.mithras.fund.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.workflow.persistence.model.CommonProcessPrepare;
import cn.zswltech.mithras.workflow.persistence.mapper.CommonProcessPrepareMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.fund.application.organization.FundOrganizationService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.testng.collections.Maps;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.CONCURRENT_OPERATION;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * 资金端资料归档
 * @author lllin
 * @date 2026-01-07
 */
@Slf4j
@Service("FundFilingMaterialsService")
public class FundFilingMaterialsService extends AbstractFilingMaterialsService<FilingMaterialsMapper, FilingMaterials> {
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private CommonProcessPrepareMapper commonProcessPrepareMapper;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private OssClient ossClient;
    @Resource
    private FlowProcessConvert flowProcessConvert;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private MaterialsListMapper materialsListMapper;



    @Transactional(rollbackFor = Exception.class)
    @Override
    public String startProcess(FilingBaseREQ filingBaseREQ) {
        // 防止重复发起流程
        String lockKey = CacheEnum.EFFECT_SUBMIT_LOCK.buildKey(ProcessModelTypeEnum.FundFilingMaterialsApplyFlow.name(), filingBaseREQ.getId());
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            FilingMaterials filingMaterials = this.getById(filingBaseREQ.getId());
            Assert.notNull(filingMaterials, () -> MithrasException.newException("记录不存在"));
            if (!Objects.equals(FilingMaterialsProcessStatusEnum.UN_SUBMIT.name(), filingMaterials.getApproveStatus())) {
                throw new MithrasException("流程已提交审批，不可重复提交");
            }
            /*初始化流程发起对象*/
            StartProcessReq startProcessReq = new StartProcessReq();
            startProcessReq.setModelKey(ProcessModelTypeEnum.FundFilingMaterialsApplyFlow.name());
            startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                    .map(AccountVO::getId)
                    .map(String::valueOf)
                    .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
            startProcessReq.setBusinessKey(String.valueOf(filingBaseREQ.getId()));
            startProcessReq.setProcessInstanceName(String.format("%s-%s", filingMaterials.getProjCode(),FilingMaterialsFilingTypeEnum.of(filingMaterials.getFilingType()).display));
            Long currentUserId = AccountUtil.getLoginInfo().getId();
            List<OrgDO> allOrg = SpringUtil.getBean(SysUserService.class).listAllDept();
            Optional<OrgDO> optional = allOrg.stream().filter(e -> Objects.equals(e.getCode(), "ZJGLB")).findFirst();
            /*发起人、发起部门*/
            startProcessReq.setStartUserId(String.valueOf(currentUserId));
            if (optional.isPresent()) {
                startProcessReq.setStartUserDeptId(String.valueOf(optional.get().getId()));
            }
            Map<String, Object> varMap = new HashMap<>(1);
            varMap.put("manager", ListUtil.toList(String.valueOf(currentUserId)));
            startProcessReq.setVariables(varMap);
            filingMaterials.setApproveStatus(FilingMaterialsProcessStatusEnum.UNDER_APPROVAL.name());
            this.updateById(filingMaterials);
            String processInstanceId = processApiService.start(startProcessReq);
            /*更新申请表*/
            filingMaterials = this.getById(filingBaseREQ.getId());
            filingMaterials.setFlowId(processInstanceId);
            filingMaterials.setFirstCommitDate(LocalDateTime.now());
            filingMaterials.setReturnDate(null);
            this.updateById(filingMaterials);
            return processInstanceId;
        } finally {
            redisDistLock.unlock(lockKey);
        }
    }

    /**
     *资金经理提交校验是否已上传附件
     */
    public void checkFile(FilingMaterials filingMaterials) {
        List<FilingMaterialsConfigDTO> filingMaterialsConfigDTOS = queryRequireFilingMaterialsConfig(filingMaterials.getFilingType());
        if(CollUtil.isEmpty(filingMaterialsConfigDTOS)){
            return;
        }
        List<String> dirCodeList = filingMaterialsConfigDTOS.stream().map(FilingMaterialsConfigDTO::getDirCode).distinct().collect(toList());
        List<MaterialsList> fileList = materialsListService.list(
                Wrappers.<MaterialsList>lambdaQuery()
                        .in(MaterialsList::getMaterialsType, dirCodeList)
                        .eq(MaterialsList::getBusinessType, BusinessModuleEnum.FUND_FILING.name())
                        .eq(MaterialsList::getBelongId, filingMaterials.getId()));
        List<String> materialsTypeList = fileList.stream().map(MaterialsList::getMaterialsType).distinct().collect(Collectors.toList());
        String dirName;
        if (CollUtil.isEmpty(materialsTypeList)) {
            dirName = filingMaterialsConfigDTOS.stream().map(FilingMaterialsConfigDTO::getDirName).collect(Collectors.joining("、"));
            throw new MithrasException("请上传【" + dirName + "】");
        }
        if(materialsTypeList.size() != dirCodeList.size()){
            dirName = filingMaterialsConfigDTOS.stream().filter(e -> !materialsTypeList.contains(e.getDirCode())).map(FilingMaterialsConfigDTO::getDirName).collect(Collectors.joining("、"));
            throw new MithrasException("请上传【" + dirName + "】");
        }
    }

    /**
     * 初始化资金归档待办信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void initFundCommonProcessPrepare(Long id, Long currentAssignee,String financingCode,String filingType, String initiationMethod) {
        /*1、初始化待办信息*/
        FilingMaterials filingMaterials = FilingMaterials.initFilingMaterials(null, null, financingCode,
                filingType, initiationMethod, null,id,FilingMaterialsConstants.OBJECT_TYPE_FUND);
        List<OrgDO> orgList = sysUserService.getSpecificUserDeptList(currentAssignee);
        if (CollectionUtil.isEmpty(orgList)) {
            log.error(String.format("当前用户:%s没有所属部门,生成待办失败！",currentAssignee));
            return;
        }
        Long deptId = orgList.stream().filter(e -> Objects.equals(e.getCode(),"ZJGLB")).findFirst().map(OrgDO::getId).orElse(null);
        if (Objects.isNull(deptId)) {
            filingMaterials.setDeptId(orgList.get(0).getId());
        } else {
            filingMaterials.setDeptId(deptId);
        }
        filingMaterials.setUserId(currentAssignee);
        this.save(filingMaterials);
        //2、资料拷贝
        this.copyFile(id,filingType,filingMaterials.getId(),BusinessModuleEnum.FUND_FILING.name());
        /*3、生成流程待办*/
        CommonProcessPrepare prepare = CommonProcessPrepare.builder()
                .processType(ProcessModelTypeEnum.FundFilingMaterialsApplyFlow.name())
                .businessId(String.valueOf(filingMaterials.getId()))
                .formName(String.format("%s-%s", financingCode, Objects.requireNonNull(FilingMaterialsFilingTypeEnum.of(filingType)).display))
                .currentAssignee(JSONUtil.toJsonStr(Collections.singletonList(currentAssignee)))
                .currentNode("资金经理")
                .applyTime(LocalDateTime.now())
                .status(CommonProcessPrepareStatus.PEND_COMMIT.name())
                .build();
        commonProcessPrepareMapper.insert(prepare);
    }


    /**
     * 审批结束
     * @param id
     * @param endType
     * @param processInstanceId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void processEnd(Long id, Integer endType, String processInstanceId) {
        FilingMaterials filingMaterials = this.getById(id);
        /*更新审批状态*/
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        // 更新流程状态
        FilingMaterialsProcessStatusEnum processState;
        if (processPass) {
            processState = FilingMaterialsProcessStatusEnum.APPROVAL_PASS;
        } else {
            processState = ProcessBusinessStatusEnum.CANCEL.getType().equals(endType) ? FilingMaterialsProcessStatusEnum.CANCEL : FilingMaterialsProcessStatusEnum.APPROVAL_REJECT;
        }
        filingMaterials.setApproveStatus(processState.name());
        filingMaterials.setApproveDate(LocalDateTime.now());
        this.updateById(filingMaterials);
        String objectTypeBusinessType = filingMaterials.getFilingType();
        /*归档资料更新回去*/
        List<MaterialsList> fileList = materialsListService.list(
                Wrappers.<MaterialsList>lambdaQuery()
                        .eq(MaterialsList::getBusinessType, BusinessModuleEnum.FUND_FILING.name())
                        .eq(MaterialsList::getBelongId, filingMaterials.getId()));
        //删除全量
        materialsListService.remove(
                Wrappers.<MaterialsList>lambdaQuery()
                        .eq(MaterialsList::getBusinessType, objectTypeBusinessType)
                        .eq(MaterialsList::getBelongId, filingMaterials.getObjectId()));
        fileList.stream().forEach(e ->{
            e.setId(null);
            e.setBelongId(filingMaterials.getObjectId());
            e.setBusinessType(objectTypeBusinessType);
        });
        materialsListService.saveBatch(fileList);
    }

    @Override
    public Map<String, List<SelectRSP>> getOperationsDirDict(Long id) {
        return super.getOperationsDirDict(id);
    }


    public List<FilingMaterialsConfigDTO> queryRequireFilingMaterialsConfig(String filingType){
        return baseMapper.queryRequireFilingMaterialsConfig(filingType);
    }

    public String getOverrideFileName(FilingMaterials filingMaterials){
        String fileName;
        if (Objects.equals(FilingMaterialsFilingTypeEnum.FUND_DIRECT_FINANCING.name(), filingMaterials.getFilingType())) {
            //直融：融资编号-产品名称
            FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = getBean(FundDirectFinancingBaseInfoService.class).getById(filingMaterials.getObjectId());
            Assert.notNull(fundDirectFinancingBaseInfo, () -> MithrasException.newException("查询资金直融信息失败"));
            fileName = fundDirectFinancingBaseInfo.getFinancingCode() + "-" + fundDirectFinancingBaseInfo.getProductName();
        } else {
            //间融：融资编号-机构简称
            FundFinancingBaseInfo fundFinancingBaseInfo = getBean(FundFinancingBaseInfoService.class).getById(filingMaterials.getObjectId());
            Assert.notNull(fundFinancingBaseInfo, () -> MithrasException.newException("查询资金间融信息失败"));
            List<FundOrganization> organizationList = getBean(FundOrganizationService.class).getByFinancingId(fundFinancingBaseInfo.getId());
            String abbreviation = organizationList.stream().map(FundOrganization::getAbbreviation).collect(Collectors.joining("、"));
            fileName = fundFinancingBaseInfo.getFinancingCode() + "-" + abbreviation;
        }
        return fileName;
    }

    /**
     * 获取归档资料信息id与重写后文件名称的映射
     *
     * @param filingMaterialsList 归档资料信息列表
     * @return Map<Long, String> key=filing_materials的id，value=重写后文件名
     */
    public Map<Long, String> getOverrideFileName(List<FilingMaterials> filingMaterialsList) {
        return filingMaterialsList.stream().collect(toMap(FilingMaterials::getId, this::getOverrideFileName));
    }

    public Map<String, String> getDirMap(String filingType){
        Map<String, String> map = new HashMap<>();
        List<FilingMaterialsConfigDTO> filingMaterialsConfigDTOS = this.getFilingMaterialsConfigDTO(filingType);
        if(CollUtil.isNotEmpty(filingMaterialsConfigDTOS)){
            map = filingMaterialsConfigDTOS.stream().collect(Collectors.toMap(FilingMaterialsConfigDTO::getDirCode, FilingMaterialsConfigDTO::getDirName));
        }
        return map;
    }

    public Map<String, String> getDirMap(Collection<String> filingTypes){
        Map<String, String> map = Maps.newHashMap();
        for (String fileType : filingTypes) {
            map.putAll(getDirMap(fileType));
        }
        return map;
    }

}
