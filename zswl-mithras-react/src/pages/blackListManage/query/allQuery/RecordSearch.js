import { getTableColumns, isUnifiedCreditCode } from '@/utils'
import { observer } from '@zswl/admin'
import { Select, Table, TableStore } from '@zswl/components'
import ALl_COLUMNS from '../../Column'
import { useMemo } from 'react'
import { ExportAction } from '@/components/RiskActions'
import listLibraryApi from '@/api/blackList/listLibraryApi'
import queryExternalDataApi from '@/api/blackList/queryExternalDataApi'
import { saveServer } from '@/utils'

export const getEnterpriseName = async (searchValue) => {
  if (!searchValue) return []
  const params = {}

  if (!isUnifiedCreditCode(searchValue)) {
    params.enterpriseName = searchValue
  } else {
    params.unifiedSocialCreditCode = searchValue
  }

  const cache = await queryExternalDataApi.postVagueEnterprise(params).then((res) =>
    res.map((item) => ({
      label: item.enterpriseName,
      value: item.unifiedSocialCreditCode,
    }))
  )
  return cache
}
const Index = () => {
  const columns = useMemo(() => {
    const nameColumns = [
      { title: '企业名称', search: false },
      {
        title: '统一社会信用代码',
        search: {
          element: (
            <Select
              options={getEnterpriseName}
              placeholder="请输入企业名称或统一社会信用代码"
              labelInValue={false}
              debounceSearch
            />
          ),
          itemProps: {
            label: '企业信息',
            transform: (val) => ({ unifiedSocialCreditCode: val, enterpriseName: undefined }),
          },
        },
        sorter: { multiple: 1 },
      },
      {
        title: '所属机构',
        dataIndex: 'applyOrganization',
        search: true,
        sorter: { multiple: 4 },
      },
      { title: '业务类型', search: true },
      { title: '黑灰标识', search: true },
      { title: '申请原因', rename: '入库原因', search: false, render: (val) => val },
      {
        title: '入库时间',
        search: true,
        sorter: { multiple: 2 },
      },
      {
        title: '计划出库时间',
        search: true,
        sorter: { multiple: 3 },
      },
      {
        title: '业务规模（万元）',
        render: (val) => val,
        sorter: { multiple: 5 },
      },
      { title: '名单来源', search: true },
      '所属集团',
      '所属集团黑灰标识',
    ]
    return getTableColumns(ALl_COLUMNS, nameColumns, true)
  }, [])
  const store = useMemo(
    () =>
      new TableStore({
        request: async (params, { sorter }) => {
          if (!Array.isArray(sorter)) sorter = [sorter]
          const orderByList = sorter
            .filter((v) => v.order)
            .map(({ order, field }) => ({
              orderByField: field,
              descFlag: order === 'descend' ? 1 : 0,
            }))
          const res = await listLibraryApi.postInfoList({
            ...params,
            orderByList,
          })
          return res
        },
      }),
    []
  )
  return (
    <Table
    columnsFilter={'query_allQuery_RecordSearch'}
            onFilter={(key,val) => saveServer('query_allQuery_RecordSearch',val)}
    
      columns={columns}
      store={store}
      selectable
      serial
      columnWidth={120}
      scroll={{ x: 'auto' }}
      actions={[<ExportAction api={listLibraryApi.getInfoExport} store={store} key="export" />]}
    ></Table>
  )
}
export default observer(Index)
