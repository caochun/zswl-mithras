package cn.zswltech.mithras.credit.application.groupcredit.establish.impl;

import cn.zswltech.mithras.credit.groupcredit.establish.mapper.GroupCreditEstablishBaseInfoLibMapper;
import cn.zswltech.mithras.credit.groupcredit.establish.mapper.model.GroupCreditEstablishBaseInfoLib;
import cn.zswltech.mithras.credit.application.groupcredit.establish.GroupCreditEstablishBaseInfoLibService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @description 集团授信立项基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@Service
public class GroupCreditEstablishBaseInfoLibServiceImpl extends ServiceImpl<GroupCreditEstablishBaseInfoLibMapper, GroupCreditEstablishBaseInfoLib>
        implements GroupCreditEstablishBaseInfoLibService {
}
