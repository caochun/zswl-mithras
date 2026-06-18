import { App, Page, Select, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import Store from './store'
import { useEffect, useMemo } from 'react'
import styles from './index.less'
import IconFont from '@/components/Icon'
import classNames from 'classnames'
import { FormAmount } from '@/components/Form'
import { ClientSelect, FounderSelect, OrgSelect } from '@/components'
import { DateColumn, FiledFormat, MatchOptionColumn } from '@/components/Format'
import { Cascader, DatePicker, Tooltip } from 'antd'
import useGetIndustry from '@/utils/hooks/useGetIndusty'
import { dateRangeTransform, rangePresets } from '@/utils'
import { saveServer } from '@/utils'
import { TableExportAction as TableExport } from '@/components/Actions'

const processTypeList = [
  { key: 'totalClient', name: '总客户数', params: 'TOTAL' },
  { key: 'existingClient', name: '存续客户数', params: 'EXISTING' },
  { key: 'settledClient', name: '已结清客户数', params: 'SETTLED' },
  { key: 'overdueClient', name: '当前逾期客户数', params: 'OVERDUE' },
]
function Custom({ query }) {
  const { type } = query
  const store = useMemo(() => {
    return new Store()
  }, [])
  const { page, setSelectedType, selectedType, regionList } = store
  const { industry, industryEnum } = useGetIndustry()
  const data = page.getData()
  useEffect(() => {
    store.initRegionList()
  }, [])
  // 看板跳过来
  useEffect(() => {
    if (type) {
      setSelectedType(type)
    }
  }, [type])

  const columns = useMemo(() => {
    return [
      {
        title: '客户名称',
        width: 300,
        dataIndex: 'clientName',
        fixed: 'left',
        actions({ clientName: name, id }) {
          if (type) {
            return [
              {
                name,
                onClick: () => {
                  window.open(`/lifeCycle/custom/detail/${id}`)
                },
              },
            ]
          }
          return [{ name, to: `/lifeCycle/custom/detail/${id}` }]
        },
      },
      {
        title: '授信总金额（万元）',
        dataIndex: 'applyCreditAmount',
        render: (val) => <FormAmount.Format value={val} />,
        align: 'right',
        width: 180,
      },
      {
        title: '剩余本金（万元）',
        dataIndex: 'lastPrincipal',
        align: 'right',
        render: (val) => <FormAmount.Format value={val} />,
      },
      {
        title: '存量风险敞口（万元）',
        dataIndex: 'stockRiskExposure',
        width: 180,
        align: 'right',
        render: (val) => <FormAmount.Format value={val} />,
      },
      {
        title: '地区分类',
        dataIndex: 'registerAddress',
        width: 180,
        render: (val, { provinceName, cityName, districtName }) => {
          const address = [provinceName, cityName, districtName].filter(Boolean).join('/')
          return <Tooltip title={address}>{address}</Tooltip>
        },
      },
      MatchOptionColumn({
        title: '企业标签',
        dataIndex: 'enterpriseNature',
        matchOption: 'enterpriseNatureEnum',
        width: 150,
      }),
      MatchOptionColumn({
        title: '国标行业分类',
        dataIndex: 'industryType',
        matchOption: industryEnum,
        width: 180,
      }),
      MatchOptionColumn({
        title: '风控行业分类',
        dataIndex: 'riskControlIndustryClassify',
        width: 180,
      }),
      { title: '所属主办', width: 150, dataIndex: 'belongSponsorName' },
      { title: '所属部门', width: 150, dataIndex: 'belongDeptName' },
      { title: '创建人', dataIndex: 'creatorName' },
      { title: '创建时间', dataIndex: 'createTime', width: 180 },
    ]
  }, [industryEnum])

  const currentProcessTypeList = useMemo(() => {
    if (JSON.stringify(data) === '{}') {
      return processTypeList
    }
    return processTypeList.map((item) => {
      return {
        ...item,
        num: data[`${item.key}Num`],
        add: data[`${item.key}NumMonthIncrease`] ?? 0,
      }
    })
  }, [data])

  return (
    <Page store={store.page} className={styles.page}>
      <div className={styles.wrap}>
        <div className={styles.processTypeList}>
          <div className={styles.bgColor}>
            {currentProcessTypeList.map((item, index) => {
              const { num, name, add, params } = item
              return (
                <div
                  key={index}
                  className={classNames(styles.item, {
                    [styles.selected]: params === selectedType,
                  })}
                  onClick={() => setSelectedType(params)}
                >
                  <div className={styles.itemTitle}>{name}</div>
                  <div className={styles.count}>{num}</div>
                  {index != 3 && (
                    <div className={styles.desc}>
                      <div className={styles.text}>本月新增</div>
                      <div className={styles.num}>{add}</div>
                      <IconFont type="icon-arrow_up" />
                    </div>
                  )}
                </div>
              )
            })}
          </div>
        </div>
        <div className={styles.tableWrap}>
          <Table
            columnsFilter={'lifeCycle_custom_1'}
            onFilter={(key, val) => saveServer('lifeCycle_custom_1', val)}
            resizable
            store={store.table}
            rowKey="key"
            columnWidth={150}
            extra={
              <TableExport
                table={store.table}
                otherExcelProps={{
                  fileName: '客户全周期列表',
                }}
              />
            }
            searchbar={{
              labelCol: { span: 6 },
              items: [
                {
                  label: '客户名称',
                  name: 'clientName',
                },
                {
                  label: '所属主办',
                  name: 'sponsorId',
                  element: <FounderSelect params={{ job: 'projmanager' }} />,
                },
                {
                  label: '创建时间',
                  dataIndex: 'createDate',
                  element: (
                    <DatePicker.RangePicker
                      renderExtraFooter={(panelNode) => {}}
                      ranges={rangePresets}
                    />
                  ),
                  itemProps: {
                    transform: (val) =>
                      dateRangeTransform(
                        val,
                        `createDateFrom`,
                        `createDateTo`,
                        'yyyy-MM-DD',
                        'createDate'
                      ),
                  },
                },
                {
                  label: '地区分类',
                  name: 'registerAddress',
                  element: (
                    <Cascader
                      getPopupContainer={() => document.body}
                      options={regionList}
                      changeOnSelect
                      placeholder="请选择"
                      loadData={store.loadRegionListChild}
                    />
                  ),
                },
                {
                  title: '企业标签',
                  name: 'enterpriseNature',
                  element: (
                    <Select
                      style={{ width: '100%' }}
                      options={'enterpriseNatureEnum'}
                      getPopupContainer={() => document.body}
                    />
                  ),
                  width: 150,
                },
                {
                  title: '国标行业分类',
                  dataIndex: 'industryType',
                  width: 180,
                  element: <Cascader options={industry} getPopupContainer={() => document.body} />,
                },
                {
                  title: '风控行业分类',
                  dataIndex: 'riskControlIndustryClassify',
                  element: (
                    <Select
                      style={{ width: '100%' }}
                      options={'riskControlIndustryClassify'}
                      getPopupContainer={() => document.body}
                    />
                  ),
                  width: 180,
                },
                {
                  label: '所属部门',
                  name: 'deptId',
                  element: <OrgSelect functionCode="selectorgs-groupCreditReview" />,
                },
                {
                  label: '创建人',
                  name: 'createBy',
                  element: <FounderSelect params={{ job: 'projmanager' }} />,
                },
              ],
            }}
            scroll={{
              x: 1200,
            }}
            columns={columns}
          />
        </div>
      </div>
    </Page>
  )
}

export default observer(Custom)
