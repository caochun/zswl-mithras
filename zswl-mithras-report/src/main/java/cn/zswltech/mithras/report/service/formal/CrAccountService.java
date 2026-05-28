package cn.zswltech.mithras.report.service.formal;

import cn.zswltech.mithras.report.mapper.formal.CrAccountMapper;
import cn.zswltech.mithras.report.mapper.formal.model.CrAccount;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 征信报送-账户表
 *
 * @author wangchuanhao
 * @date 2022/10/8 10:41 AM
 */
@Service
public class CrAccountService extends ServiceImpl<CrAccountMapper, CrAccount> implements IService<CrAccount> {

}
