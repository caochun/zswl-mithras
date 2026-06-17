import React, { useEffect } from 'react'
import { observer } from '@zswl/admin'
import { SettingOutlined } from '@ant-design/icons'
import { SearchBar, Select, Button, App } from '@zswl/components'
import { DatePicker, Table, Tooltip, Empty, Checkbox, Spin, Radio, Space, Popover, Row } from 'antd'
import { subjectQuarterType, subjectReportType } from './columns'
import styles from './index.less'
import DeteleIcon from './DeteleIcon'
import CommonSearch, { defaultParams } from './CommonSearch'
import { saveServer } from '@/utils'

const { Item } = SearchBar
function Index({ store }) {
  useEffect(() => {
    //store.getFinanceList({ subjectType: 'GOV_CAPITAL_BALANCE' })
    store.govSearchBar.search()
  }, [])

  const handleRemove = () => {
    store.govSearchBar.search()
  }

  const { displayDimensions = [] } = store.govSearchBar.getParams()

  const content = (
    <div>
      <p>金额单位</p>
      <div>
        <Radio.Group
          defaultValue={10000}
          onChange={(e) => {
            store.unitChange(e, 'GOV_CAPITAL_BALANCE')
          }}
        >
          <Space direction="vertical">
            <Radio value={1}>元</Radio>
            <Radio value={10000}>万元</Radio>
            <Radio value={100000000}>亿元</Radio>
          </Space>
        </Radio.Group>
      </div>
      <p style={{ marginTop: '16px' }}>小数保留位数</p>
      <div>
        <Radio.Group defaultValue={2}>
          <Space
            direction="vertical"
            onChange={(e) => {
              store.precisionChange(e, 'GOV_CAPITAL_BALANCE')
            }}
          >
            <Radio value={0}>0</Radio>
            <Radio value={1}>0.0</Radio>
            <Radio value={2}>0.00</Radio>
            <Radio value={3}>0.000</Radio>
            <Radio value={4}>0.0000</Radio>
          </Space>
        </Radio.Group>
      </div>
    </div>
  )
  const { financeList = {}, loading } = store
  // console.log({ financeList })
  let firstList = []
  Object.keys(financeList).length > 0 &&
    financeList[0].itemList.forEach((m, z) => {
      firstList.push({
        ...m,
        quarter: financeList[0].quarter,
        reportType: financeList[0].reportType,
        year: financeList[0].year,
        subjectType: financeList[0].subjectType,
      })
    })
  const tableTitle = (target) => {
    return [
      {
        title: '报表类型',
        dataIndex: 'reportType',
        children: [
          {
            title: '报告期',
            children: [
              {
                title: '年份',
                children: [
                  {
                    title: '科目代码',
                    dataIndex: 'subjectCode',
                    render: (v) => {
                      return <div style={{ width: '80px' }}>{v}</div>
                    },
                  },
                  {
                    title: '科目',
                    dataIndex: 'subjectName',
                    render: (v) => {
                      return (
                        <Tooltip title={v}>
                          <div style={{ width: '160px' }} className={styles.subjectName}>
                            {v}
                          </div>
                        </Tooltip>
                      )
                    },
                  },
                ],
              },
            ],
          },
        ],
      },
      {
        title: subjectReportType[target[0].reportType],
        dataIndex: 'reportType',
        children: [
          {
            //title: subjectQuarterType[target[0].quarter],
            title: App.matchOption('subjectQuarterType', String(target[0].quarter)).label,
            children: [
              {
                title: target[0].year,
                children: [
                  {
                    title: '基础',
                    dataIndex: 'subjectValueStr',
                    align: 'right',
                    render: (items) => {
                      if (items) {
                        return <div style={{ width: '140px' }}>{items}</div>
                      }
                      return <div style={{ width: '140px' }}>-</div>
                    },
                  },
                  displayDimensions.includes('PERCENT') && {
                    title: '百分比',
                    dataIndex: 'subjectPercentStr',
                    align: 'right',
                    render: (items) => {
                      if (items) {
                        return <div style={{ width: '100px' }}>{items}</div>
                      }
                      return <div style={{ width: '100px' }}>-</div>
                    },
                  },
                  displayDimensions.includes('OVER_YEAR') && {
                    title: '同比',
                    dataIndex: 'subjectOverYearStr',
                    align: 'right',
                    width: 140,
                    render: (items) => {
                      if (items) {
                        return <div style={{ width: '100px' }}>{items}</div>
                      }
                      return <div style={{ width: '100px' }}>-</div>
                    },
                  },
                ].filter(Boolean),
              },
            ],
          },
        ],
      },
    ]
  }
  return (
    <>
      <div className={styles.searchWrap}>
        <div className={styles.searchBar}>
          <SearchBar
            store={store.govSearchBar}
            layout={'inline'}
            trigger={false}
            gutter={[4, 4]}
            searchButton={false}
            resetButton={false}
            limit={10}
            initialValues={{
              reportType: 'ALL',
              displayDimensions: ['BASE'],
              ...defaultParams,
            }}
          >
            <Row>
              <CommonSearch searchStore={store.govSearchBar} />

              <Item label={'报表类型'} name={'reportType'}>
                <Select options={'subjectReportType'} />
              </Item>
              <Item label={'维度'} name={'displayDimensions'}>
                <Select
                  style={{ minWidth: '100px' }}
                  mode="multiple"
                  options={'subjectItemDisplayDimension'}
                  placeholder="可多选"
                />
              </Item>
              <Item>
                <Button type={'primary'} onClick={store.govSearchBar.search}>
                  查询
                </Button>
              </Item>
            </Row>
          </SearchBar>
        </div>
        <div className={styles.settingOutlined}>
          <Popover placement="bottomRight" title="数字格式" content={content}>
            <SettingOutlined />
          </Popover>
        </div>
      </div>
      <div>
        <Spin spinning={loading}>
          <div className={styles.tableWrap}>
            <div className={styles.childWrap}>
              {Object.keys(financeList).length > 0 && (
                <div className={styles.tableCell}>
                  <Table
                                      columnsFilter={'maintain_detail_Financial_Gov_1'}
                                      onFilter={(key,val) => saveServer('maintain_detail_Financial_Gov_1',val)}
                    bordered
                    size={'small'}
                    columns={tableTitle(firstList)}
                    dataSource={firstList.length > 0 && firstList}
                    pagination={false}
                  />
                  <div className={styles.tableCell_remove}>
                    <DeteleIcon
                      store={store}
                      dataSource={financeList}
                      index={0}
                      handleRemove={handleRemove}
                    ></DeteleIcon>
                  </div>
                </div>
              )}
            </div>
            {Object.keys(financeList).length > 0 &&
              financeList.slice(1).map((obj, i) => {
                return (
                  <div className={styles.tableCell} key={i}>
                    <Table
                                      columnsFilter={'maintain_detail_Financial_Gov_2'}
                                      onFilter={(key,val) => saveServer('maintain_detail_Financial_Gov_2',val)}
                      bordered
                      size={'small'}
                      pagination={false}
                      columns={[
                        {
                          title: subjectReportType[obj.reportType],
                          dataIndex: 'reportType',
                          children: [
                            {
                              title: App.matchOption('subjectQuarterType', String(obj.quarter))
                                .label,
                              // title: subjectQuarterType[obj.quarter],
                              children: [
                                {
                                  title: obj.year,
                                  children: [
                                    {
                                      title: '基础',
                                      align: 'right',
                                      dataIndex: 'subjectValueStr',
                                      render: (items) => {
                                        if (items) {
                                          return <div style={{ width: '140px' }}>{items}</div>
                                        }
                                        return <div style={{ width: '140px' }}>-</div>
                                      },
                                    },
                                    displayDimensions.includes('PERCENT') && {
                                      title: '百分比',
                                      dataIndex: 'subjectPercentStr',
                                      align: 'right',
                                      render: (items) => {
                                        if (items) {
                                          return <div style={{ width: '100px' }}>{items}</div>
                                        }
                                        return <div style={{ width: '100px' }}>-</div>
                                      },
                                    },
                                    displayDimensions.includes('OVER_YEAR') && {
                                      title: '同比',
                                      dataIndex: 'subjectOverYearStr',
                                      align: 'right',
                                      render: (items) => {
                                        if (items) {
                                          return <div style={{ width: '100px' }}>{items}</div>
                                        }
                                        return <div style={{ width: '100px' }}>-</div>
                                      },
                                    },
                                  ].filter(Boolean),
                                },
                              ],
                            },
                          ],
                        },
                      ]}
                      dataSource={obj.itemList}
                    />
                    <div className={styles.tableCell_remove}>
                      <DeteleIcon
                        store={store}
                        dataSource={financeList}
                        index={i + 1}
                        handleRemove={handleRemove}
                      ></DeteleIcon>
                    </div>
                  </div>
                )
              })}
          </div>
        </Spin>
        {Object.keys(financeList).length == 0 && <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />}
      </div>
    </>
  )
}

export default observer(Index)
