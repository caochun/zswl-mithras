import { http } from '@zswl/admin'
import { Button } from '@zswl/components'

const FileExport = ({
  tableStore,
  extraParams,
  businessType,
  ids,
  transformParams = (v) => v,
  functionCode,
  children = '导出',
}) => {
  const handleClick = async () => {
    const tableParams = { ...(tableStore?.getParams?.() || {}) }

    Object.keys(tableParams).forEach((key) => {
      if (Array.isArray(tableParams[key])) {
        tableParams[key] = JSON.stringify(tableParams[key])
      }
    })

    const newTableParams = transformParams?.(tableParams)
    await http.post(
      '/file/export',
      {
        businessType,
        ids,
        ext: {
          ...newTableParams,
          ...extraParams,
        },
      },
      {
        type: 'download',
        headers: {
          functionCode: functionCode || 'dashboardFileExport',
        },
      }
    )
  }

  return (
    <Button type="primary" onClick={handleClick}>
      {children}
    </Button>
  )
}

export default FileExport
