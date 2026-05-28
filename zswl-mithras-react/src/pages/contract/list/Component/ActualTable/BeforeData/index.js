import { observer } from '@zswl/admin'
import { Drawer, Dropdown, Menu } from 'antd'
import { Table, Button } from '@zswl/components'
import { DownOutlined } from '@ant-design/icons'
import RenderColumn from '@/components/RenderColumn'
import { bizTypeMapText } from '@/pages/contract/list/bizType.config'
import styles from '../index.less'
import { saveServer } from '@/utils'

const Index = ({ bizType, baseStore }) => {
  const { showDrawer, setShowDrawer, beforeDetailList, changeList, handleMenuClick } = baseStore

  return (
    <Drawer
      title={`实际${bizTypeMapText[bizType]?.rentTitle}`}
      placement="left"
      onClose={() => {
        setShowDrawer(false)
      }}
      width={'70%'}
      open={showDrawer}
    >
      <div className={styles.pages}>
        {beforeDetailList?.length > 0 ? (
          beforeDetailList.map((item, index) => {
            return (
              <div className={styles.page} key={index}>
                <div className={styles.titleWrap}>
                  <div className={styles.subTitle}>
                    借据编号：
                    {
                      <span style={{ color: changeList[index]?.receiptCode ? 'red' : undefined }}>
                        {item.receiptCode || '-'}
                      </span>
                    }
                  </div>
                  {
                    <Dropdown
                      overlay={
                        <Menu
                          onClick={(e) => handleMenuClick(e, item, 'HISTORY')}
                          items={[
                            {
                              label: `导出${bizTypeMapText[bizType]?.rentTitle}`,
                              key: '1',
                              disabled: !item.receiptId,
                            },
                            {
                              label: '导出现金流表',
                              key: '2',
                              disabled: !item.receiptId,
                            },
                          ]}
                        />
                      }
                    >
                      <Button>
                        导出
                        <DownOutlined />
                      </Button>
                    </Dropdown>
                  }
                </div>
                <Table
                  rowKey={'phase'}
                  dataSource={item.rentActualList || []}
                  columnsFilter={'ActualTable_BeforeData_1'}
                  onFilter={(key, val) => saveServer('ActualTable_BeforeData_1', val)}
                  columns={[
                    {
                      title: '现金流编号',
                      width: 280,
                      dataIndex: 'cashFlowCode',
                      render(val, t) {
                        return <RenderColumn data={val}></RenderColumn>
                      },
                    },
                    {
                      title: '日期',
                      width: 200,
                      dataIndex: 'date',
                      render(val, t) {
                        return <RenderColumn data={val}></RenderColumn>
                      },
                    },
                    {
                      title: '期项',
                      width: 180,
                      dataIndex: 'phase',
                      render(val, t) {
                        return <RenderColumn data={val}></RenderColumn>
                      },
                    },
                    {
                      title: `${bizTypeMapText[bizType]?.rentText}(元)`,
                      width: 180,
                      dataIndex: 'rent',
                      align: 'right',
                      render(val, t) {
                        return <RenderColumn data={val} formatNum></RenderColumn>
                      },
                    },
                    {
                      title: '本金(元)',
                      align: 'right',
                      dataIndex: 'principal',
                      width: 180,
                      render(val, t) {
                        return <RenderColumn data={val} formatNum></RenderColumn>
                      },
                    },
                    {
                      title: '利息(元)',
                      align: 'right',
                      dataIndex: 'interest',
                      width: 180,
                      render(val, t) {
                        return <RenderColumn data={val} formatNum></RenderColumn>
                      },
                    },
                    {
                      title: '剩余本金(元)',
                      align: 'right',
                      dataIndex: 'remainingPrincipal',
                      width: 180,
                      render(val, t) {
                        return <RenderColumn data={val} formatNum></RenderColumn>
                      },
                    },
                  ]}
                />
              </div>
            )
          })
        ) : (
          <>
            <div className={styles.btnWrap}>
              <div className={styles.title}>{'实际租金表'}</div>
            </div>
            <Table
              columnsFilter={'ActualTable_BeforeData_2'}
              onFilter={(key, val) => saveServer('ActualTable_BeforeData_2', val)}
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
          </>
        )}
      </div>
    </Drawer>
  )
}

export default observer(Index)
