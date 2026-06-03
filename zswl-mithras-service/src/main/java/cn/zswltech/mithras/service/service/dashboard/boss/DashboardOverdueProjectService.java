package cn.zswltech.mithras.service.service.dashboard.boss;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.dto.dashboard.boss.OverdueProjectListRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dashboard.domain.enums.BossDashboardGuanYuanDataSourceKeyEnum;
import cn.zswltech.mithras.dashboard.application.guanyuandata.boss.OverdueProjectDTO;
import cn.zswltech.sleipnir.toolkit.request.guanyuan.GuanYuanDSRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @date 2024/5/16/14:40
 * @description 项目逾期信息
 */
@Slf4j
@Service
public class DashboardOverdueProjectService extends GuanYuanBasicService {
    public List<OverdueProjectListRSP> listOverdueProject() {
        // 调用接口获取数据
        List<OverdueProjectDTO> list = this.queryFromGuanYuan(BossDashboardGuanYuanDataSourceKeyEnum.OverdueProjectList, new GuanYuanDSRequest.Body(), OverdueProjectDTO.class);
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 按照逾期天数倒排
        list.sort(Comparator.comparing(OverdueProjectDTO::getMaxOverdueDays).reversed());
        // 拼装返回参数
        return list.stream().map(e -> {
            OverdueProjectListRSP rsp = new OverdueProjectListRSP();
            rsp.setProjectId(e.getProjReviewId());
            rsp.setProjectName(e.getProjName());
            if (Objects.nonNull(e.getTotalOverdueAmount())) {
                BigDecimal b = BigDecimal.valueOf(e.getTotalOverdueAmount()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP);
                rsp.setOverdueAmount(new ValueUnitDTO(b.toPlainString(), "元"));
            }
            if (Objects.nonNull(e.getMaxOverdueDays())) {
                rsp.setOverdueDays(new ValueUnitDTO(e.getMaxOverdueDays().toString(), "天"));
            }
            rsp.setDeptName(e.getBizDeptName());
            rsp.setSponsorName(e.getProjSponsorUserName());
            return rsp;
        }).collect(Collectors.toList());
    }
}
