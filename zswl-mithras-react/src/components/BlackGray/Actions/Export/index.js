import { observer, http } from '@zswl/admin'
import { Button } from '@zswl/components'

/**
 * 表格中导出操作，渲染一个导出按钮
 * @param store 表格的store，必传
 * @param api 导出的api
 * @param fileName 导出的文件名
 * @param access 权限标识
 */
function BlackGrayExportAction({ store, api, fileName, access, type = 'get' }) {
  const list = store.getList()
  const disabled = list.length === 0
  const handleClick = async () => {
    if (disabled) {
      return
    }
    let params
    const { keys } = store.getSelected()
    if (keys.length) {
      params = { ids: keys }
    } else {
      /*
         这里取到的可能是未经过优化的参数，所以外部应该优先优化好参数
         通过TableStore的optimizeParams返回优化之后的参数
         */
      params = store.getParams()
    }
    const { page, pageSize, ...rest } = params
    if (typeof api === 'function') {
      await api(rest)
    } else if (typeof api === 'string') {
      await http[type](api, { params: rest, type: 'download', fileName })
    }
  }
  return (
    <Button.Download disabled={disabled} onClick={handleClick} access={access}>
      导出
    </Button.Download>
  )
}

export default observer(BlackGrayExportAction)
