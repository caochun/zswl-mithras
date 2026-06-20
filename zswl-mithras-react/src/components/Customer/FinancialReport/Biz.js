import React, { useEffect } from 'react'
import { observer } from '@zswl/admin'
import { SearchBar, Select, Button, App } from '@zswl/components'
import { SettingOutlined } from '@ant-design/icons'
import { DatePicker, Table, Tooltip, Empty, Spin, Checkbox, Radio, Space, Popover, Row } from 'antd'
import { subjectQuarterType, subjectReportType } from './columns'
import styles from './index.less'
import DeleteIcon from './DeleteIcon'
import CommonSearch, { defaultParams } from './CommonSearch'
import { saveServer } from '@/utils'

const { Item } = SearchBar

function Index ({ store }) {
  useEffect(() => {
    //store.getFinanceList({ subjectType: 'BIZ_INDEX' })
    store.bizSearchBar.search()
  }, [])

  const handleRemove = () => {
    store.bizSearchBar.search()
  }
  const { financeList = {}, loading } = store
  const content = (
    <div>
      <p>金额单位</p>
      <div>
        <Radio.Group
          defaultValue={10000}
          onChange={(e) => {
            store.unitChange(e, 'BIZ_INDEX')
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
              store.precisionChange(e, 'BIZ_INDEX')
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
                    align: 'center',
                    render: (v, i) => {
                      return <div style={{ width: '80px' }}>{v}</div>
                    },
                  },
                  {
                    title: '科目',
                    dataIndex: 'subjectName',
                    align: 'center',
                    render: (v, i) => {
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
        align: 'center',
        children: [
          {
            //title: subjectQuarterType[target[0].quarter],
            title: App.matchOption('subjectQuarterType', String(target[0].quarter)).label,
            align: 'center',
            children: [
              {
                title: target[0].year,
                align: 'center',
                children: [
                  {
                    title: '基础',
                    align: 'center',
                    dataIndex: 'subjectValueStr',
                    render: (items) => {
                      if (items) {
                        return <div style={{ width: '140px', textAlign: 'right' }}>{items}</div>
                      }
                      return <div style={{ width: '140px', textAlign: 'right' }}>-</div>
                    },
                  },
                  // {
                  //   title: '百分比（%）',
                  //   dataIndex: 'subjectPercentStr',
                  //   align: 'right',
                  //   render: (items) => {
                  //     if (items) {
                  //       return <div style={{ width: '100px' }}>{items}</div>
                  //     }
                  //     return <div style={{ width: '100px' }}>-</div>
                  //   },
                  // },
                  // {
                  //   title: '同比（%）',
                  //   dataIndex: 'subjectOverYearStr',
                  //   width: 140,
                  //   align: 'right',
                  //   render: (items) => {
                  //     if (items) {
                  //       return <div style={{ width: '100px' }}>{items}</div>
                  //     }
                  //     return <div style={{ width: '100px' }}>-</div>
                  //   },
                  // },
                ],
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
            store={store.bizSearchBar}
            layout={'inline'}
            gutter={[4, 4]}
            limit={10}
            searchButton={false}
            resetButton={false}
            trigger={false}
            initialValues={{
              reportType: 'ALL',
              displayDimensions: ['BASE'],
              ...defaultParams,
            }}
          >
            <Row>
              <CommonSearch searchStore={store.bizSearchBar} />

              <Item label={'报表类型'} name={'reportType'}>
                <Select options={'subjectReportType'} />
              </Item>
              <Item label={'维度'} name={'displayDimensions'}>
                <Select
                  mode="multiple"
                  options={'subjectItemDisplayDimension'}
                  placeholder="可多选"
                  disabled={true}
                // value={'基础数据'}
                />
              </Item>
              <Item>
                <Button type={'primary'} onClick={store.bizSearchBar.search}>
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
                    columnsFilter={'maintain_detail_Financial_Biz_1'}
                    onFilter={(key, val) => saveServer('maintain_detail_Financial_Biz_1', val)}
                    bordered
                    size={'small'}
                    columns={tableTitle(firstList)}
                    dataSource={firstList.length > 0 && firstList}
                    pagination={false}
                  />
                     <div className={styles.tableCell_remove}>
                      <DeleteIcon
                        store={store}
                        dataSource={financeList}
                        index={0}
                        handleRemove={handleRemove}
                      ></DeleteIcon>
                    </div>
                </div>
              )}
            </div>
            {Object.keys(financeList).length > 0 &&
              financeList.slice(1).map((obj, i) => {
                return (
                  <div className={styles.tableCell} key={i}>
                    <Table
                      columnsFilter={'maintain_detail_Financial_Biz_2'}
                      onFilter={(key, val) => saveServer('maintain_detail_Financial_Biz_2', val)}
                      bordered
                      size={'small'}
                      pagination={false}
                      columns={[
                        {
                          title: subjectReportType[obj.reportType],
                          dataIndex: 'reportType',
                          align: 'center',
                          children: [
                            {
                              title: App.matchOption('subjectQuarterType', String(obj.quarter))
                                .label,
                              align: 'center',
                              children: [
                                {
                                  title: obj.year,
                                  align: 'center',
                                  children: [
                                    {
                                      title: '基础',
                                      align: 'center',
                                      dataIndex: 'subjectValueStr',
                                      render: (items) => {
                                        if (items) {
                                          return (
                                            <div style={{ width: '140px', textAlign: 'right' }}>
                                              {items}
                                            </div>
                                          )
                                        }
                                        return (
                                          <div style={{ width: '140px', textAlign: 'right' }}>
                                            -
                                          </div>
                                        )
                                      },
                                    },

                                  ],
                                },
                              ],
                            },
                          ],
                        },
                      ]}
                      dataSource={obj.itemList}
                    />
                 
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
