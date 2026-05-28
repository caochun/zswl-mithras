import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import styles from './index.less'
import Store from './store'
import { PlusSquareOutlined, MinusSquareOutlined } from '@ant-design/icons'
import { Button, Input, Tree } from 'antd'
import { Form, Page } from '@zswl/components'
import DetailLayout from '@/components/DetailLayout'

const { Item } = Form
const ArchivesTemplate = ({ id }) => {
  const store = useMemo(() => new Store(), [])
  const { templateData, treeDataTemp } = store

  //递归过滤树
  const loopList = (arr) => {
    return arr
      ?.map((item) => {
        const { children, key, title, projName, isGrey, size, ...rest } = item
        const obj = {
          title: title || projName,
          key,
          isLeaf: true,
          isGrey,
          size,
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

  const titleRender = ({ title, isLeaf, key, isGrey, size }) => {
    return (
      <div className={styles.block}>
        <div className={styles.title} style={{ color: `${isGrey ? 'grey' : '#000000D9'}` }}>
          {title}
          {isLeaf ? null : `（${size}）`}
        </div>
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
          <Item label={'归档类型'} name={'templateName'} required={true}>
            <Input disabled style={{ width: '50%' }} />
          </Item>
          <Item label={'归档资料'} name={'templateData'} required={true}>
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
        </Form>
      </DetailLayout>
    </Page>
  )
}

export default observer(ArchivesTemplate)
