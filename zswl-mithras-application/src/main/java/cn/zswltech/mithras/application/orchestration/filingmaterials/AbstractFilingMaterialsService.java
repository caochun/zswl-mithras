package cn.zswltech.mithras.application.orchestration.filingmaterials;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.ProcessHistoryReq;
import cn.zswltech.flow.core.domain.resp.ProcessHistoryResp;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.biz.service.SystemConfigService;
import cn.zswltech.gruul.dao.dal.entity.SystemConfigDO;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.filingmaterials.FilingBaseREQ;
import cn.zswltech.mithras.dto.filingmaterials.FilingBasicRemoveREQ;
import cn.zswltech.mithras.dto.filingmaterials.FilingMaterialsConfigDTO;
import cn.zswltech.mithras.dto.flow.search.ProcessHistoryRSP;
import cn.zswltech.mithras.filingmaterials.constant.FilingMaterialsConstants;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.workflow.flow.convert.FlowProcessConvert;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.filingmaterials.enums.FilingMaterialsProcessStatusEnum;
import cn.zswltech.mithras.document.mapper.MaterialsListMapper;
import cn.zswltech.mithras.filingmaterials.mapper.FilingMaterialsMapper;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.filingmaterials.model.FilingMaterials;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.join;
import static java.util.stream.Collectors.toList;

/**
 *
 *@author: lllin
 *@CreateTime: 2026-01-09
 */
@Service
@Slf4j
public abstract class AbstractFilingMaterialsService<M extends BaseMapper<T>, T extends FilingMaterials> extends ServiceImpl<FilingMaterialsMapper, FilingMaterials> {
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private MaterialsListMapper materialsListMapper;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private OssClient ossClient;
    @Resource
    private FlowProcessConvert flowProcessConvert;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private SystemConfigService systemConfigService;

    /**
     * 待办->发起流程
     * @param filingBaseREQ
     * @return
     */
    public abstract String startProcess(FilingBaseREQ filingBaseREQ);

    /**
     * 流程结束
     * @param id
     * @param endType
     * @param processInstanceId
     */
    public abstract void processEnd(Long id, Integer endType, String processInstanceId);

    /**
     * 待办->关闭流程
     * @param filingBaseREQ
     * @return
     */
    public void close(FilingBaseREQ filingBaseREQ) {
        FilingMaterials filingMaterials = this.getById(filingBaseREQ.getId());
        Assert.notNull(filingMaterials, () -> MithrasException.newException("记录不存在"));
        filingMaterials.setApproveStatus(FilingMaterialsProcessStatusEnum.CANCEL.name());
        this.updateById(filingMaterials);
    }

    /**
     * 重复文件名替换
     *
     * @param clientMaterials
     */
    public void repeatFileNameReplace(List<MaterialsList> clientMaterials) {
        Map<String, List<MaterialsList>> groupMaterials = clientMaterials.stream().collect(Collectors.groupingBy(MaterialsList::getFilename));
        Map<String, Integer> fileNameCountMap = new HashMap<>();
        for (MaterialsList materials : clientMaterials) {
            String filename = materials.getFilename();
            List<MaterialsList> materialsLists = groupMaterials.get(filename);
            if (materialsLists.isEmpty() || materialsLists.size() <= 1) {
                continue;
            }
            try {
                int repeatCount = fileNameCountMap.getOrDefault(filename, 0) + 1;
                fileNameCountMap.put(filename, repeatCount);

                int lastDotIndex = filename.lastIndexOf(".");
                String replaceName;
                if (lastDotIndex == -1) {
                    replaceName = filename + "(" + repeatCount + ")";
                } else {
                    String prefix = filename.substring(0, lastDotIndex);
                    String suffix = filename.substring(lastDotIndex);
                    replaceName = prefix + "(" + repeatCount + ")" + suffix;
                }
                materials.setFilename(replaceName);
                log.info("文件名:{}重复，重命名为:{}", filename, replaceName);

            } catch (Exception e){
                log.error("文件名重复处理失败,文件名:{},失败原因:{}", filename,e.getMessage());
            }
        }
    }

