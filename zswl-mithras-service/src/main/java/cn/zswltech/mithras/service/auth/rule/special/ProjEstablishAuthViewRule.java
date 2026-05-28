package cn.zswltech.mithras.service.auth.rule.special;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.service.SysUserService;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.hutool.core.util.ObjectUtil.equal;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.gruul.common.util.AccountUtil.getLoginInfo;

/**
 * 立项查看权限
 *
 * @author wangchuanhao
 * @date 2022/12/8 10:22 AM
 */
@Component
public class ProjEstablishAuthViewRule {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;

    /**
     * 如果有评审 用评审的主办协办
     * 如果没有评审 用立项的主办协办
     * @param projEstablishId
     */
    public void checkEstablish(Long projEstablishId) {
        List<Long> deptIds = sysUserService.canViewDeptIds();
        if (isNull(deptIds)) {
            // 当前用户非业务部门用户，可查看全部
            return;
        }
        ProjEstablishBaseInfo projEstablishBaseInfo = projEstablishBaseInfoMapper.selectById(projEstablishId);
        if (Objects.isNull(projEstablishBaseInfo)) {
            throw new AuthCheckException("主表数据不存在");
        }
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectOne(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .eq(ProjReviewBaseInfo::getProjEstablishId, projEstablishBaseInfo.getId())
                .notIn(ProjReviewBaseInfo::getProjReviewStatus, ListUtil.toList(RecordStatus.CLOSED.name(), RecordStatus.EXPIRE.name()))
                .last("LIMIT 1")
        );
        if (Objects.nonNull(projReviewBaseInfo)) {
            checkReview(projReviewBaseInfo, deptIds);
            return;
        }
        List<Long> cosponsorList = StringUtils.isBlank(projEstablishBaseInfo.getProjCosponsorUserIds())
                ? new ArrayList<>() : JSONArray.parseArray(projEstablishBaseInfo.getProjCosponsorUserIds(), Long.class);
        if (equal(projEstablishBaseInfo.getProjSponsorUserId(), getLoginInfo().getId())
                || cosponsorList.contains(getLoginInfo().getId())
                || deptIds.contains(projEstablishBaseInfo.getBizDeptId())) {
            return;
        } else {
            throw new AuthCheckException("当前登陆用户无权限查看");
        }

    }

    public void checkReview(Long projReviewId) {
        List<Long> deptIds = sysUserService.canViewDeptIds();
        if (isNull(deptIds)) {
            // 当前用户非业务部门用户，可查看全部
            return;
        }
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(projReviewId);
        if (Objects.isNull(projReviewBaseInfo)) {
            throw new AuthCheckException("主表数据不存在");
        }
        checkReview(projReviewBaseInfo, deptIds);
    }

    private void checkReview(ProjReviewBaseInfo projReviewBaseInfo, List<Long> deptIds) {
        List<Long> cosponsorList = StringUtils.isBlank(projReviewBaseInfo.getProjCosponsorUserIds())
                ? new ArrayList<>() : JSONArray.parseArray(projReviewBaseInfo.getProjCosponsorUserIds(), Long.class);
        if (equal(projReviewBaseInfo.getProjSponsorUserId(), getLoginInfo().getId())
                || cosponsorList.contains(getLoginInfo().getId())
                || deptIds.contains(projReviewBaseInfo.getBizDeptId())) {
            return;
        } else {
            throw new AuthCheckException("当前登陆用户无权限查看");
        }
    }

}
