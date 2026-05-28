package cn.zswltech.mithras.service.gendoc.render;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.contract.ContractExtraFileTypeEnum;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import com.deepoove.poi.XWPFTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.join;
import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author luyujie
 * @date 2025/11/22
 * @description 合同结清-所有权转移证书
 */
@Component
@Slf4j
public class ContractSettleOwnerChangeRender extends AbstractContractRender<ContractBaseInfo> {
    @Resource
    private OssClient ossClient;
    @Resource
    protected MaterialsListService materialsListService;
    @Override
    public String render(OutputStream outputStream, ContractBaseInfo contractBaseInfo) throws Exception {
        Map<String, Object> renderMap = new HashMap<>(8);
        renderMap.put("contractCode", contractBaseInfo.getContractCode());

        // 承租人
        List<ContractTenantry> contractTenantryList = this.listContractTenantry(contractBaseInfo.getId());
        Assert.notEmpty(contractTenantryList, () -> MithrasException.newException("承租人为空"));
        List<String> nameList = contractTenantryList.stream().map(ContractTenantry::getLesseeName).collect(Collectors.toList());
        renderMap.put("tenantryNameListText", StrUtil.join("、", nameList));

        renderMap.put("year", "{{year}}");
        renderMap.put("month", "{{month}}");
        renderMap.put("day", "{{day}}");
        // 渲染文档
        InputStream inputStream = getBean(FileTemplateService.class).getTemplate("租后结清", "所有权转移证书.docx");
//        IOUtils.copy(inputStream, outputStream);
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return ContractExtraFileTypeEnum.CONTRACT_SETTLE_OWN.display() + GlobalConstants.OFFICE_WORD_SUFFIX;
    }


    public String render2(OutputStream outputStream, ContractBaseInfo contractBaseInfo, MaterialsList materialsList) throws Exception {
        log.info("合同结清生成最终的所有权转移证书........");
        Map<String, Object> renderMap = new HashMap<>(8);
        renderMap.put("year", LocalDate.now().getYear());
        renderMap.put("month", LocalDate.now().getMonthValue());
        renderMap.put("day", LocalDate.now().getDayOfMonth());

        renderMap.put("contractCode", contractBaseInfo.getContractCode());

        // 承租人
        List<ContractTenantry> contractTenantryList = this.listContractTenantry(contractBaseInfo.getId());
        Assert.notEmpty(contractTenantryList, () -> MithrasException.newException("承租人为空"));
        List<String> nameList = contractTenantryList.stream().map(ContractTenantry::getLesseeName).collect(Collectors.toList());
        renderMap.put("tenantryNameListText", StrUtil.join("、", nameList));
        // 渲染文档
        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteArrayInputStream is = null;
        try {
            //新文件填充列表页面编辑的文件，存量文件继续从模板重新取--20260206版本
            if(materialsList.getCreateTime().isAfter(LocalDateTimeUtil.parseDate("2026-02-07", "yyyy-MM-dd").atStartOfDay())){
                inputStream = ossClient.downLoad(join("/", materialsList.getOssFilename()));
            }else{
                inputStream = getBean(FileTemplateService.class).getTemplate("租后结清", "所有权转移证书.docx");
            }
            XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
            template.write(byteArrayOutputStream);
            //新文件填充后更新所有权转移证书页面的文件
            is = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            ossClient.upLoad(is, join("/", materialsList.getOssFilename()), false);
            template.writeAndClose(outputStream);
        } catch (Exception e) {
            //log.error("生成所有权转移证书发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("所有权转移证书填充发生未知异常："+e);
        } finally {
            if (Objects.nonNull(inputStream)) {
                inputStream.close();
            }
            if (Objects.nonNull(byteArrayOutputStream)) {
                byteArrayOutputStream.close();
            }
            if (Objects.nonNull(is)) {
                is.close();
            }
        }
        return ContractExtraFileTypeEnum.CONTRACT_SETTLE_OWN.display() + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    @Override
    protected Set<Long> signClientIds(ContractBaseInfo contractBaseInfo) {
        // 设置签约方信息-浙江浙商融资租赁有限公司
        Set<Long> id = new HashSet<>();
        id.add(0L);
        return id;
    }

    @Override
    protected boolean needSignByMyself() {
        return false;
    }

    @Override
    protected boolean customShowFile(ContractBaseInfo contractBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-直租合同", "1-9.所有权转移证书.docx");
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-直租合同", "1-9.所有权转移证书.docx");
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