    public void copyFile(Long oldBelongId, String oldBusinessType, Long targetId, String businessType) {
        List<MaterialsList> fileList = materialsListService.list(
                Wrappers.<MaterialsList>lambdaQuery()
                        .eq(MaterialsList::getBusinessType, oldBusinessType)
                        .eq(MaterialsList::getBelongId, oldBelongId));
        List<MaterialsList> targetList = new LinkedList<>();
        fileList.forEach(item -> {
            MaterialsList newMaterial = new MaterialsList();
            newMaterial.setBelongId(targetId);
            newMaterial.setBusinessType(businessType);
            newMaterial.setMaterialsType(item.getMaterialsType());
            newMaterial.setMaterialSubType(item.getMaterialSubType());
            newMaterial.setOssFilename(item.getOssFilename());
            newMaterial.setSuffix(item.getSuffix());
            newMaterial.setFilename(item.getFilename());
            newMaterial.setFilePath(item.getFilePath());
            newMaterial.setSystemGenerate(item.getSystemGenerate());
            newMaterial.setSourceBusinessKey(item.getSourceBusinessKey());
            newMaterial.setCreateBy(item.getCreateBy());
            newMaterial.setCreateTime(item.getCreateTime());
            newMaterial.setUpdateBy(item.getUpdateBy());
            newMaterial.setUpdateTime(item.getUpdateTime());
            targetList.add(newMaterial);
        });
        if (CollUtil.isNotEmpty(targetList)) {
            materialsListService.saveBatch(targetList);
        }
    }

