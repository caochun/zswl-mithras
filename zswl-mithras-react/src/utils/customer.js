import { getQuery } from '@zswl/admin'

// client表加个字段，isReleased 表示是否被释放过 boolean, 之前clientStatus = 释放变成，clientStatus === 新建  && isReleased === true，其余都不为释放
export const transformClientStatus = ({ clientStatus, isReleased }) => {
  return clientStatus === 'NEW' && isReleased ? 'RELEASE' : clientStatus
}

// 客户列表这边跳详情，编辑区需要这个字段，表示只能看自己的版本。其他的入口进来只能看 生效的版本
export const getIsClientDetailParams = () => {
  return {
    isClientDetail: getQuery('isClientDetail') ? true : '',
  }
}
