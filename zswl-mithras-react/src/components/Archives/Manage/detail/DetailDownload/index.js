import { observer } from '@zswl/admin'
import { App, Form, Modal } from '@zswl/components'
import { Button, Input, Tree } from 'antd'
import { PlusSquareOutlined, MinusSquareOutlined } from '@ant-design/icons'
import styles from './index.less'
import { useMemo, useState } from 'react'
import moment from 'moment'

const { Item } = Form
const { TextArea } = Input
const DetailDownloadModal = ({ store }) => {
  const { downloadTreeData } = store
  const [removeIds, setRemoveIds] = useState([])

  //递归过滤树
  const loopList = (arr) => {
    return arr
      ?.map((item) => {
        const { children, key, title, fileName, ...rest } = item
        if (removeIds.includes(key)) {
          return false
        }
        const obj = {
          title: title || fileName,
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
  const treeData = loopList(downloadTreeData)

  const titleRender = ({ title, key, isLeaf }) => {
    return (
      <div className={styles.block}>
        <div className={styles.title}>{title}</div>
        {isLeaf && (
          <Button
            type="text"
            className={styles.btn}
            onClick={() => {
              setRemoveIds(removeIds.concat(key))
            }}
          >
            删除
          </Button>
        )}
      </div>
    )
  }

  const res = []
  const getValues = (dataList) => {
    dataList?.map((item) => {
      if (!item.isLeaf) {
        getValues(item.children)
      } else {
        const obj = {}
        obj.fileId = item.key
        obj.expires = moment().add(15, 'years').format('YYYY-MM-DD HH:mm:ss')
        res.push(obj)
      }
    })
  }
  return (
    <Modal
      title={'申请下载'}
      store={store.downloadModal}
      okText={'提交审批'}
      destroyOnClose
      afterClose={() => {
        setRemoveIds([])
      }}
      onOk={() => {
        getValues(treeData)
        store.submit(res)
      }}
    >
      <Form store={store.form} labelCol={{ span: 5 }} preserve={true}>
        <Item
          label={
            <div>
              <span className={styles.requireTag}>*</span>申请资料
            </div>
          }
          name={'bizType'}
        >
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
        <Item
          label={'申请原因'}
          name={'reason'}
          rules={[{ required: true, message: '请输入申请原因！' }]}
        >
          <TextArea placeholder={'请输入'} maxLength={800} />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(DetailDownloadModal)
