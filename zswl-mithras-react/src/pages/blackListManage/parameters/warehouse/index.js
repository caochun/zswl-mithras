import { getTableColumns } from '@/utils'
import { Button, Page, Table, TableStore, Tabs } from '@zswl/components'
import { useMemo } from 'react'
import { history, observer } from '@zswl/admin'
import { DeleteAction } from '@/components/RiskActions'
import { Input, Popconfirm, Space, message } from 'antd'
import warehouseRuleApi from '@/api/blackList/warehouseRuleApi'
import ALL_COLUMNS from '@/components/BlackGray/Columns'
import Store from './store'
import { saveServer } from '@/utils'

const RuleTable = observer(({ path, source }) => {
  const columns = useMemo(() => {
    const nameColumns = [
      {
        title: '规则编号',
        actions: ({ ruleNumber: name, id }) => [{ name, to: `${path}/detail/${id}?view=1` }],
      },
      { title: '规则名称', search: true },
      '规则状态',
      { title: '黑灰标识', search: true },
      '适用业务',
      '适用机构',
      '更新时间',
    ]
    return getTableColumns(ALL_COLUMNS, nameColumns, true)
  }, [])
  const store = useMemo(
    () =>
      new TableStore({
        request: async (params) => {
          return await warehouseRuleApi.postConfigList({ ...params, source })
        },
      }),
    [source]
  )
  const { rows, keys } = store?.getSelected?.()

  const canOpen = rows.length >= 1 && rows.every((item) => !item.status)
  const canStop = rows.length >= 1 && rows.every((item) => item.status)
  const changeState = async (status) => {
    await warehouseRuleApi.postConfigSwitch({ status, ids: keys })
    message.success(`操作成功`)
    store.search()
  }
  const add = () => {
    history.push(`${path}/detail?source=${source}`)
  }
  const edit = () => {
    history.push(`${path}/detail/${keys[0]}?type=edit&source=${source}`)
  }
  return (
    <Table
      columnsFilter={'parameters_warehouse_1'}
              onFilter={(key,val) => saveServer('parameters_warehouse_1',val)}
      
      columns={columns}
      store={store}
      selectable
      serial
      columnWidth={120}
      actions={[
        <Button.Add key="add" onClick={add}>
          新增
        </Button.Add>,
        <Button.Edit key="edit" onClick={edit} disabled={keys.length !== 1}>
          编辑
        </Button.Edit>,
        <Popconfirm
          key={'open'}
          title="确定启用吗？"
          okText="确定"
          cancelText="取消"
          onConfirm={() => changeState(1)}
          placement="top"
          onClick={(e) => e.stopPropagation()}
        >
          <Button.Enable key={'open'} disabled={!canOpen}>
            启用
          </Button.Enable>
        </Popconfirm>,
        <Popconfirm
          key={'stop'}
          title="确定停用吗？"
          okText="确定"
          cancelText="取消"
          onConfirm={() => changeState(0)}
          placement="top"
          onClick={(e) => e.stopPropagation()}
        >
          <Button.Disable key={'stop'} disabled={!canStop}>
            停用
          </Button.Disable>
        </Popconfirm>,
        <DeleteAction
          key="delete"
          store={store}
          disabled={!canOpen}
          api={warehouseRuleApi.postConfigRemove}
        >
          删除
        </DeleteAction>,
      ]}
    />
  )
})

const Index = ({ props: { sub }, path }) => {
  const items = [
    {
      key: 'inside',
      label: '内部名单规则',
      children: <RuleTable path={path} source="INTERNAL_APPROVAL" />,
    },
    {
      key: 'outside',
      label: '外部名单规则',
      children: <RuleTable path={path} source="EXTERNAL_APPROVAL" />,
    },
  ]
  return (
    <Page>
      <Tabs
        items={items}
        type="card"
        activeKey={Store.activeKey}
        onChange={Store.activeKeyChange}
      ></Tabs>
    </Page>
  )
}

export default observer(Index)
