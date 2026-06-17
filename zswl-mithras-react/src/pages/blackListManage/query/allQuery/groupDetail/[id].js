import { getTableColumns } from '@/utils'
import { Card } from 'antd'
import { http, makeAutoObservable, observer } from '@zswl/admin'
import { Table, Descriptions, Page } from '@zswl/components'
import ALl_COLUMNS from '@/components/BlackGray/Columns'
import { useMemo } from 'react'
import { saveServer } from '@/utils'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new Page.Store()
  base = new Descriptions.Store({
    request: () => {
      const { id } = this.page.getParams()
      return http.get('/black/gray/singleGroup/detail', {
        params: {
          groupName: id,
        },
      })
    },
  })
  list = new Table.Store({
    request: (params) => {
      const { id, ...rest } = this.page.getParams()
      return http.get('/black/gray/singleGroup/groupStockList', {
        params: {
          ...params,
          groupName: id,
          ...rest,
        },
      })
    },
  })
  company = new Table.Store({
    request: (params) => {
      const { id, ...rest } = this.page.getParams()
      return http.get('/black/gray/singleGroup/groupCompanyStockList', {
        params: {
          ...params,
          groupName: id,
          ...rest,
        },
      })
    },
  })
}
const store = new Store()

const Index = ({ params, query }) => {
  const listColumns = useMemo(() => {
    return getTableColumns(ALl_COLUMNS, [
      { title: '所属机构', dataIndex: 'applyOrganization', matchOption: 'orgOptions' },
      { title: '业务类型' },
      { title: '黑灰标识' },
      { title: '入库原因', dataIndex: 'applyReason' },
      { title: '入库日期' },
      { title: '计划出库日期' },
      { title: '业务规模（万元）' },
      { title: '名单来源' },
    ])
  }, [])
  const companyColumns = useMemo(() => {
    return getTableColumns(ALl_COLUMNS, [
      { title: '企业名称' },
      {
        title: '统一社会信用代码',
      },
      {
        title: '黑灰标识',
      },
    ])
  }, [])
  return (
    <Page params={{ ...params, ...query }} store={store} current="在库明细">
      <Card title="基本信息">
        <Descriptions
          store={store.base}
          items={[
            {
              title: '集团名称',
              dataIndex: 'groupName',
            },
            {
              title: '统一社会信用代码',
              dataIndex: 'groupCreditCode',
            },
          ]}
        />
      </Card>
      <Card title="在库明细" style={{ marginTop: 20 }}>
        <Table onFilter={(key,val) => saveServer('allQuery_groupDetail_idjs_1',val)} columnsFilter={'allQuery_groupDetail_idjs_1'} columns={listColumns} store={store.list} serial columnWidth={120} />
      </Card>
      <Card title="在库下属企业" style={{ marginTop: 20 }}>
        <Table onFilter={(key,val) => saveServer('allQuery_groupDetail_idjs_2',val)} columnsFilter={'allQuery_groupDetail_idjs_2'} columns={companyColumns} store={store.company} serial columnWidth={120} />
      </Card>
    </Page>
  )
}
export default observer(Index)