    public void fileRemove(FilingBasicRemoveREQ filingBasicRemoveREQ) {
        LambdaQueryWrapper<MaterialsList> query = Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBelongId, filingBasicRemoveREQ.getMainId())
                .eq(MaterialsList::getMaterialsType, FilingMaterialsConstants.BASIC_INFORMATION)
                .eq(MaterialsList::getBusinessType, filingBasicRemoveREQ.getModuleType())
                .ne(MaterialsList::getId,filingBasicRemoveREQ.getFileId());
        if(Objects.nonNull(filingBasicRemoveREQ.getSourceBusinessKey())){
            query.eq(MaterialsList::getSourceBusinessKey,filingBasicRemoveREQ.getSourceBusinessKey());
        }
        List<MaterialsList> materialsListList = materialsListMapper.selectList(query);
        if(CollUtil.isEmpty(materialsListList)){
            throw new MithrasException("该目录下必须存在一条附件！");
        }
        materialsListService.remove(filingBasicRemoveREQ.getFileId());
    }

    public Map<String, List<SelectRSP>> getOperationsDirDict(Long id) {
        FilingMaterials filingMaterials = this.getById(id);
        if (filingMaterials == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        return getOperationsDirDictByFilingType(filingMaterials.getFilingType());
    }

    public Map<String, List<SelectRSP>> getOperationsDirDictByFilingType(String filingType) {
        List<SelectRSP> selectRSPList;
        Map<String, List<SelectRSP>> map = new HashMap<>();
        /*查询表中配置好的全量一级目录*/
        List<FilingMaterialsConfigDTO> filingMaterialsConfigDTOS = getFilingMaterialsConfigDTO(filingType);
        /*通过businessType分组*/
        Map<String, List<FilingMaterialsConfigDTO>> groupByBusiness = filingMaterialsConfigDTOS.stream().collect(Collectors.groupingBy(FilingMaterialsConfigDTO::getBusinessType));
        /*根据流程key分组*/
        for (Map.Entry<String, List<FilingMaterialsConfigDTO>> entry : groupByBusiness.entrySet()) {
            selectRSPList = new ArrayList<>();
            /*一级目录下根据定义好的sortcode排序*/
            List<FilingMaterialsConfigDTO> materialsConfigDTOS = entry.getValue().stream().sorted(Comparator.comparing(FilingMaterialsConfigDTO::getSortCode)).collect(Collectors.toList());
            for (FilingMaterialsConfigDTO filingMaterialsConfigDTO : materialsConfigDTOS) {
                String conditionKey = filingMaterialsConfigDTO.getConditionKey();
                /*该目录是否需拼接流程审批号条件字段*/
                if (CharSequenceUtil.isEmpty(conditionKey)) {
                    selectRSPList.add(new SelectRSP(filingMaterialsConfigDTO.getDirName(), filingMaterialsConfigDTO.getDirCode(), 1));
                }
            }
            map.put(entry.getKey(), selectRSPList);
        }
        return map;
    }



    public List<FilingMaterialsConfigDTO> getFilingMaterialsConfigDTOById(Long id){
        FilingMaterials filingMaterials = this.getById(id);
        return getFilingMaterialsConfigDTO(filingMaterials.getFilingType());
    }

    public List<FilingMaterialsConfigDTO> getFilingMaterialsConfigDTO(String filingType) {
        return baseMapper.queryFilingMaterialsConfig(filingType);
    }

    public Map<String, String> getDirMap(String filingType){
        Map<String, String> map = new HashMap<>();
        List<FilingMaterialsConfigDTO> filingMaterialsConfigDTOS = this.getFilingMaterialsConfigDTO(filingType);
        if(CollUtil.isNotEmpty(filingMaterialsConfigDTOS)){
            map = filingMaterialsConfigDTOS.stream().collect(Collectors.toMap(FilingMaterialsConfigDTO::getDirCode, FilingMaterialsConfigDTO::getDirName));
        }
        return map;
    }


    /**
     * 获取流程实例对应的审批历史
     * @param processInstanceId 流程实例
     * @return
     */
    public List<ProcessHistoryRSP> getProcessHis(String processInstanceId) {
        ProcessHistoryReq flowReq = new ProcessHistoryReq();
        flowReq.setPageIndex(1);
        flowReq.setPageSize(999);
        flowReq.setProcessInstanceId(processInstanceId);
        Page<ProcessHistoryResp> historyRespPage = processApiService.history(flowReq);
        List<ProcessHistoryRSP> rspList = historyRespPage.getContents().stream()
                .map(flowProcessConvert::flowHistoryResp2RSP).collect(Collectors.toList());
        flowProcessConvert.historyRSPFillName(rspList);
        return rspList;
    }

    public void replaceReviewName(Long id, String processInstanceId, String activityId,List<String> businessTypeAll) {
        /*获取当前流程下所有待填充的基础资料清单*/
        List<MaterialsList> materialsListList = materialsListService.list(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBelongId, id)
                .eq(MaterialsList::getMaterialsType, FilingMaterialsConstants.BASIC_INFORMATION)
                .eq(MaterialsList::getSystemGenerate, YesOrNoNumberEnum.YES.getCode())
                .in(MaterialsList::getBusinessType, businessTypeAll));
        if (CollUtil.isEmpty(materialsListList)) {
            return;
        }
        if (!FilingMaterialsConstants.ACTIVITY_MAP.containsKey(activityId)
                || !FilingMaterialsConstants.TASK_ACTIVITY_LIST.containsKey(activityId)) {
            log.error("Map：ACTIVITY_MAP映射失败");
            return;
        }
        String operatorName = "";
        StringBuilder stringBuilder = new StringBuilder();

        /*获取当前流程审批节点的审批人*/
        List<ProcessHistoryRSP> processHis = this.getProcessHis(processInstanceId);
        Map<String, List<ProcessHistoryRSP>> activityMap = processHis.stream().filter(item -> Objects.nonNull(item.getTaskActivityId()))
                .collect(Collectors.groupingBy(ProcessHistoryRSP::getTaskActivityId));
        if (activityMap.containsKey(activityId)) {
            List<String> activityIdList = FilingMaterialsConstants.TASK_ACTIVITY_LIST.get(activityId);
            if (CollUtil.isEmpty(activityIdList)) {
                log.error("Map:TASK_ACTIVITY_LIST 映射失败");
                return;
            }
            for (String taskActivity : activityIdList) {
                List<ProcessHistoryRSP> processHistoryRSPList = activityMap.get(taskActivity);
                List<ProcessHistoryRSP> collect = processHistoryRSPList.stream()
                        .filter(Objects::nonNull)
                        .sorted(Comparator.comparing(ProcessHistoryRSP::getOperateTime,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                        .collect(toList());
                stringBuilder.append(collect.get(0).getOperatorName()).append("、");
            }
        }
        operatorName = stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
        String key = FilingMaterialsConstants.ACTIVITY_MAP.get(activityId);
        for (MaterialsList materialsList : materialsListList) {
            String ossFileName = materialsList.getOssFilename();
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                /*填充经办人*/
                this.generateRender(os, ossFileName, key, operatorName);
                byte[] renderData = os.toByteArray();
                if (renderData.length == 0) {
                    log.error("资料ID：{} 渲染后数据为空，OSS文件名：{}", materialsList.getId(), ossFileName);
                    continue;
                }
                try (ByteArrayInputStream is = new ByteArrayInputStream(renderData)) {
                    String ossPath = join("/", ossFileName);
                    ossClient.upLoad(is, ossPath, false);
                    log.info("资料ID：{} 重新上传OSS成功，文件名：{}", materialsList.getId(), ossFileName);
                }
            } catch (IOException e) {
                log.error("资料重新上传OSS失败，资料ID：{}，OSS文件名：{}", materialsList.getId(), ossFileName, e);
            } catch (Exception e) {
                log.error("归档资料重新填充资料清单失败，资料ID：{}，OSS文件名：{}", materialsList.getId(), ossFileName, e);
            }
        }

    }

    /**
     * 填充资料清单
     *
     * @param outputStream 输出流
     * @param ossFilename 文件路径
     * @param key 填充对象
     * @param name 填充值
     * @throws Exception
     */
    public void generateRender(OutputStream outputStream, String ossFilename, String key, String name) throws Exception {
        String ossPath = join("/", ossFilename);
        try (InputStream inputStream = ossClient.downLoad(ossPath);
             XWPFDocument document = new XWPFDocument(inputStream)) {
            generateRunByKey(document, key, name);
            document.write(outputStream);
            outputStream.flush();
        }
    }

    public void generateRunByKey(XWPFDocument document,String key,String name){
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            String paraText = paragraph.getText();
            if (!paraText.contains(key)) {
                continue;
            }
            List<XWPFRun> runs = paragraph.getRuns();
            if (runs.isEmpty()) {
                continue;
            }
            StringBuilder fullText = new StringBuilder();

            for (XWPFRun run : runs) {
                String text = run.getText(0);
                if (text != null) {
                    fullText.append(text);
                }
            }
            String allText = fullText.toString().replace("null", "");
            String actualName = Objects.isNull(name) ? "" : name;
            if (Objects.equals(key, FilingMaterialsConstants.INITIAL_REVIEW)) {
                allText = replacePrefixValue(allText, key, actualName + " ", FilingMaterialsConstants.REVIEW);
            } else {
                allText = replacePrefixValue(allText, key, actualName, null);
            }
            while (!paragraph.getRuns().isEmpty()) {
                paragraph.removeRun(0);
            }
            // 新建一个Run，写入替换后的完整文本
            XWPFRun newRun = paragraph.createRun();
            newRun.setText(allText);
            newRun.setBold(true);
            newRun.setFontSize(10.5);
            newRun.setFontFamily("仿宋_GB2312");
        }
    }

    /**
     * 填充对象匹配及值替换
     * @param text
     * @param prefix
     * @param newValue
     * @param nextPrefix
     * @return
     */
    protected String replacePrefixValue(String text, String prefix, String newValue, String nextPrefix) {
        if (!text.contains(prefix)) {
            return text;
        }
        newValue = Objects.isNull(newValue) ? "" : newValue;
        /*构建正则：匹配「前缀 + 任意旧值」，直到下一个前缀/段落结尾*/
        String regex;
        if (nextPrefix != null) {
            /*匹配前缀后到下一个前缀之间的内容（不包含下一个前缀）*/
            regex = Pattern.quote(prefix) + ".*?(?=" + Pattern.quote(nextPrefix) + ")";
        } else {
            /*匹配前缀后到段落结尾的所有内容*/
            regex = Pattern.quote(prefix) + ".*$";
        }
        /*替换逻辑：前缀 + 最新值*/
        return text.replaceAll(regex, prefix + newValue);
    }

    /**
     * 档案管理复核岗
     * @return
     */
    public List<String> getReviewJobUser(){
        List<Long> userIds = sysUserService.queryJobUserIds(JobEnum.yunYingGuanLi.name());
        userIds.addAll(sysUserService.queryJobUserIds(JobEnum.yunYingGuanLiReview.name()));
        if(CollUtil.isEmpty(userIds)){
            throw new MithrasException("档案复核岗审批人获取失败");
        }
        return userIds.stream().map(String::valueOf).distinct().collect(Collectors.toList());
    }

    /**
     * 档案管理初审岗
     * @return
     */
    public List<String> getInitApproveUser(){
        /*初审岗 默认葛晓青*/
        SystemConfigDO systemConfigDO = systemConfigService.getConfig(FilingMaterialsConstants.FILING_FLOW_INIT_REVIEW_USER).getData();
        List<Long> userIdList = new ArrayList<>();
        if (Objects.isNull(systemConfigDO)) {
            log.error("项目资料归档流程档案管理（经办）审批人未配置");
        } else {
            userIdList = Optional.ofNullable(JSON.parseArray(systemConfigDO.getConfigValue(), Long.class)).orElse(new ArrayList<>());
        }
        if (userIdList.isEmpty()) {
            userIdList.add(FilingMaterialsConstants.FILE_MANAGEMENT_HANDLER);
        }
        return userIdList.stream().map(String::valueOf).distinct().collect(Collectors.toList());
    }

}
