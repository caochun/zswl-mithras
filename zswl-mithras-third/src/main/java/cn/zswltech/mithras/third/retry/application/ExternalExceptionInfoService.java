package cn.zswltech.mithras.third.retry.application;

import cn.zswltech.mithras.third.retry.persistence.model.ExternalExceptionInfo;
import cn.zswltech.mithras.third.retry.persistence.mapper.ExternalExceptionInfoMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @ClassName ExternalExceptionInfoService
 * @Description 调用三方异常接口
 * @Author jackerhe
 * @Date 2022/10/17 2:17 下午
 * @Version 1.0
 **/
@Service
public class ExternalExceptionInfoService extends ServiceImpl<ExternalExceptionInfoMapper, ExternalExceptionInfo> {

}
