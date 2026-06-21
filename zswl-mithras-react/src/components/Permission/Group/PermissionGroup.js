import { observer } from '@zswl/admin'
import { App, Page, Table } from '@zswl/components'
import { Button, Card, Col, List, Row, Tree } from 'antd'
import { useEffect, useState } from 'react'
import GroupModal from './GroupModal'
import style from './index.less'
import store from './store'

function PermissionGroup() {
  const detail = store.page.getData()
  const { groupList } = store
  useEffect(() => {
    return App.resetStore(store)
  }, [])
  return (
    <Page className={style.content} store={store.page}>
      <Row gutter={24} wrap={false}>
        <Col style={{ width: 480 }}>
          <Card
            size={'small'}
            title={'菜单列表'}
            bodyStyle={{ maxHeight: 600, minHeight: 200, overflowY: 'auto' }}
          >
            <Tree
              onSelect={store.onCheck}
              // onCheck={store.onCheck}
              treeData={detail.treeData || []}
            />
          </Card>
        </Col>
        <Col flex={1}>
          <Card
            size={'small'}
            title={'分组列表'}
            extra={[
              <Button onClick={store.create} key="create">
                新建分组
              </Button>,
            ]}
          >
            <List
              itemLayout="horizontal"
              dataSource={groupList}
              renderItem={(item) => (
                <List.Item
                  actions={[
                    <a key="list-loadmore-edit" onClick={() => store.edit(item.id)}>
                      编辑
                    </a>,
                    <a key="list-loadmore-more" onClick={() => store.delete(item.id)}>
                      删除
                    </a>,
                  ]}
                >
                  <List.Item.Meta title={item.name} description={item.describe} avatar={''} />
                </List.Item>
              )}
            />
          </Card>
        </Col>
      </Row>
      <GroupModal />
    </Page>
  )
}

export default observer(PermissionGroup)
