import React, { useEffect } from 'react'
import { observer } from '@zswl/admin'
import { SearchBar, Select, Button, App } from '@zswl/components'
import { SettingOutlined } from '@ant-design/icons'
// import store from './store'
import { DatePicker, Table, Tooltip, Empty, Spin, Checkbox, Radio, Space, Popover, Row } from 'antd'
import { subjectQuarterType, subjectReportType } from './columns'
import styles from './index.less'
import { saveServer } from '@/utils'

const { Item } = SearchBar

function Index({ canEdit, store }) {
  useEffect(() => {
    //store.getFinanceList({ subjectType: 'BIZ_INDEX' })
    store.bizSearchBar.search()
  }, [])
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
  const { financeList = {}, loading } = store
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
              quarter: '4',
              reportType: 'ALL',
              displayDimensions: ['BASE'],
              latest: true,
            }}
          >
            <Row>
              <Item label={'报告期'} name={'quarter'}>
                <Select
                  options="subjectQuarterType"
                  style={{ minWidth: '118px' }}
                  disabled={!canEdit}
                />
              </Item>
              <Item label={'最新'} name={'latest'} valuePropName="checked">
                <Checkbox disabled={!canEdit} />
              </Item>
              <Item label={'时间选择'} name={'year'}>
                <DatePicker.RangePicker
                  picker={'year'}
                  style={{ width: '186px' }}
                  disabled={!canEdit}
                />
              </Item>
              <Item label={'报表类型'} name={'reportType'}>
                <Select options={'subjectReportType'} disabled={!canEdit} />
              </Item>
              <Item label={'维度'} name={'displayDimensions'}>
                <Select
                  mode="multiple"
                  options={'subjectItemDisplayDimension'}
                  placeholder="可多选"
                  disabled={true}
                  // value={'基础数据'} disabled={!canEdit}
                />
              </Item>
              <Item>
                {canEdit && (
                  <Button type={'primary'} onClick={store.bizSearchBar.search} disabled={!canEdit}>
                    查询
                  </Button>
                )}
                {canEdit && (
                  <Button
                    type={'primary'}
                    onClick={store.save}
                    style={{ widht: '80px', marginLeft: 8 }}
                    disabled={!canEdit}
                  >
                    保存
                  </Button>
                )}
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
                <Table
                  columnsFilter={'components_Financial_Biz_1'}
                          onFilter={(key,val) => saveServer('components_Financial_Biz_1',val)}
                  
                  bordered
                  size={'small'}
                  columns={tableTitle(firstList)}
                  dataSource={firstList.length > 0 && firstList}
                  pagination={false}
                />
              )}
            </div>
            {Object.keys(financeList).length > 0 &&
              financeList.slice(1).map((obj, i) => {
                return (
                  <div className={styles.childWrap} key={i}>
                    <Table
                      bordered
                      size={'small'}
                      columnsFilter={'components_Financial_Biz_2'}
                      onFilter={(key,val) => saveServer('components_Financial_Biz_2',val)}

                      pagination={false}
                      columns={[
                        {
                          title: subjectReportType[obj.reportType],
                          dataIndex: 'reportType',
                          align: 'center',
                          children: [
                            {
                              //title: subjectQuarterType[obj.quarter],
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
                                    // {
                                    //   title: '百分比（%）',
                                    //   align: 'right',
                                    //   dataIndex: 'subjectPercentStr',
                                    //   render: (items) => {
                                    //     if (items) {
                                    //       return <div style={{ width: '100px' }}>{items}</div>
                                    //     }
                                    //     return <div style={{ width: '100px' }}>-</div>
                                    //   },
                                    // },
                                    // {
                                    //   title: '同比（%）',
                                    //   align: 'right',
                                    //   dataIndex: 'subjectOverYearStr',
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
