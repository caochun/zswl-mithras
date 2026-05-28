import { Descriptions, Form, Table, TableStore, Button } from '@zswl/components'
import { message, Space } from 'antd'
import { forwardRef, useEffect, useImperativeHandle, useMemo, useState } from 'react'
import styles from './index.less'
import { isElement, isString } from 'lodash'
import { saveServer } from '@/utils'
import TableExport from '@/components/Actions/TableExport'

function Index(
  {
    columns,
    title,
    detail = [],
    saveData,
    isLog,
    canEdit = true,
    initEdit = false,
    tableApi,
    access,
    tableStoreConfig,
    otherExcelProps,
    ...rest
  },
  ref
) {
  const [baseEdit, setBaseEdit] = useState(initEdit)
  const [tableList, setTableList] = useState([])

  const table = useMemo(() => {
    return new TableStore({
      request: async (params) => {
        const { list = [], ...restTable } = (await tableApi?.(params)) || {}
        const newList = list.map(({ id, ...item }) => ({
          id: id?.value ?? id,
          ...item,
        }))
        setTableList(newList)
        return { list: newList, ...restTable }
      },
      ...tableStoreConfig,
    })
  }, [tableApi])
  const saveBase = async () => {
    const { list, values } = await table.submit()
    await saveData?.(list, values)
    message.success('保存成功')
    setBaseEdit(false)
    table?.search()
  }

  useImperativeHandle(ref, () => ({
    setBaseEdit,
    baseEdit,
    tableList,
    table,
  }))

  return (
    <>
      <div className={styles.titleRow}>
        {isString(title) ? <h3>{title}</h3> : title}
        {tableList?.length > 0 && (
          <Space key="edit">
            {canEdit && baseEdit && (
              <>
                <Button key="cancel" onClick={() => setBaseEdit(false)}>
                  取消
                </Button>
                <Button type="primary" key="save" onClick={saveBase}>
                  保存
                </Button>
              </>
            )}
            {canEdit && !baseEdit && (
              <Button type="primary" key="edit" onClick={() => setBaseEdit(true)} access={access}>
                编辑
              </Button>
            )}
            {otherExcelProps && <TableExport table={table} otherExcelProps={otherExcelProps} />}
          </Space>
        )}
      </div>
      <Table
        resizable
        store={table}
        columnWidth={120}
        columnsFilter={'Table_EditTable_1'}
        onFilter={(key, val) => saveServer('Table_EditTable_1', val)}
        columns={columns}
        editable={baseEdit}
        className={styles.desc}
        {...rest}
      />
    </>
  )
}

export default forwardRef(Index)
