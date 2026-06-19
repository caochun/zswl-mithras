import { Button } from '@zswl/components'
import Api from '@/api/common/actionApi'

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
    await Api.exportFile(
      {
        businessType,
        ids,
        ext: {
          ...newTableParams,
          ...extraParams,
        },
      },
      functionCode
    )
  }

  return (
    <Button type="primary" onClick={handleClick}>
      {children}
    </Button>
  )
}

export default FileExport
