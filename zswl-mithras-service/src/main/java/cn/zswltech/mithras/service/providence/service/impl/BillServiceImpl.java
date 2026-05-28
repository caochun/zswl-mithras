package cn.zswltech.mithras.service.providence.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.blackgray.dto.req.CompleteWarehouseREQ;
import cn.zswltech.mithras.blackgray.enums.BlackGrayOrgEnum;
import cn.zswltech.mithras.blackgray.enums.BlackGraySourceEnum;
import cn.zswltech.mithras.blackgray.enums.BlackGrayTypeEnum;
import cn.zswltech.mithras.blackgray.service.BlackGrayLibraryService;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.providence.entity.BillOverdue;
import cn.zswltech.mithras.service.providence.enums.OrgTypeEnum;
import cn.zswltech.mithras.service.providence.mapper.BillOverdueMapper;
import cn.zswltech.mithras.service.providence.req.BillOverdueReq;
import cn.zswltech.mithras.service.providence.rsp.BillOverdueRsp;
import cn.zswltech.mithras.service.providence.service.BillService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.zswltec.providence.dto.base.PageR;
import com.zswltec.providence.dto.base.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jim
 * @version 1.0.0
 * @descripition:
 * @date 2024/12/24 14:21
 */
@Service
@Slf4j
public class BillServiceImpl implements BillService {

    @Resource
    private BillOverdueMapper billOverdueMapper;

