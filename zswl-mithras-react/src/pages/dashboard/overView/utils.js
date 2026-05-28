// https://w0ejidjnlc5.feishu.cn/wiki/Wv7cwYjwYiGqHlk9thqcz4Q2n0b
//   1. 鲁素萍和张磐只能看到其分管部门对应的信息 临时处理
import { getUserInfo } from '@/utils'

export const canSeeDetailFn = () => {
  const { id } = getUserInfo()
  return ![52, 198].includes(id)
}
