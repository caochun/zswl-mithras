package cn.zswltech.mithras.service.overdue.infrastructure.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.contract.overdue.application.assembler.DocPrintingAssembler;
import cn.zswltech.mithras.contract.overdue.application.dto.PrintingListDto;
import cn.zswltech.mithras.contract.overdue.application.query.PrintingPageQuery;
import cn.zswltech.mithras.contract.overdue.application.service.PrintingQueryService;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.DocPrintingDao;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.model.DocPrinting;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/5 15:14
 */
@Service
public class PrintingQueryServiceImpl implements PrintingQueryService {
    @Resource
    private DocPrintingDao docPrintingDao;
    @Resource
    private DocPrintingAssembler docPrintingAssembler;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FlowTaskApiService taskApiService;

    @Override
    public PageR<PrintingListDto> page(PrintingPageQuery query) {
        Page<DocPrinting> page = docPrintingDao.page(new Page<>(query.getPage(), query.getPageSize()),
                Wrappers.<DocPrinting>lambdaQuery()
                        .eq(ObjectUtil.isNotEmpty(query.getApplicant()), DocPrinting::getCreateBy, query.getApplicant())
                        .eq(ObjectUtil.isNotEmpty(query.getType()), DocPrinting::getType, query.getType())
                        .orderByDesc(DocPrinting::getUpdateTime));
        List<PrintingListDto> printingListDto = docPrintingAssembler.po2ListDto(page.getRecords());
        Map<Long, String> userNames = id2NameService.sysUserId2Name(printingListDto.stream().map(PrintingListDto::getCreateBy).collect(Collectors.toSet()));
        for (PrintingListDto item : printingListDto) {
            item.setCreateByName(userNames.get(item.getCreateBy()));
            if (StringUtils.isNotBlank(item.getProcessId())) {
                ProcessResp processResp = taskApiService.queryProcessById(item.getProcessId());
                if (processResp != null) {
                    String startUserId = processResp.getStartUserId();
                    OrgDO orgDO = sysUserService.getBizDeptByUserId(Long.valueOf(startUserId));
                    if (orgDO != null) {
                        item.setApplyDeptName(orgDO.getName());
                    }
                    String userName = SpringUtil.getBean(Id2NameService.class).sysUserId2NameSingle(Long.valueOf(startUserId));
                    item.setApplyName(userName);
                }
            } else {
                item.setProcessStatus("UN_SUBMIT");
            }
        }
        return PageR.of(page, printingListDto);
    }
}
