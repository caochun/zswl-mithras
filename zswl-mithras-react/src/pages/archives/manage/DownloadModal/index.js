import { observer } from '@zswl/admin'
import { Form, Modal } from '@zswl/components'
import { Button, Input, Tree } from 'antd'
import { PlusSquareOutlined, MinusSquareOutlined } from '@ant-design/icons'
import styles from './index.less'
import { useState } from 'react'
import store from '../store'
import moment from 'moment'

const { Item } = Form
const { TextArea } = Input
function DownloadModal() {
  const list = store.table.getList()
  const { rows } = store.table.getSelected()
  const [removeIds, setRemoveIds] = useState([])

  //递归过滤树
  const loopList = (arr) => {
    return arr
      .map((item) => {
        const { children, key, projName, ...rest } = item
        if (
          removeIds.includes(key) ||
          !rows.some((row) => {
            return row.key === key || row.parent?.includes(key)
          })
        ) {
          return false
        }
        const obj = {
          title: projName,
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
  // const filterList = (arr) => {
  //   return arr.filter(item => {
  //     if (!item.isLeaf && item.children.length === 0) {
  //       return false
  //     }
  //     return true
  //   }).map(item => {
  //     if (item.children) {
  //       return {
  //         ...item,
  //         children: filterList(item.children),
  //       }
  //     }
  //     return item
  //   })
  // }
  // const treeData = filterList(loopList(list))
  const treeData = loopList(list)

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
      store={store.createModal}
      okText={'提交审批'}
      destroyOnClose
      afterClose={() => {
        setRemoveIds([])
      }}
      onOk={() => {
        getValues(treeData)
        store.submit(res)
      }}
      width={600}
    >
      <Form store={store.form} labelCol={{ span: 4 }} preserve={true}>
        <Item
          label={'申请资料'}
          name={'bizType'}
          rules={[{ required: true, message: '请选择业务类型！' }]}
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

export default observer(DownloadModal)
