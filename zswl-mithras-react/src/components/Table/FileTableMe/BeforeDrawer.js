import { FiledFormat } from '@/components/Format'
import { observer } from '@zswl/admin'
import { Button, Drawer, DrawerStore, Table, TableStore } from '@zswl/components'
import { useEffect, useMemo, useState } from 'react'
import { saveServer } from '@/utils'

const filedRender = (val) => {
  const value = val?.beforeValue !== undefined ? val?.beforeValue : val
  return <FiledFormat title={value} isChange={val?.isChange} needBar={false} />
}
function Index({ dataSource = [], columns, ...rest }) {
  const drawerStore = useMemo(() => new DrawerStore(), [])
  const [expandKeys, setExpandKeys] = useState(['folder-0'])
  const items = useMemo(() => {
    return columns.map((item) => {
      return {
        ...item,
        render: filedRender,
      }
    })
  }, [columns])

  useEffect(() => {
    setExpandKeys(dataSource?.map((item, index) => `folder-${index}`))
  }, [dataSource])
  const tableData = useMemo(() => {
    return (dataSource || []).map((item, index) => {
      return {
        ...item,
        children: (item?.children || [])?.filter((v) => v?.name?.beforeValue),
      }
    })
  }, [dataSource])
  return (
    <>
      <Button key="before" onClick={() => drawerStore.open()} type="primary">
        变更前数据
      </Button>
      <Drawer
        store={drawerStore}
        width={'70%'}
        placement="left"
        title="变更前数据"
        extra={[]}
        {...rest}
      >
        <Table
          dataSource={tableData}
          columns={items}
        columnsFilter={'Table_FileTable_BeforeDrawer'}
      onFilter={(key,val) => saveServer('Table_FileTable_BeforeDrawer',val)}

          expandable={{
            expandedRowKeys: expandKeys,
            onExpand: (expanded, record) => {
              if (expanded) {
                setExpandKeys([...expandKeys, record.id])
              } else {
                setExpandKeys(expandKeys.filter((item) => item !== record.id))
              }
            },
          }}
        />
      </Drawer>
    </>
  )
}

export default observer(Index)
