package cn.zswltech.mithras.api.riskcontrol;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.riskcontrol.model.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author yibin
 * 金控集中度报送相关api；
 * 这里的集中度并非字面意思的集中度，其实是项目维度的数据报送
 */
public interface RiskControlJzdReportApi {

    /**
     * 将集中度记录报送给母公司
     *
     * @param req
     * @return
     */
    @PostMapping("/risk/control/jzd/report/submit")
    R<Void> submit(@RequestBody @Valid JzdReportSubmitREQ req);

    /**
     * 手动新增一条集中度报送数据
     *
     * @param req
     * @return
     */
    @PostMapping("risk/control/jzd/report/add")
    R<Void> addManually(@RequestBody @Valid JzdReportAddREQ req);

    /**
     * 集中度报送记录删除
     *
     * @param req
     * @return
     */
    @PostMapping("risk/control/jzd/report/remove")
    R<Void> remove(@RequestBody @Valid JzdReportRemoveREQ req);

    /**
     * 集中度报送记录-修改
     *
     * @param req
     * @return
     */
    @PostMapping("risk/control/jzd/report/modify")
    R<Void> modify(@RequestBody @Valid JzdReportModifyREQ req);

    /**
     * 集中度报送记录-列表
     *
     * @param req
     * @return
     */
    @PostMapping("risk/control/jzd/report/list")
    R<PageR<JzdReportListRSP>> list(@RequestBody @Valid JzdReportListREQ req);


}
