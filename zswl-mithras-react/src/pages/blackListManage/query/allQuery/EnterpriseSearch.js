import { getTableColumns } from '@/utils'
import { makeAutoObservable, observer, http } from '@zswl/admin'
import { Table, TableStore, Select, App } from '@zswl/components'
import ALl_COLUMNS from '../../Column'
import { useEffect, useMemo } from 'react'
import { Radio, Space } from 'antd'
import { ExportAction } from '@/components/RiskActions'
import listLibraryApi from '@/api/blackList/listLibraryApi'
import cls from 'classnames'
import styles from './style.less'
import { getEnterpriseName } from './RecordSearch'
import { saveServer } from '@/utils'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  init = async () => {
    await this.getTypeList()
  }

  table = new TableStore({
    request: async (params) => {
      const { activeType, activeEnterprise } = this
      return await listLibraryApi.postInfoDistinct({
        ...params,
        businessType: activeType,
        cardOrgCode: activeEnterprise?.orgCode,
      })
    },
  })
  /**
   * 企业类型
   */
  typeList = []
  getTypeList = async () => {
    const res = await http.get('/black/gray/singleEnt/businessType')
    const { blackGrayBusinessTypeEnum } = App.getData().optionsType
    // this.typeList = res.map((value) => {
    //   return blackGrayBusinessTypeEnum.find((item) => item.value === value)
    // })
    this.typeList = blackGrayBusinessTypeEnum
    if (!res.includes(this.activeType)) {
      // this.setActiveType(res[0])
      res.length > 1 && this.setActiveType(res[2])
    }
  }
  activeType
  setActiveType = (val) => {
    if (!this.table.loading) {
      this.activeType = val
      // this.getEnterpriseList()
    }
  }

  /**
   * 卡片
   */
  enterpriseList = []
  getEnterpriseList = async () => {
    const { activeType } = this
    this.enterpriseList = await http.get('/black/gray/singleEnt/cardCount', {
      params: {
        businessType: activeType,
      },
    })
    if (!this.enterpriseList.find((item) => item.orgCode === this.activeEnterprise?.orgCode)) {
      this.setActiveEnterprise(this.enterpriseList[0])
    } else {
      this.table.search()
    }
  }
  activeEnterprise
  setActiveEnterprise = (val) => {
    if (!this.table.loading) {
      this.activeEnterprise = val
      this.table.search()
    }
  }
}
const store = new Store()

const Index = ({ path }) => {
  useEffect(() => {
    store.init()
  }, [])
  const columns = useMemo(() => {
    const nameColumns = [
      { title: '企业名称', search: false },
      {
        title: '统一社会信用代码',
        search: false,
      },
      {
        title: '黑灰标识',
        search: false,
      },
    ]
    const newColumns = getTableColumns(ALl_COLUMNS, nameColumns, true)
    newColumns.push({
      title: '在库明细',
      width: 100,
      actions({ unifiedSocialCreditCode }) {
        return [
          {
            name: '查看',
            to: `${path}/detail/${unifiedSocialCreditCode}?businessType=${store.activeType}&applyOrganization=${store.activeEnterprise?.orgCode}`,
          },
        ]
      },
    })
    return newColumns
  }, [])
  return (
    <div>
      <div style={{ marginBottom: 10 }}>
        <Type />
      </div>
      <Card />
      <Table
        columnsFilter={'query_allQuery_EnterpriseSearch'}
                onFilter={(key,val) => saveServer('query_allQuery_EnterpriseSearch',val)}
        
        style={{ marginTop: 20 }}
        columns={columns}
        store={store.table}
        serial
        autoRequest={false}
        searchbar={[
          {
            title: '企业信息',
            dataIndex: 'unifiedSocialCreditCode',
            element: (
              <Select
                options={getEnterpriseName}
                placeholder="请输入企业名称或统一社会信用代码"
                debounceSearch
              />
            ),
          },
        ]}
        columnWidth={120}
        rowKey={'unifiedSocialCreditCode'}
        rowSelection
        scroll={{ x: 'auto' }}
        actions={[
          <ExportAction
            api={({ ids }) => {
              const { activeType, activeEnterprise } = store
              const { unifiedSocialCreditCode } = store.table.getParams()
              return listLibraryApi.getDistinctExport({
                unifiedSocialCreditCode,
                creditCodeList: ids,
                businessType: activeType,
                cardOrgCode: activeEnterprise?.orgCode,
              })
            }}
            store={store.table}
            key="export"
          />,
        ]}
      />
    </div>
  )
}
const Type = observer(() => {
  const { typeList, activeType } = store
  return (
    <Radio.Group
      value={activeType}
      options={typeList}
      onChange={(e) => {
        store.setActiveType(e.target.value)
      }}
    />
  )
})
const Card = observer(() => {
  const { activeEnterprise, enterpriseList } = store
  return (
    <Space size={[12, 12]} wrap className={styles.card}>
      {enterpriseList.map((item) => {
        const { orgCode, orgName, blackCount, grayCount } = item
        return (
          <div
            className={cls(styles.item, activeEnterprise?.orgCode === orgCode && styles.active)}
            key={orgCode}
            onClick={() => {
              store.setActiveEnterprise(item)
            }}
          >
            <div className={styles.name}>{orgName}</div>
            <div className={styles.data}>
              <div className={styles.black}>黑 {blackCount}</div>
              <div className={styles.grey}>灰 {grayCount}</div>
            </div>
          </div>
        )
      })}
    </Space>
  )
})
export default observer(Index)
