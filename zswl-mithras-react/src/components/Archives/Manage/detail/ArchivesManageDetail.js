import { useMemo, useState } from 'react'
import { Anchor, Space, Input, Upload, Divider, Tag, Tooltip, message } from 'antd'
import { observer } from '@zswl/admin'
import { Page, Modal, Table, App, SearchBar, Button } from '@zswl/components'
import styles from '../index.less'
import DataStore from './store'
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons'
import DetailDownload from './DetailDownload'
import { getFileType } from '@/utils'
import { saveServer } from '@/utils'

const { Link } = Anchor
const { Dragger } = Upload
const { Search } = Input
const { Item } = SearchBar

const ArchivesDetail = ({ params: { id }, query }) => {
  const store = useMemo(() => {
    return new DataStore()
  }, [])
  const { data, tableStores, uploadData } = store

  //详情申请下载按钮disable
  const haveData = () => {
    const temp = []
    Object.keys(tableStores).forEach((key) => {
      const { rows } = tableStores[key]?.getSelected()
      rows && rows?.map((v) => temp.push(v))
    })
    return !temp.length
  }

  const columns = [
    {
      title: '文件类型',
      dataIndex: 'fileType',
      width: 120,
    },
    {
      title: '文件名称',
      dataIndex: 'fileName',
      width: 200,
    },
    {
      title: '上传人',
      dataIndex: 'uploadUser',
    },
    {
      title: '上传时间',
      dataIndex: 'uploadTime',
      width: 180,
    },
    {
      title: '借阅状态',
      dataIndex: 'status',
      render(val) {
        const { label, color } = App.matchOption('fileStatus', val)
        return (
          <Space size={0}>
            <Tag style={{ border: 0 }} color={color}>
              {label}
            </Tag>
          </Space>
        )
      },
    },
    {
      title: '操作',
      width: 200,
      dataIndex: 'operator',
      render: (val, rows) => {
        const canDownload = rows.status === '已借阅'
        return (
          <Space>
            <a onClick={() => preview(rows.fileId)}>预览</a>
            {canDownload ? (
              <Button
                type="link"
                onClick={() =>
                  store.downloadFile({
                    mainId: Number(id),
                    fileId: rows.fileId,
                    moduleType: 'ARCHIVES',
                  })
                }
              >
                下载
              </Button>
            ) : (
              <span></span>
            )}
          </Space>
        )
      },
    },
  ]

  const preview = (fileId) => {
    window.open(`/preview/reportPreview/${fileId}`)
  }

  return (
    <Page store={store} params={{ id }} header={null}>
      <div className={styles.wrap}>
        <div className={styles.nav}>
          <div className={styles.title}>
            {data && data[0]?.projName + `(${data[0]?.requiredSituation})`}
            {data && data[0] && (
              <Tag
                className={styles.tag}
                color={App.matchOption('archivingStatus', data[0]?.status).color}
              >
                {App.matchOption('archivingStatus', data[0]?.status).label}
              </Tag>
            )}
          </div>
          <div className={styles.btnWrap}>
            <Space>
              {/* <Search  //非受控组件
								value={store.searchValue}
								allowClear
								placeholder="请输入搜索"
								className={styles.searchInput}
								onSearch={(v) => store.pageSearch(v) }
								onChange={(e) => store.setSearchValue(e.target.value)}
							/> */}
              <SearchBar store={store.searchBar} searchButton={false} resetButton={false}>
                <Item name={'content'} noStyle>
                  <Search
                    allowClear
                    placeholder="请输入搜索"
                    className={styles.searchInput}
                    onSearch={() => store.searchBar.search()}
                  />
                </Item>
              </SearchBar>
            </Space>
          </div>
        </div>
        <div className={styles.wrap}>
          <div className={styles.contentWrap}>
            <div className={styles.anchorWrap}>
              <Anchor className={styles.anchor} style={{ top: 100 }}>
                {data &&
                  data?.map((item) => {
                    return <Link href={item.href} title={item.title} key={item.key} />
                  })}
              </Anchor>
            </div>
            <div className={styles.content}>
              {data?.map((v, index) => {
                const statusApproval =
                  v.flowStatus === 'UNDER_APPROVAL' ||
                  v.flowStatus === 'SYS_UNDER_APPROVAL' ||
                  v.flowStatus === 'APPROVAL_PASS' ||
                  v.flowStatus === 'SYS_APPROVAL_PASS'
                if (v.title === '待补充文档') {
                  return (
                    <div id={v.title} key={v.id} className={styles.replenish}>
                      <div className={styles.title}>
                        <p className={styles.text}>
                          {v.title}
                          <span className={styles.textName}>
                            {'( ' + (v.fileTypes || '无') + ' )'}
                          </span>
                        </p>
                        <Button
                          type="primary"
                          className={styles.btn}
                          onClick={store.remind}
                          disabled={statusApproval}
                          access={'archivesmanage'}
                        >
                          提醒催办
                        </Button>
                      </div>
                      <div className={styles.draggerAll}>
                        <div className={styles.draggerBlock}>
                          <div
                            className={statusApproval ? styles.disableDragger : styles.dragger}
                            onClick={statusApproval ? null : () => store.uploadModal.open({ id })}
                          >
                            <span className={styles.cell}>
                              <div className={styles.cellItem}>
                                <p>
                                  <PlusOutlined className={styles.icon} />
                                </p>
                                <p className={styles.text}>点击或拖拽上传待补充文件</p>
                              </div>
                            </span>
                          </div>
                        </div>
                      </div>
                      <Divider />
                    </div>
                  )
                } else {
                  return (
                    <div key={v.id}>
                      {index === 1 ? (
                        <Button
                          type="primary"
                          className={styles.contentBtn}
                          onClick={store.downloadModal.open}
                          disabled={haveData()}
                        >
                          申请下载
                        </Button>
                      ) : null}
                      <div key={v.id} id={v.id}>
                        <h3>{v.title}</h3>
                        <Table
        columnsFilter={'manage_detail_idjs'}
                onFilter={(key,val) => saveServer('manage_detail_idjs',val)}

                          selectable
                          rowKey={'fileId'}
                          // autoRequest={false}
                          store={tableStores[v.id]}
                          pagination={false}
                          columns={columns}
                        />
                      </div>
                    </div>
                  )
                }
              })}
            </div>
          </div>
        </div>
      </div>
      <UploadModal store={store} currentList={uploadData} id={id} />
      <DetailDownload store={store} />
    </Page>
  )
}

