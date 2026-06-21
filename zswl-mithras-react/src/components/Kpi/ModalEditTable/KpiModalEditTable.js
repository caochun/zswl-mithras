import { useState, useMemo, useEffect } from 'react'
import { observer } from '@zswl/admin'
import { Table, Button, TableStore } from '@zswl/components'
import { Space, message } from 'antd'
import styles from './index.less'
import { saveServer } from '@/utils'

const KpiModalEditTable = (props) => {
  const { store, columns, saveApi, tableApi, typeInfo, ...rest } = props
  const [editable, setEditable] = useState(typeInfo.isEdit)

  const $table = useMemo(() => {
    return new TableStore({
      request: async () => {
        return await tableApi()
      },
      pagination: false,
    })
  }, [tableApi])

  const saveEditData = async () => {
    const { list, values } = await $table.submit()
    if (list) {
      await saveApi({ list, values })
      message.success('保存成功')
      $table.search()
      setEditable(false)
    }
  }

  useEffect(() => {
    $table.search()
  }, [tableApi])

  return (
    <>
      {typeInfo.isEdit ? (
        <div className={styles.header}>
          {!editable ? (
            <Button type="primary" onClick={() => setEditable(true)}>
              编辑
            </Button>
          ) : (
            <Space>
              <Button onClick={() => setEditable(false)}>取消</Button>
              <Button onClick={saveEditData} type="primary">
                确认
              </Button>
            </Space>
          )}
        </div>
      ) : null}

      <Table
        columnsFilter={'Component_ModalEditTable_1'}
        onFilter={(key, val) => saveServer('Component_ModalEditTable_1', val)}
        scroll={false}
        className={styles.zStyleTable}
        bordered
        resizable={false}
        autoRequest={false}
        columnWidth={160}
        store={$table}
        columns={columns(editable)}
        {...rest}
      ></Table>
    </>
  )
}

export default observer(KpiModalEditTable)
