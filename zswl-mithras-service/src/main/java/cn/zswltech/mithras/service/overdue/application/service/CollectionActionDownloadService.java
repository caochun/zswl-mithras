package cn.zswltech.mithras.service.overdue.application.service;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.mithras.contract.enums.overdue.OverdueCollectionType;
import cn.zswltech.mithras.contract.overdue.application.assembler.CollectionAssembler;
import cn.zswltech.mithras.contract.overdue.application.dto.CollectionActionDto;
import cn.zswltech.mithras.contract.overdue.domain.collection.Collection;
import cn.zswltech.mithras.contract.overdue.domain.collection.CollectionId;
import cn.zswltech.mithras.contract.overdue.domain.collection.CollectionRepository;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.others.MithrasException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
public class CollectionActionDownloadService {
    @Resource
    private CollectionRepository collectionRepository;
    @Resource
    private CollectionAssembler collectionAssembler;
    @Autowired
    private HttpServletResponse response;

    public void downloadAction(CollectionId collectionId) {
        Collection collection = collectionRepository.find(new CollectionId(collectionId.getId()));
        try {
            List<CollectionActionDto> originalList =
                    collectionAssembler.entity2ActionListDto(collection.getCollectionActionList());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ExcelWriter w = ExcelUtil.getWriter(true);
            w.writeHeadRow(ListUtil.of("催收记录编号", "催收类型", "催收日期", "催收人员", "催收进展"));

            for (CollectionActionDto c : originalList) {
                w.writeRow(ListUtil.of(
                        c.getCode(),
                        OverdueCollectionType.valueOf(c.getType()).display(),
                        c.getDate(),
                        c.getProcessPerson(),
                        c.getDescribe()
                ));
            }
            w.flush(bos, true);
            ServletOutputStream outputStream = response.getOutputStream();
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(
                    collection.getClientName() + "逾期催收记录" + GlobalConstants.OFFICE_EXCEL_SUFFIX,
                    StandardCharsets.UTF_8.name()));
            outputStream.write(bos.toByteArray());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出逾期催收记录发生未知异常", e);
            throw new MithrasException("导出逾期催收记录发生未知异常");
        }
    }
}