    @Resource
    private BlackGrayLibraryService blackGrayLibraryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R overdueListImport(MultipartFile multipartFile, String busiDate) {
        List<BillOverdue> billOverdueList = parsePdf(multipartFile,busiDate);
        SpringUtil.getBean(BillService.class).refreshData(billOverdueList, busiDate);
        return R.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshData(List<BillOverdue> billOverdueList, String busiDate) {
        billOverdueMapper.delete(new LambdaQueryWrapper<BillOverdue>().eq(BillOverdue::getBusiDate,busiDate));
        List<List<BillOverdue>> partition = Lists.partition(billOverdueList, 200);
        for (List<BillOverdue> billOverdues : partition) {
            billOverdueMapper.batchSave(billOverdues);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = null;
        try {
            parse = sdf.parse(busiDate);
        } catch (ParseException e) {
            log.error("overdueListImport busiDate error {}", busiDate);
            throw new MithrasException("时间节点异常");
        }
        // 刷新黑灰名单
        // TODO
        Date finalParse = parse;
        List<CompleteWarehouseREQ> warehouseReq = billOverdueList.stream().map(billOverdue -> {
            CompleteWarehouseREQ req = new CompleteWarehouseREQ();
            req.setEnterpriseName(billOverdue.getOrgName());
            req.setUnifiedSocialCreditCode(billOverdue.getOrgCode());
            req.setWarehouseTime(finalParse);
            req.setShareType(YesOrNoNumberEnum.NO.getCode());
            req.setBlackGrayType(BlackGrayTypeEnum.GRAY_LIST.name());
            req.setBlackGrayTypeNum(BlackGrayTypeEnum.GRAY_LIST.getNum());
            req.setBlackGraySort(40);
            req.setApplyOrganization(BlackGrayOrgEnum.ZSZL.name());
            req.setShareType(YesOrNoNumberEnum.NO.getCode());
            req.setSource(BlackGraySourceEnum.INTERNAL_UPLOAD.name());
            return req;
        }).collect(Collectors.toList());
        blackGrayLibraryService.completeWarehouse(warehouseReq,"IG20250114000032");
    }

    @Override
    public PageR<BillOverdueRsp> overdueList(BillOverdueReq req) {
        Page<BillOverdue> page = new Page<>(req.getPage(),req.getPageSize());
        String busiDate = req.getBusiDate();
        LambdaQueryWrapper<BillOverdue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(BillOverdue::getBusiDate);
        if (ObjectUtil.isNotNull(busiDate)){
            queryWrapper.eq(BillOverdue::getBusiDate, busiDate);
        }
        if (ObjectUtil.isNotNull(req.getOrgName())){
            queryWrapper.like(BillOverdue::getOrgName, req.getOrgName());
        }

        Page<BillOverdue> overduePage = billOverdueMapper.selectPage(page, queryWrapper);

        return PageR.of(BeanUtil.copyToList(overduePage.getRecords(), BillOverdueRsp.class), overduePage.getTotal(), overduePage.getPages(), req.getPage(), req.getPageSize());
    }

    @Override
    public List<BillOverdue> latestOverdueList(String busiDate) {
        if (ObjectUtil.isNull(busiDate)){
            busiDate = billOverdueMapper.selectLatestBusiDate();
        }
        return billOverdueMapper.selectList(new LambdaQueryWrapper<BillOverdue>().eq(BillOverdue::getBusiDate, busiDate));
    }

    public static List<BillOverdue> parsePdf(MultipartFile multipartFile, String busiDate) {
        List<BillOverdue> billOverdueList = new ArrayList<>();
        try {
            InputStream inputStream = multipartFile.getInputStream();
            PDDocument document = PDDocument.load(inputStream);
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            pdfTextStripper.setSortByPosition(true);
            pdfTextStripper.setStartPage(1);
            pdfTextStripper.setEndPage(document.getNumberOfPages());
            String content = pdfTextStripper.getText(document);

            String[] lines = content.split("\\r?\\n");

            boolean isCompanyTable = false;
            boolean isFinancialTable = false;
            String regex = ".*[\\u4E00-\\u9FFF].*";
            List<String> info = Arrays.asList(new String[4]);
            boolean overRow = false;

            for (String line : lines) {
                if (line.contains("企业名称") && line.contains("统一社会信用代码")) {
                    isCompanyTable = true;
                    isFinancialTable = false;
                    continue;
                } else if (line.contains("金融机构名称") && line.contains("金融机构行号")) {
                    isCompanyTable = false;
                    isFinancialTable = true;
                    continue;
                }

                // 解析企业表格
                if (isCompanyTable && line.matches(regex)) {
                    String[] fields = line.split(" ");
                    if (fields.length <2){
                        if (ObjectUtil.isNull(info.get(1))){
                            info.set(1, fields[0]);
                            overRow = true;
                            continue;
                        }else {
                            String s = info.get(1)+fields[0];
                            info.set(1,s);
                            billOverdueList.add(new BillOverdue()
                                    .setSeqNo(Integer.parseInt(info.get(0)))
                                    .setBusiDate(busiDate)
                                    .setOrgCode(info.get(2))
                                    .setOrgName(info.get(1))
                                    .setOrgType(OrgTypeEnum.ENTERPRISE.getCode())
                                    .setOverdueStartDate(info.get(3)));
                            log.info("info:{}", info);
                            info.set(0, null);
                            info.set(1,null);
                            info.set(2,null);
                            info.set(3,null);
                            overRow = false;
                            continue;
                        }
                    }
                    if (overRow){
                        info.set(0, fields[0]);
                        info.set(2, fields[1]);
                        fields[6] = fields[6].endsWith("日")?fields[6].replace("日",""):fields[6];
                        fields[6] = fields[6].length()==1?"0"+fields[6]:fields[6];
                        info.set(3,fields[2]+"-"+fields[4]+"-"+fields[6]);
                        continue;
                    }
                    fields[7] = fields[7].endsWith("日")?fields[7].replace("日",""):fields[7];
                    fields[7] = fields[7].length()==1?"0"+fields[7]:fields[7];
                    String overdueStartDate = fields[3]+"-"+fields[5]+"-"+fields[7];
                    billOverdueList.add(new BillOverdue()
                            .setSeqNo(Integer.parseInt(fields[0]))
                            .setBusiDate(busiDate)
                            .setOrgCode(fields[2])
                            .setOrgName(fields[1])
                            .setOrgType(OrgTypeEnum.ENTERPRISE.getCode())
                            .setOverdueStartDate(overdueStartDate));
                    //log.info("序号: {}, 企业名称: {}, 社会信用代码: {}, 持续逾期时间: {}", column1, column2, column3, column4);
                }
                // 解析金融机构表格
                else if (isFinancialTable && line.matches(regex)) {
                    String[] fields = line.split(" ");
                    fields[7] = fields[7].length()==1?"0"+fields[7]:fields[7];
                    String column4 = fields[3]+"-"+fields[5]+"-"+fields[7];
                    billOverdueList.add(new BillOverdue()
                            .setSeqNo(Integer.parseInt(fields[0]))
                            .setBusiDate(busiDate)
                            .setOrgCode(fields[2])
                            .setOrgName(fields[1])
                            .setOrgType(OrgTypeEnum.FINANCIAL_INSTITUTION.getCode())
                            .setOverdueStartDate(column4));
                }
            }
        } catch (Exception e) {
            log.error("票据逾期pdf解析失败", e);
            throw new RuntimeException(e);
        }
        return billOverdueList;
    }
}
