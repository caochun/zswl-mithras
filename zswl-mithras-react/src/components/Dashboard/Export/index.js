import { Button } from '@zswl/components'
import Api from './api'

const Index = ({
  tableStore,
  extraParams,
  businessType,
  ids,
  transformParams = (v) => v,
  functionCode,
}) => {
  const handleClick = async () => {
    const tableParams = tableStore?.getParams()

    Object.keys(tableParams).map((key) => {
      if (Array.isArray(tableParams[key])) {
        tableParams[key] = JSON.stringify(tableParams[key])
      }
    })
    const newTableParams = transformParams?.(tableParams)
    await Api.postDashboardFileExport(
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
      导出
    </Button>
  )
}

export default Index
