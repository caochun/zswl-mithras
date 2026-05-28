import { downLoadExcel, downLoadMorExcel } from '@/components/Excel'
import IconFont from '@/components/Icon'
import { observer, http } from '@zswl/admin'
import { Button } from '@zswl/components'
import _ from 'lodash'
import { useMemo } from 'react'

/**
 * 表格中导出操作，渲染一个导出按钮
 * @param store 表格的store，必传
 * @param api 导出的api
 * @param fileName 导出的文件名
 */
function Index({ icon = 'icon-icon_export', table, disabled, access, otherExcelProps }) {
  const getTableData = async ({ tableStore }) => {
    const params = tableStore.getParams()
    const postParams = {
      ...params,
      pageSize: 9999,
      page: 1,
    }
    const res = await tableStore.request(postParams)
    const columns = tableStore.getOptimizedColumns()
    const dataSource = _.isArray(res.list) ? res.list : res

    return { dataSource, columns }
  }
  const handleClick = async () => {
    const tableList = _.isArray(table) ? table : [{ tableStore: table, otherData: [] }]
    const excelList = await Promise.all(
      tableList.filter(Boolean).map(async ({ tableStore, otherData, ...rest }) => {
        const { dataSource, columns } = await getTableData({ tableStore })
        return { dataSource: [...dataSource, ...(otherData ?? [])], columns, ...rest }
      })
    )
    downLoadMorExcel({
      excelList,
      fileName: otherExcelProps?.fileName,
      format: otherExcelProps?.format,
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
    <Button
      icon={BtnIconFont}
      disabled={disabled}
      onClick={handleClick}
      access={access}
      type="primary"
    >
      导出
    </Button>
  )
}

export default observer(Index)