function UploadModal({ store, currentList, id }) {
  const getCorporationUploadParams = (materialsType) => {
    return {
      name: 'file',
      multiple: true,
      beforeUpload: async (info) => {
        const { code } = await store.upload({
          file: info,
          materialsType,
        })
        if (code === 200) {
          return Upload.LIST_IGNORE
        } else {
          message.info('上传失败！')
          return Upload.LIST_IGNORE
        }
      },
      onRemove: () => false,
    }
  }

  const handleCancel = () => {
    store.uploadModal.close()
  }

  return (
    <Modal
      title={'上传资料'}
      store={store.uploadModal}
      className={styles.uploadModal}
      destroyOnClose
      footer={[
        <Button key="back" onClick={handleCancel}>
          取消
        </Button>,
        // <Button key="save" onClick={
        // 	() => {
        // 		// store.pageSearch('')
        // 		// store.setSearchValue('')
        // 		store.searchBar.reset()
        // 		store.uploadModal.close()
        // 	}}
        // >保存
        // </Button>,
        <Button key="submit" type="primary" onClick={() => store.effectSubmit()}>
          提交审批
        </Button>,
      ]}
    >
      {currentList?.map((item, index) => {
        return (
          <div key={item.title} style={{ marginTop: index === 0 ? 0 : 10 }}>
            <h3>{item.title}</h3>
            {item.children?.map((v, i) => {
              return (
                <>
                  <Dragger
                    key={v.label}
                    {...getCorporationUploadParams(v.value)}
                    style={{ marginTop: i !== 0 ? '20px' : '0px' }}
                  >
                    <p className="ant-upload-text">
                      {v.required === 1 && <span className={styles.requireTag}>*</span>}
                      {v.label}
                    </p>
                    <p className="ant-upload-hint">点击/将文件拖拽到这里上传</p>
                  </Dragger>
                  {v.files &&
                    v.files?.length > 0 &&
                    v.files?.map((fileItem) => {
                      const { fileName, fileId } = fileItem
                      const { Icon } = getFileType(fileName)
                      return (
                        <Space size={8} key={fileItem.fileId} className={styles.spaceItem}>
                          <Icon style={{ fontSize: 16 }} />
                          <Tooltip title={fileName}>
                            {fileName.length > 25 ? fileName.substring(0, 25) + '...' : fileName}
                          </Tooltip>
                          <DeleteOutlined
                            style={{ color: 'rgba(0, 0, 0, 0.65)' }}
                            onClick={() => {
                              store.remove({
                                mainId: id,
                                fileId,
                                moduleType: 'ARCHIVES',
                              })
                            }}
                          />
                        </Space>
                      )
                    })}
                </>
              )
            })}
          </div>
        )
      })}
    </Modal>
  )
}

export default observer(ArchivesDetail)
