import { observer, http } from '@zswl/admin'
import { Button } from '@zswl/components'
import { message, Modal } from 'antd'

/**
 * 表格中删除操作，渲染一个删除按钮
 * @param store 表格的store,必传
 * @param api 接口,当是字符串时表示接口地址，函数时就会直接调用
 */
function Index({ store, api, ...rest }) {
  const handleClick = () => {
    Modal.confirm({
      title: '确定删除吗？',
      onOk: async () => {
        const { keys } = store.getSelected()
        if (typeof api === 'function') {
          await api({ ids: keys })
        } else if (typeof api === 'string') {
          await http.post(api, { ids: keys })
        }
        message.success('删除成功')
        store.search()
      },
    })
  }
  return <Button.Delete {...rest} onClick={handleClick} />
}

export default observer(Index)
