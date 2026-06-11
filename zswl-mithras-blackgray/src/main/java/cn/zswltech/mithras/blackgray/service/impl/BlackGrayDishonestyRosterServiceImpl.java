package cn.zswltech.mithras.blackgray.service.impl;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayDishonestyRosterREQ;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayDishonestyRosterRSP;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayDishonestyRosterMapper;
import cn.zswltech.mithras.blackgray.mapper.model.BlackGrayDishonestyRosterDO;
import cn.zswltech.mithras.blackgray.service.BlackGrayDishonestyRosterService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlackGrayDishonestyRosterServiceImpl implements BlackGrayDishonestyRosterService {

    @Resource
    private BlackGrayDishonestyRosterMapper blackGrayDishonestyRosterMapper;

    @Override
    public R<PageInfo<BlackGrayDishonestyRosterRSP>> query(BlackGrayDishonestyRosterREQ req) {
        Page<BlackGrayDishonestyRosterRSP> page = PageHelper.startPage(req.getPage(), req.getPageSize());

        Example example = new Example(BlackGrayDishonestyRosterDO.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(req.getCertCode())) {
            criteria.andLike(BlackGrayDishonestyRosterDO.CERT_CODE, "%" + req.getCertCode() + "%");
        }
        if (StringUtils.isNotBlank(req.getPeopleEnforced())) {
            criteria.andLike(BlackGrayDishonestyRosterDO.PEOPLE_ENFORCED, "%" + req.getPeopleEnforced() + "%");
        }
        if (StringUtils.isNotBlank(req.getRosterType())) {
            criteria.andEqualTo(BlackGrayDishonestyRosterDO.ROSTER_TYPE, req.getRosterType());
        }
        List<BlackGrayDishonestyRosterDO> itemList = blackGrayDishonestyRosterMapper.selectByExample(example);
        List<BlackGrayDishonestyRosterRSP> dtoList = itemList.stream().map(item -> {
            BlackGrayDishonestyRosterRSP rsp = new BlackGrayDishonestyRosterRSP();
            BeanUtils.copyProperties(item, rsp);
            return rsp;
        }).collect(Collectors.toList());

        page.clear();
        page.addAll(dtoList);
        return R.ok(new PageInfo<>(page));
    }
}
