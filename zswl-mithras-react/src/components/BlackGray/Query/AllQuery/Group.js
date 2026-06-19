import { getTableColumns } from '@/utils'
import { makeAutoObservable, observer } from '@zswl/admin'
import { Table, TableStore, Select, App, SearchBar } from '@zswl/components'
import ALl_COLUMNS from '../../Columns'
import { useEffect, useMemo } from 'react'
import { Radio } from 'antd'
import { ExportAction } from '../../actions'
import { getEnterpriseName } from './RecordSearch'
import { saveServer } from '@/utils'
import listLibraryApi from '@/api/blackGray/listLibraryApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  table = new TableStore({
    request: (params) => {
      return listLibraryApi.getSingleGroupList(params)
    },
  })
  typeList = []
  getTypeList = async () => {
    const res = await listLibraryApi.getSingleEntBusinessType()
    const { blackGrayBusinessTypeEnum } = App.getData().optionsType
    this.typeList = res.map((value) => {
      return blackGrayBusinessTypeEnum.find((item) => item.value === value)
    })
    const { businessType } = this.table.getParams()
    if (!businessType || !this.typeList.find((item) => item.value === businessType)) {
      this.table.setParams({ businessType: this.typeList[0].value })
    }
    this.table.search()
  }
}
const store = new Store()

const Index = ({ path }) => {
  const { typeList } = store
  const columns = useMemo(() => {
    const nameColumns = [
      { title: '集团名称' },
      {
        title: '统一社会信用代码',
        search: false,
        dataIndex: 'groupCreditCode',
      },
      {
        title: '黑灰标识',
        search: false,
      },
      {
        title: '下属企业在库数',
      },
    ]
    const newColumns = getTableColumns(ALl_COLUMNS, nameColumns, true)
    newColumns.push({
      title: '在库明细',
      width: 100,
      actions(row) {
        const { businessType } = store.table.getParams()
        return [
          {
            name: '查看',
            to: `${path}/groupDetail/${row.groupName}?businessType=${businessType}`,
          },
        ]
      },
    })
    return newColumns
  }, [])
  useEffect(() => {
    store.getTypeList()
  }, [])
  return (
    <div>
      <SearchBar store={store.table} layout="inline">
        <SearchBar.Item dataIndex="businessType">
          <Radio.Group options={typeList} />
        </SearchBar.Item>
        <SearchBar.Item title="集团信息" dataIndex="groupNameCreditCode" style={{ marginTop: 10 }}>
          <Select
            style={{ width: 260 }}
            options={getEnterpriseName}
            placeholder="请输入集团名称或统一社会信用代码"
            debounceSearch
          />
        </SearchBar.Item>
      </SearchBar>
      <Table
        columnsFilter={'query_allQuery_Group'}
        onFilter={(key, val) => saveServer('query_allQuery_Group', val)}
        style={{ marginTop: 20 }}
        columns={columns}
        store={store.table}
        autoRequest={false}
        serial
        columnWidth={120}
        rowKey={'groupName'}
        selectable
        actions={[
          <ExportAction
            api={({ ids, ...rest }) =>
              listLibraryApi.getSingleGroupExport({ exportGroupNames: ids, ...rest })
            }
            store={store.table}
            key="export"
          />,
        ]}
      />
    </div>
  )
}
export default observer(Index)
