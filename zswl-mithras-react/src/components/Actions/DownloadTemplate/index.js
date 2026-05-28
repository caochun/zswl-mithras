import { observer, http } from '@zswl/admin'
import { App, Button } from '@zswl/components'
import fileList from '@/api/common/fileList'
import { downFile, toHump } from '@/utils'
import { isFunction } from 'lodash'
import { message } from 'antd'

/**
 * 表格中导出操作，渲染一个导出按钮
 * @param store 表格的store，必传
 * @param api 导出的api
 * @param fileName 导出的文件名
 */
function Index({ disabled, templateDownApi, params = {}, access, title = '模板下载', size= 'medium'}) {
  const { functionCode: customFunctionCode, ...rest } = params
  const handleClick = async (e) => {
    if (isFunction(templateDownApi)) return await templateDownApi()
    const functionCode =
      customFunctionCode || (rest?.moduleType && `${toHump(rest.moduleType)}FileDownloadTemplate`)

    const res = await fileList.getDownloadTemplate?.(rest, functionCode)
    if (!res?.success) {
      message.error(res.msg)
    } else {
      downFile(res)
    }
  }

  return (
    <Button.Download disabled={disabled} onClick={handleClick} access={access} size={size}>
      {title}
    </Button.Download>
  )
}

export default observer(Index)
