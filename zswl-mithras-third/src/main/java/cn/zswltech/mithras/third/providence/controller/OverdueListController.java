package cn.zswltech.mithras.third.providence.controller;

import cn.zswltech.mithras.third.providence.req.BillOverdueReq;
import cn.zswltech.mithras.third.providence.rsp.BillOverdueRsp;
import cn.zswltech.mithras.third.providence.service.BillService;
import cn.zswltech.mithras.third.providence.service.impl.BillOverdueDraftService;
import com.zswltec.providence.dto.base.PageR;
import com.zswltec.providence.dto.base.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;


/**
 * @author Jim
 * @version 1.0.0
 * @descripition: 逾期名单管理
 * @date 2024/12/23 14:12
 */
@RestController
@Slf4j
public class OverdueListController {

    @Resource
    private BillService billService;
    @Resource
    private BillOverdueDraftService billOverdueDraftService;

    /**
     * 票据逾期名单导入
     * @param multipartFile 文件
     * @param busiDate 业务日期 yyyy-MM-dd
     * @return
     */
    @PostMapping("/overdueList/import")
    public R overdueListImport(@RequestParam("multipartFile") MultipartFile multipartFile, @RequestParam("busiDate") String busiDate) {
        return billService.overdueListImport(multipartFile,busiDate);
    }

    /**
     * 票据逾期名单查询
     * @param req 截止日期
     * @return 逾期名单
     */
    @PostMapping("/overdueList/list")
    public R<PageR<BillOverdueRsp>> overdueList(@RequestBody BillOverdueReq req) {
        return R.ok(billService.overdueList(req));
    }

    /**
     * 票据逾期名单（草稿）导入
     * @param multipartFile 文件
     * @param busiDate 业务日期 yyyy-MM-dd
     * @return 操作结果
     */
    @PostMapping(path = "/overdueList/draft/import")
    public R<Void> overdueListDraftImport(@RequestParam("multipartFile") MultipartFile multipartFile, @RequestParam("busiDate") String busiDate) {
        billOverdueDraftService.parseAndImport(multipartFile, busiDate);
        return R.ok();
    }

    /**
     * 票据逾期名单（草稿）查询
     * @param req 查询条件
     * @return 逾期名单
     */
    @PostMapping("/overdueList/draft/list")
    public R<PageR<BillOverdueRsp>> overdueListDraft(@RequestBody BillOverdueReq req) {
        return R.ok(billOverdueDraftService.pageList(req));
    }
}
