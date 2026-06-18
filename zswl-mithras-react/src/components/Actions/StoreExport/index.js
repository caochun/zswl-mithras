import { observer, http } from '@zswl/admin'
import { Button } from '@zswl/components'

/**
 * Export action driven by a TableStore. If rows are selected, selected ids are exported;
 * otherwise current table params are exported.
 */
function StoreExport({ store, api, fileName, access, type = 'get' }) {
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

export default observer(StoreExport)
