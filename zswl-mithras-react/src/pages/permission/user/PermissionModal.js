import { observer } from '@zswl/admin'
import { Modal, Table } from '@zswl/components'
import { Card, Checkbox, Collapse, Popover, Tag } from 'antd'
import { saveServer } from '@/utils'

function Index({ store }) {
  const columns = [
    {
      title: '菜单', // 菜单
      width: 150,
      dataIndex: 'menuName',
      ellipsis: true,
    },
    {
      title: '功能分组', // 菜单
      width: 180,
      dataIndex: 'fgroupName',
      ellipsis: true,
    },
    {
      title: '菜单功能', // 菜单功能
      dataIndex: 'functionList',
      render: (text, record) => {
        return (
          <div className="show-content">
            {text?.map((item = {}, funcIndex) => {
              return (
                <Checkbox disabled={true} checked={item?.hasPermission} key={item.funcId}>
                  <Popover title={'功能编码'} content={<span>{item?.code}</span>}>
                    <span
                      style={{
                        color: item.type == 2 ? '#2552e6' : '#17233D',
                      }}
                    >
                      {item?.name}
                    </span>
                  </Popover>
                </Checkbox>
              )
            })}
          </div>
        )
      },
    },
  ]
  const getHavePermissionCount = (list) => {
    let count = 0
    let allCount = 0
    // 递归寻找 functionList 里面的 hasPermission
    const recursion = (data) => {
      data.forEach((item) => {
        if (item?.functionList?.length) {
          item.functionList.forEach((func) => {
            allCount++
            if (func?.hasPermission) {
              count++
            }
          })
        }
        if (item?.children?.length) {
          recursion(item.children)
        }
      })
    }
    recursion(list)
    const hasAllPermission = allCount === count
    const hasPermission = hasAllPermission ? 2 : count ? 1 : 0
    return { allCount, count, hasPermission }
  }

  return (
    <Modal title="查看权限" store={store} okText={'确定'} width={1200} destroyOnClose footer={null}>
      {(data) => {
        const node = data.map((item, index) => {
          const { hasPermission } = getHavePermissionCount(item.children)
          const rightText = hasPermission ? (
            <span style={{ color: hasPermission === 1 ? '#2552e6' : 'red' }}>{`已授权${hasPermission === 1 ? '部分' : '全部'
              }权限`}</span>
          ) : (
            <span style={{ color: '#17233D' }}>无权限</span>
          )
          return (
            <Collapse.Panel
              key={item.id}
              header={
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <span>{item.groupName}</span>
                  <span>{rightText}</span>
                </div>
              }
            >
              <Table
                columnsFilter={'permission_user_PermissionModal'}
                onFilter={(key, val) => saveServer('permission_user_PermissionModal', val)}
                className="authority-table"
                defaultExpandAllRows={true}
                columns={columns}
                dataSource={item.children}
                pagination={false}
                expandRowByClick={true}
                size="middle"
              // indentSize={0}
              // expandIconColumnIndex={1}
              />
            </Collapse.Panel>
          )
        })
        return (
          <div>
            <div
              style={{
                padding: '12px 0',
              }}
            >
              <Tag color="#2552e6">表示编辑</Tag>
              <Tag color="#17233D">表示查看</Tag>
            </div>
            <Collapse accordion>{node}</Collapse>
          </div>
        )
      }}
    </Modal>
  )
}

export default observer(Index)
