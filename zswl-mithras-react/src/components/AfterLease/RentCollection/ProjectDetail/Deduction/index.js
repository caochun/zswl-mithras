import { App, Button, Form, Modal, ModalStore, Table } from '@zswl/components'
import { Descriptions, Input, InputNumber, Tooltip } from 'antd'
import styles from '../index.less'
import { FormUpload } from '@/components/Form'
import { amountFormat, getInputNumberAmountProps, validatorBigZero, validatorNoZero } from '@/utils'
import { observer } from '@zswl/admin'
import NoOverdue from '../../Components/NoOverdue'
import { saveServer } from '@/utils'

const { Item } = Form
const Deduction = ({ store }) => {
  const { reductionList, page, receiptList } = store
  const { collectionLevel } = receiptList || {}
  const { canEdit, isProcess } = store.page.getParams()
  const columns = [
    {
      title: '资料名称',
      dataIndex: 'filename',
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
      fixed: 'right',
      width: 120,
      render: (id, { filename }) => {
        if (id.toString().indexOf('folder') > -1) {
          return null
        }
        return (
          <div className={styles.control}>
            <Button
              type="link"
              //  className={styles.item}
              style={{ padding: 0 }}
              onClick={() => preview(id)}
            >
              预览
            </Button>
            <Button
              // className={styles.item}
              type="link"
              // style={{ padding: 0 }}
              onClick={() => {
                return store.download({
                  filename,
                  fileId: id,
                  moduleType: 'OVERDUE_COLLECTION_REDUCTION',
                  materialsType: 'DEDUCTION_INTEREST',
                })
              }}
            >
              下载
            </Button>
          </div>
        )
      },
    },
  ]
  const preview = (id) => {
    window.open(`/preview/reportPreview/${id}`)
  }
  const buttonDisableStatus = () => {
    return reductionList?.list?.find((item) => item.collectionStatus === 'NEW')
  }
  if (!collectionLevel) {
    return <NoOverdue />
  }
  return (
    <div className={styles.deduction}>
      <div className={styles.nav}>
        <div className={styles.title}></div>
        {/* {!page.getParams().isProcess && (
          <Button
            type="primary"
            disabled={buttonDisableStatus()}
            onClick={() => store.deductionMaterialsAddModal.open()}
          >
            添加
          </Button>
        )} */}
      </div>
      {reductionList?.list?.map((item, index) => {
        const {
          penaltyInterestDeductionAmount,
          processStatus,
          reasonExplain,
          createByName,
          fileList,
          createTime,
          processTime,
        } = item
        return (
          <div className={styles.deductionItem} key={index}>
            <div className={styles.canEdit}>
              {canEdit && isProcess && processStatus === 'UNDER_APPROVAL' && (
                <Button
                  type="primary"
                  onClick={() =>
                    store.deductionMaterialsAddModal.open({
                      ...item,
                      penaltyInterestDeductionAmount: penaltyInterestDeductionAmount / 10000,
                      files: fileList.map((t) => {
                        return {
                          id: t.id,
                          name: t.filename,
                        }
                      }),
                    })
                  }
                >
                  编辑
                </Button>
              )}
            </div>
            <Descriptions title="" column={2} size={'small'} className={styles.des}>
              <Descriptions.Item label="审批状态">
                {App.matchOption('commonProcessStatus', processStatus).label}
              </Descriptions.Item>
              <Descriptions.Item label="申请人">{createByName}</Descriptions.Item>
              <Descriptions.Item label="发起时间">{createTime}</Descriptions.Item>
              <Descriptions.Item label="通过时间">{processTime || '-'}</Descriptions.Item>
              <Descriptions.Item label="罚息减免金额" span={2}>
                {amountFormat(penaltyInterestDeductionAmount / 10000)}
              </Descriptions.Item>
              <Descriptions.Item label="原因简述" span={2}>
                {reasonExplain}
              </Descriptions.Item>
            </Descriptions>
            <div className={styles.title}>罚息减免材料</div>
            <Table onFilter={(key,val) => saveServer('ProjectDetail_Deduction_1',val)} columnsFilter={'ProjectDetail_Deduction_1'} dataSource={fileList} columns={columns} />
          </div>
        )
      })}

      <DeductionAddModal canEdit={canEdit} isProcess={isProcess} store={store} />
    </div>
  )
}
const DeductionAddModal = (props) => {
  const { canEdit, isProcess, store } = props
  const { pushRemoveFileIds, interest } = store
  return (
    <Modal
      store={store.deductionMaterialsAddModal}
      title="罚息减免"
      okText={canEdit && isProcess ? '保存' : '发起逾期减免申请'}
      destroyOnClose
    >
      <div className={styles.deductionTip}>*当前该合同剩余未收罚息：{`${interest || 0}`}元</div>
      <Form labelCol={{ span: 7 }} preserve={false}>
        <Form.Item
          label={'罚息减免金额(元）'}
          name="penaltyInterestDeductionAmount"
          rules={[
            {
              required: true,
              message: '请输入罚息减免金额(元）!',
            },
            validatorBigZero,
            validatorNoZero,
          ]}
        >
          <InputNumber
            style={{ width: '100%' }}
            {...getInputNumberAmountProps()}
            max={interest}
            placeholder="请输入罚息减免金额(元）!"
          />
        </Form.Item>
        <Item
          label={'原因简述'}
          name={'reasonExplain'}
          rules={[{ required: true, message: '原因简述！' }]}
        >
          <Input.TextArea />
        </Item>
        <Item
          name="files"
          rules={[{ required: true, message: '请上传文件！' }]}
          label="罚息减免材料"
        >
          <FormUpload
            maxCount={20}
            onRemove={(val) => {
              pushRemoveFileIds(val.id)
            }}
          />
        </Item>
      </Form>
    </Modal>
  )
}
export default observer(Deduction)
