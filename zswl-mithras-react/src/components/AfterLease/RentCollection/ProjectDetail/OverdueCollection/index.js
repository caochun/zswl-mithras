import { App, Form, Button, Table } from '@zswl/components'

// import store from '../store'

import { message, Select, Tooltip, Upload } from 'antd'

import styles from '../index.less'
import { useEffect, useState } from 'react'
import { observer } from '@zswl/admin'
import NoOverdue from '../../Components/NoOverdue'
import { saveServer } from '@/utils'

const OverdueCollection = ({ store }) => {
  const { canEdit, isProcess } = store.page.getParams()
  const { receiptList } = store
  const { collectionLevel, fileList: list } = receiptList || {}
  const [fileList, setFileList] = useState([])
  const [isLoading, setIsLoading] = useState(false)

  const deleteButton = (id) => (
    <div
      className={styles.item}
      onClick={() => {
        store.remove(id)
      }}
    >
      删除
    </div>
  )
  const columns = [
    {
      title: '资料名称',
      dataIndex: 'filename',
      width: 250,
      render: (val) => <Tooltip title={val}>{val}</Tooltip>,
    },
    {
      title: '上传人',
      dataIndex: 'createByName',
    },
    {
      title: '上传时间',
      dataIndex: 'createTime',
    },
    {
      title: '操作',
      dataIndex: 'id',
      width: 220,
      fixed: 'right',
      render: (id, { filename }) => {
        if (id.toString().indexOf('folder') > -1) {
          return null
        }
        return (
          <div className={styles.control}>
            <div className={styles.item} onClick={() => preview(id)}>
              预览
            </div>
            <Button
              // className={styles.item}
              type="link"
              style={{ padding: 0 }}
              onClick={() => {
                store.download({
                  filename,
                  fileId: id,
                  moduleType: 'OVERDUE_COLLECTION_REDUCTION',
                  materialsType: 'OVERDUE_COLLECTION',
                })
              }}
            >
              下载
            </Button>
            {!isProcess ? deleteButton(id) : canEdit ? deleteButton(id) : null}
          </div>
        )
      },
    },
  ]
  const preview = (id) => {
    window.open(`/preview/reportPreview/${id}`)
  }
  const uploadProps = {
    name: 'file',
    beforeUpload: (val) => {
      setFileList([val])
      setIsLoading(true)
      return false
    },
    // onChange: (info, lists, event) => {
    //   console.log('info:', info)
    //   console.log('lists:', lists)
    //   console.log('event:', event)

    //   // if (info?.fileList?.length) {
    //   //   setIsLoading(false)
    //   // }
    // },

    onRemove: () => {
      setFileList([])
    },
    fileList: [],
  }

  useEffect(async () => {
    if (fileList.length !== 0) {
      const res = await store.upload(fileList[0]).finally(() => {
        setIsLoading(false)
      })
      // console.log('res32:', res)
    }
  }, [fileList])
  if (!collectionLevel) {
    return <NoOverdue />
  }
  return (
    <>
      <div className={styles.nav} style={{ marginBottom: 20 }}>
        <div className={styles.title}>催收层级：{collectionLevel || '-'}</div>
        <div className={styles.btnWrap}>
          <Upload {...uploadProps}>
            <Button
              type="primary"
              loading={isLoading}
              style={{ marginRight: 8 }}
              disabled={isProcess ? !canEdit : false}
            >
              上传附件
            </Button>
          </Upload>
        </div>
      </div>
      <Table onFilter={(key,val) => saveServer('ProjectDetail_OverdueCollection_1',val)} columnsFilter={'ProjectDetail_OverdueCollection_1'} dataSource={list} columns={columns} />
    </>
  )
}

export default observer(OverdueCollection)
