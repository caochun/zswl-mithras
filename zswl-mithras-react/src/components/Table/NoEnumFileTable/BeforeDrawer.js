import { FiledFormat } from '@/components/Format'
import { observer } from '@zswl/admin'
import { Button, Drawer, DrawerStore, Table, TableStore } from '@zswl/components'
import { useMemo, useState } from 'react'
import { saveServer } from '@/utils'

const filedRender = (val) => {
  const value = val?.beforeValue !== undefined ? val?.beforeValue : val
  return <FiledFormat title={value} isChange={val?.isChange} needBar={false} />
}
function Index({ dataSource = [], columns, ...rest }) {
  const drawerStore = useMemo(() => new DrawerStore(), [])
  const items = useMemo(() => {
    return columns.map((item) => {
      return {
        ...item,
        render: filedRender,
      }
    })
  }, [columns])
  const tableData = useMemo(() => {
    return (dataSource || []).filter((v) => v?.name?.beforeValue)
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
        <Table onFilter={(key,val) => saveServer('Table_NoEnumFileTable_BeforeDrawer',val)} columnsFilter={'Table_NoEnumFileTable_BeforeDrawer'} dataSource={tableData} columns={items} />
      </Drawer>
    </>
  )
}

export default observer(Index)
