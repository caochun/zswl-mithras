package cn.zswltech.mithras.service.service.riskcontrol;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlConcentrationGroupListREQ;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlConcentrationGroupListRSP;
import cn.zswltech.mithras.riskcontrol.concentration.RiskControlConcentrationGroupConverter;
import cn.zswltech.mithras.riskcontrol.concentration.RiskControlConcentrationGroup;
import cn.zswltech.mithras.riskcontrol.concentration.RiskControlConcentrationGroupMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.math.RoundingMode.HALF_UP;

/**
 * @author zhaozhengkang
 * @description 集团集中度
 * @date 2023-03-02
 */
@Service
public class RiskControlConcentrationGroupService
        extends ServiceImpl<RiskControlConcentrationGroupMapper, RiskControlConcentrationGroup> {
    @Resource
    private RiskControlConcentrationGroupConverter baseConverter;
    @Resource
    private RiskControlConcentrationClientService riskControlConcentrationClientService;

    public PageR<RiskControlConcentrationGroupListRSP> list(RiskControlConcentrationGroupListREQ req) {
        QueryWrapper<RiskControlConcentrationGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("date_time_point", req.getDataTimePoint());
        queryWrapper.eq(ObjectUtil.isNotEmpty(req.getState()), "state", req.getState());
        queryWrapper.like(ObjectUtil.isNotEmpty(req.getGroupName()), "group_name", req.getGroupName());
        queryWrapper.eq(ObjectUtil.isNotEmpty(req.getZhejiangInnerGroup()), "zhejiang_inner_group",
                req.getZhejiangInnerGroup());
        queryWrapper.orderByDesc("remaining_principal");
        Page<RiskControlConcentrationGroup> page = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), queryWrapper);
        BigDecimal totalRemaining = riskControlConcentrationClientService.getTotalRemaining();
        List<RiskControlConcentrationGroupListRSP> rsps = new ArrayList<>();
        for (RiskControlConcentrationGroup record : page.getRecords()) {
            RiskControlConcentrationGroupListRSP rsp = baseConverter.entity2ListRsp(record);
            if (rsp.getBadBalance() != null && rsp.getBadBalance() != 0L) {
                rsp.setBadBalanceRatio(new BigDecimal(rsp.getBadBalance()).divide(totalRemaining, 4, HALF_UP)
                        .multiply(new BigDecimal(10000L)).longValue());
            }
            rsps.add(rsp);
        }
        return PageR.of(page, rsps);
    }
}