import { RenderColumn } from '@/components/Format'
import { Table, Form } from '@zswl/components'
import { observer, getQuery } from '@zswl/admin'
import { DatePicker, Space } from 'antd'
import moment from 'moment'
import { hasValue } from '@/utils'
import { saveServer } from '@/utils'
import { bizTypeMapText } from '../../../../bizTypeConfig'
import styles from './index.less'

const { Item } = Form

function Index({ detail, isLog }) {
  const bizType = getQuery('bizType')

  const columnsRender = (val, format = (v) => (hasValue(v) ? v : '-')) => {
    return isLog ? (
      <span style={{ color: val?.isChange ? 'red' : undefined }}>{format(val?.value)}</span>
    ) : (
      format(val)
    )
  }

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.title}>{`实际${bizTypeMapText[bizType]?.rentTitle}`}</div>
      </div>
      {detail && detail.length > 0 ? (
        detail.map((item, index) => {
          return (
            <div key={index}>
              <div className={styles.titleWrap}>
                <Space>
                  <div className={styles.subTitle}>
                    借据编号：{columnsRender(item.receiptCode) || '-'}
                  </div>

                  {(isLog ? item.actualStartDate.value : item.actualStartDate) && (
                    <Item label={'实际起租日'}>
                      <DatePicker
                        placeholder={'请选择'}
                        style={{ width: 200 }}
                        disabled
                        value={moment(columnsRender(item.actualStartDate))}
                      />
                    </Item>
                  )}
                </Space>
              </div>
              <Table
                columnsFilter={'diffInfo_ShiJiZuJin_1'}
                onFilter={(key,val) => saveServer('diffInfo_ShiJiZuJin_1',val)}
                scroll={{ x: 1200 }}
                rowKey={'phase'}
                autoRequest={false}
                dataSource={(isLog ? item.rentActualList.lsitMap : item.rentActualList) || []}
                columns={[
                  {
                    title: '现金流编号',
                    width: 180,
                    dataIndex: 'cashFlowCode',
                    render: (val) => <RenderColumn isCompare={isLog} data={val}></RenderColumn>,
                  },
                  {
                    title: '日期',
                    width: 180,
                    dataIndex: 'date',
                    render: (val) => <RenderColumn isCompare={isLog} data={val}></RenderColumn>,
                  },
                  {
                    title: '期项',
                    width: 180,
                    dataIndex: 'phase',
                    render: (val) => <RenderColumn isCompare={isLog} data={val}></RenderColumn>,
                  },
                  {
                    title: `${bizTypeMapText[bizType]?.rentText}(元)`,
                    width: 180,
                    dataIndex: 'rent',
                    render: (val) => (
                      <RenderColumn isCompare={isLog} data={val} formatNum></RenderColumn>
                    ),
                  },
                  {
                    title: '本金(元)',
                    dataIndex: 'principal',
                    width: 180,
                    render: (val) => (
                      <RenderColumn isCompare={isLog} data={val} formatNum></RenderColumn>
                    ),
                  },
                  {
                    title: '利息(元)',
                    dataIndex: 'interest',
                    width: 180,
                    render: (val) => (
                      <RenderColumn isCompare={isLog} data={val} formatNum></RenderColumn>
                    ),
                  },
                  {
                    title: '剩余本金(元)',
                    dataIndex: 'remainingPrincipal',
                    width: 180,
                    render: (val) => (
                      <RenderColumn isCompare={isLog} data={val} formatNum></RenderColumn>
                    ),
                  },
                ]}
              />
            </div>
          )
        })
      ) : (
        <Table
        columnsFilter={'diffInfo_ShiJiZuJin_2'}
        onFilter={(key,val) => saveServer('diffInfo_ShiJiZuJin_2',val)}
          columns={[
            {
              title: '现金流编号',
              dataIndex: 'cashFlowCode',
            },
            {
              title: '日期',
              dataIndex: 'date',
            },
            {
              title: '期项',
              dataIndex: 'phase',
            },
            {
              title: `${bizTypeMapText[bizType]?.rentText}(元)`,
              dataIndex: 'rent',
            },
            {
              title: '本金(元)',
              dataIndex: 'principal',
            },
            {
              title: '利息(元)',
              dataIndex: 'interest',
            },
            {
              title: '剩余本金(元)',
              dataIndex: 'remainingPrincipal',
            },
          ]}
        />
      )}
    </div>
  )
}

export default observer(Index)
