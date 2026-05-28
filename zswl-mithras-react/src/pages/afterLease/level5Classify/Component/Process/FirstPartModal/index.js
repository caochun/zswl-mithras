import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { Button, Space } from 'antd'
import styles from './index.less'
import { useEffect, useState } from 'react'

const { Item } = Form

function Index({ store, canEdit = true }) {
  const {
    addClientNames = [],
    deleteClientNames = [],
    changeClientNames = [],
  } = store.firstPartModal.getInitialValues() || {}
  return (
    <Modal
      title={store.detailVisible ? '初分详情' : '系统初分'}
      store={store.firstPartModal}
      // okText={'确定'}
      destroyOnClose
      width={600}
      closable={store.detailVisible}
      maskClosable={!store.detailVisible}
      // footer={false}
      footer={
        store.firstPartList?.firstFlag ? (
          <Space className={styles.btn}>
            <Button
              type="primary"
              onClick={() => {
                store.firstPartModal.close()
              }}
            >
              确定
            </Button>
          </Space>
        ) : (
          false
        )
      }
    >
      {!store.detailVisible ? (
        <div className={styles.detailContent}>
          <div>
            系统初分已完成<span>{!store.firstPartList?.firstFlag && '，请点击查看初分详情！'}</span>
          </div>
          {!store.firstPartList?.firstFlag && (
            <div>
              <Button
                type="primary"
                onClick={() => {
                  store.detailVisible = true
                }}
              >
                查看详情
              </Button>
            </div>
          )}
        </div>
      ) : (
        <div className={styles.content}>
          <div className={styles.weightFont}>以下客户与上次初分结果存在差异，请重新进行复核！</div>
          <div className={styles.weightContentFont}>新增投放的客户：</div>
          {/* <div>{addClientNames.join(',<br>')}</div> */}
          <div dangerouslySetInnerHTML={{ __html: addClientNames.join(',<br>') }}></div>

          <div className={styles.weightContentFont}>剩余未还本金已为0的客户：</div>
          {/* <div>{deleteClientNames.join(',<br>')}</div> */}
          <div dangerouslySetInnerHTML={{ __html: deleteClientNames.join(',<br>') }}></div>

          <div className={styles.weightContentFont}>本次初分结果与已有结果不一致的客户：</div>
          {/* <div>{changeClientNames.join(',<br>')}</div> */}
          <div dangerouslySetInnerHTML={{ __html: changeClientNames.join(',<br>') }}></div>
        </div>
      )}
    </Modal>
  )
}

export default observer(Index)
