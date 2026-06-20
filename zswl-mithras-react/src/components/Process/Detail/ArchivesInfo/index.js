import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import styles from './index.less'
import Store from './store'
import { PlusSquareOutlined, MinusSquareOutlined } from '@ant-design/icons'
import { Button, Input, Tree } from 'antd'
import { Form, Page } from '@zswl/components'
import { DetailLayout } from '@/components/Layout'

const { Item } = Form
const { TextArea } = Input
const ArchivesInfo = ({ id }) => {
  const store = useMemo(() => new Store(), [])
  const { templateData, treeDataTemp } = store

  //递归过滤树
  const loopList = (arr) => {
    return arr
      ?.map((item) => {
        const { children, key, title, projName, ...rest } = item
        const obj = {
          title: title || projName,
          key,
          isLeaf: true,
        }
        if (children) {
          obj.isLeaf = false
          obj.children = loopList(children)
        }
        return obj
      })
      .filter(Boolean)
  }
  const treeData = loopList(treeDataTemp)

  const titleRender = ({ title, isLeaf, key }) => {
    return (
      <div className={styles.block}>
        <div className={styles.title}>{title}</div>
        {isLeaf && (
          <Button
            type="text"
            className={styles.btn}
            onClick={() => window.open(`/preview/reportPreview/${key}`)}
          >
            预览
          </Button>
        )}
      </div>
    )
  }

  const anchorList = [{ label: '基本信息' }]

  return (
    <Page header={null} store={store} params={{ id }}>
      <DetailLayout anchorList={anchorList} title={'流程详情'} extra={null}>
        <Form store={store.form} labelCol={{ span: 2 }} initialValues={templateData}>
          <Item label={'申请资料'} name={'bizType'} required={true}>
            <Tree
              blockNode
              switcherIcon={(e) => {
                if (e.expanded) {
                  return <MinusSquareOutlined className={styles.icon} />
                } else {
                  return <PlusSquareOutlined className={styles.icon} />
                }
              }}
              selectable={false}
              showLine={true}
              treeData={treeData}
              titleRender={titleRender}
            />
          </Item>
          <Item label={'申请原因'} name={'reason'} required={true}>
            <TextArea maxLength={800} disabled />
          </Item>
        </Form>
      </DetailLayout>
    </Page>
  )
}

export default observer(ArchivesInfo)
