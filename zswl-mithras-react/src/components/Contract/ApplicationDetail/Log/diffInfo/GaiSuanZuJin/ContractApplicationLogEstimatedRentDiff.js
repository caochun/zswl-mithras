import { RenderColumn } from '@/components/Format'
import { Table, Form } from '@zswl/components'
import { observer, getQuery } from '@zswl/admin'
import { DatePicker, Space } from 'antd'
import moment from 'moment'
import { bizTypeMapText } from '../../../../bizTypeConfig'
import { saveServer } from '@/utils'

import styles from './index.less'

const { Item } = Form

function ContractApplicationLogEstimatedRentDiff({ detail, isLog }) {
  const bizType = getQuery('bizType')

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.title}>{`概算${bizTypeMapText[bizType]?.rentTitle}`}</div>
      </div>
      <div className={styles.subTitle}>
        {(isLog ? detail[0].estimatedLeaseDate?.value : detail[0].estimatedLeaseDate) && (
          <Space>
            <Item label={'计划起租日'}>
              <DatePicker
                placeholder={'请选择'}
                style={{ width: 200 }}
                disabled
                value={moment(
                  isLog ? detail[0].estimatedLeaseDate?.value : detail[0].estimatedLeaseDate
                )}
              />
            </Item>
          </Space>
        )}
      </div>
      <Table
              columnsFilter={'diffInfo_GaiSuanZuJin_1'}
              onFilter={(key,val) => saveServer('diffInfo_GaiSuanZuJin_1',val)}
        scroll={{ x: 1200 }}
        rowKey={'phase'}
        autoRequest={false}
        dataSource={(isLog ? detail[0].rentEstimate.lsitMap : detail.slice(1)) || []}
        columns={[
          {
            title: '日期',
            dataIndex: 'date',
            render: (val) => <RenderColumn isCompare={isLog} data={val}></RenderColumn>,
          },
          {
            title: '期项',
            dataIndex: 'phase',
            render: (val) => <RenderColumn isCompare={isLog} data={val}></RenderColumn>,
          },
          {
            title: `${bizTypeMapText[bizType]?.rentText}(元)`,
            dataIndex: 'rent',
            render: (val) => <RenderColumn isCompare={isLog} data={val} formatNum></RenderColumn>,
          },
          {
            title: '本金(元)',
            dataIndex: 'principal',
            render: (val) => <RenderColumn isCompare={isLog} data={val} formatNum></RenderColumn>,
          },
          {
            title: '利息(元)',
            dataIndex: 'interest',
            render: (val) => <RenderColumn isCompare={isLog} data={val} formatNum></RenderColumn>,
          },
          {
            title: '剩余本金(元)',
            dataIndex: 'remainingPrincipal',
            render: (val) => <RenderColumn isCompare={isLog} data={val} formatNum></RenderColumn>,
          },
        ]}
      />
    </div>
  )
}

export default observer(ContractApplicationLogEstimatedRentDiff)
