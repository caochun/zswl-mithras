import IconFont from '@/components/Icon'
import { observer, http } from '@zswl/admin'
import { Button } from '@zswl/components'
import { message } from 'antd'
import { useMemo } from 'react'

/**
 * 表格中导出操作，渲染一个导出按钮
 * @param store 表格的store，必传
 * @param api 导出的api
 * @param fileName 导出的文件名
 */
function ExportAction({ icon = 'icon-icon_export', disabled, onClick, access, text = '导出' }) {
  const handleClick = async (e) => {
    return onClick?.(e).then((res) => {
      if (res?.code === 200) {
        message.success('导出成功')
      } else if (res?.msg) {
        message.error(res.msg)
      }
    })
  }
  const BtnIconFont = useMemo(() => {
    if (typeof icon === 'string') {
      return <IconFont type={icon} />
    } else {
      return icon
    }
  }, [icon])
  return (
    <Button icon={BtnIconFont} disabled={disabled} onClick={handleClick} access={access}>
      {text}
    </Button>
  )
}

export default observer(ExportAction)
