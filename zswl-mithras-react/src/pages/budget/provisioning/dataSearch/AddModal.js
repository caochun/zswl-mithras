import { observer } from '@zswl/admin'
import { Input, DatePicker, Upload, message, InputNumber } from 'antd'
import { Modal, Form, Button, Select, App } from '@zswl/components'
import FormAmount from '@/components/Form/FormAmount'
import { ClientSelect, OrgSelect, ApiSelect, ContractSelect } from '@/components'
import DownloadTemplate from '@/components/Actions/DownloadTemplate'
import { useRef } from 'react'

const getPopupContainer = (triggerNode) => triggerNode.parentElement
const { Dragger } = Upload
/**
 * 新增数据弹窗组件
 * 支持手动添加和批量导入两种方式
 * @param {Object} props - 组件属性
 * @param {Object} props.store - 父组件传入的store
 */
const AddModal = observer(({ store }) => {
  const { enums } = store.page.getData()
  const { id } = store.addModal.getInitialValues() ?? {}
  const [form] = Form.useForm()
  const { activeTab, setActiveTab, fileList, setFileList, receiptCodeList } = store
  const title = id ? '编辑数据' : '新增数据'

  const amountCommonProps = {
    initFormat: 1,
    min: -Infinity,
  }
  const ref = useRef(null)
  console.log('ref: ', ref)

  return (
    <Modal title={title} store={store.addModal} width={600} destroyOnClose>
      {id ? null : (
        <div>
          <Button.Group>
            <Button
              type={activeTab === 'manual' ? 'primary' : 'default'}
              onClick={() => setActiveTab('manual')}
            >
              手动添加
            </Button>
            <Button
              type={activeTab === 'batch' ? 'primary' : 'default'}
              onClick={() => setActiveTab('batch')}
            >
              批量导入
            </Button>
          </Button.Group>
        </div>
      )}
      <div className="z-modal-content" ref={ref}>
        <Form form={form} layout="horizontal">
          {activeTab === 'manual' ? (
            <>
              <Form.Item hidden name={'id'}>
                <Input />
              </Form.Item>
              <Form.Item
                name="clientName"
                label="客户名称"
                rules={[{ required: true, message: '请选择客户名称' }]}
              >
                <Input disabled={id} />
              </Form.Item>
              {!id && (
                <>
                  <Form.Item
                    name="contractLeaseType"
                    label="业务类型"
                    rules={[{ required: true, message: '请选择业务类型' }]}
                  >
                    <Select
                      options="leaseType"
                      labelInValue
                      placeholder="请选择业务类型"
                      getPopupContainer={getPopupContainer}
                    />
                  </Form.Item>
                  <Form.Item
                    name="contractBizType"
                    label="业务类型"
                    rules={[{ required: true, message: '请选择业务类型' }]}
                  >
                    <Select
                      options={[
                        { label: '租赁', value: '租赁' },
                        { label: '保理', value: '保理' },
                        { label: '转租赁', value: '转租赁' },
                      ]}
                      placeholder="请选择业务类型"
                      getPopupContainer={getPopupContainer}
                    />
                  </Form.Item>

                  <Form.Item
                    name="profitBelongDeptName"
                    label="业务部门"
                    rules={[{ required: true, message: '请选择业务部门' }]}
                  >
                    <OrgSelect labelInValue getPopupContainer={getPopupContainer} />
                  </Form.Item>

                  <Form.Item
                    name="projClassifyName"
                    label="项目类型"
                    rules={[{ required: true, message: '请选择项目类型' }]}
                  >
                    <Select
                      options="kpiProjectClassifyEnum"
                      labelInValue
                      placeholder="请选择项目类型"
                      getPopupContainer={getPopupContainer}
                    />
                  </Form.Item>
                </>
              )}

              <Form.Item
                name="contractCode"
                label="合同编号"
                rules={[{ required: true, message: '请选择合同编号' }]}
              >
                <Input disabled={id} />
              </Form.Item>
              <Form.Item
                name="receiptCode"
                label="借据编号"
                rules={[{ required: true, message: '请选择借据编号' }]}
              >
                <Input />
              </Form.Item>
              <Form.Item
                name="contractExpirationDate"
                label="到期日"
                rules={[{ required: true, message: '请选择到期日' }]}
                hidden={!!id}
              >
                <DatePicker style={{ width: '100%' }} getPopupContainer={getPopupContainer} />
              </Form.Item>
              {!id && (
                <>
                  <Form.Item
                    name="residualMaturity"
                    label="剩余年限"
                    rules={[{ required: true, message: '请输入剩余年限' }]}
                  >
                    <FormAmount {...amountCommonProps} placeholder="请输入剩余年限" />
                  </Form.Item>

                  <Form.Item
                    name="profitCurrent"
                    label="本月风险金余额(元)"
                    required
                    rules={[{ required: true, message: '请输入本月风险金余额' }]}
                  >
                    <FormAmount {...amountCommonProps} placeholder="请输入本月风险金余额" />
                  </Form.Item>

                  <Form.Item
                    name="profitTotal"
                    label="上月风险金余额(元)"
                    required
                    rules={[{ required: true, message: '请输入上月风险金余额' }]}
                  >
                    <FormAmount {...amountCommonProps} placeholder="请输入上月风险金余额" />
                  </Form.Item>

                  <Form.Item
                    name="bonusCurrent"
                    label="本月风险金计提/转回"
                    required
                    rules={[{ required: true, message: '请输入本月风险金计提/转回' }]}
                  >
                    <FormAmount {...amountCommonProps} placeholder="请输入本月风险金计提/转回" />
                  </Form.Item>
                </>
              )}
              {id && (
                <Form.Item
                  name="promotionResultHandle"
                  label="上迁债项阶段"
                  required
                  rules={[{ required: true, message: '请输入上迁债项阶段' }]}
                >
                  <Select
                    options={[
                      { label: '否', value: '0' },
                      { label: '是', value: '1' },
                    ]}
                    placeholder="请选择上迁债项阶段"
                    getPopupContainer={getPopupContainer}
                  />
                </Form.Item>
              )}
              <Form.Item
                name="innerMdLevel"
                label="内评级别"
                required
                rules={[{ required: true, message: '请选择内评级别' }]}
              >
                <Select
                  options={enums?.innerLevelEnum ?? []}
                  placeholder="请选择内评级别"
                  getPopupContainer={getPopupContainer}
                />
              </Form.Item>

              <Form.Item name="outerLevel" label="外评级别">
                <Select
                  options={enums?.outerLevelEnum ?? []}
                  placeholder="请选择外评级别"
                  getPopupContainer={getPopupContainer}
                />
              </Form.Item>

              <Form.Item
                name="group"
                label="所属分组"
                required
                rules={[{ required: true, message: '请选择所属分组' }]}
              >
                <Select
                  options={enums?.kpiRatingModelGroupEnum ?? []}
                  placeholder="请选择所属分组"
                  getPopupContainer={getPopupContainer}
                />
              </Form.Item>

              <Form.Item
                name="riskLevel"
                label="五级分类"
                required
                rules={[{ required: true, message: '请选择五级分类' }]}
              >
                <Select
                  options={enums?.assetClassifyResultEnum ?? []}
                  placeholder="请选择五级分类"
                  getPopupContainer={getPopupContainer}
                />
              </Form.Item>

              <Form.Item
                name="lateDay"
                label="逾期天数"
                required
                rules={[{ required: true, message: '请输入逾期天数' }]}
              >
                <Input placeholder="请输入逾期天数" />
              </Form.Item>

              <Form.Item
                name="leaseType"
                label="租赁物类型"
                required
                rules={[{ required: true, message: '请选择租赁物类型' }]}
              >
                <Select
                  options={enums?.leaseTypeEnum ?? []}
                  placeholder="请选择租赁物类型"
                  getPopupContainer={getPopupContainer}
                />
              </Form.Item>

              <Form.Item
                name="remainPrincipal"
                label="剩余本金"
                required
                rules={[{ required: true, message: '请输入剩余本金' }]}
              >
                <FormAmount {...amountCommonProps} placeholder="请输入剩余本金" />
              </Form.Item>

              <Form.Item noStyle dependencies={['contractLeaseType']}>
                {() => {
                  const contractLeaseType = form.getFieldValue('contractLeaseType')
                  const isOperating = contractLeaseType?.value === 'jyx_zu'
                  return (
                    <Form.Item
                      name="accruedInterest"
                      label="应计利息"
                      required={!isOperating}
                      rules={[{ required: !isOperating, message: '请输入应计利息' }]}
                    >
                      <FormAmount {...amountCommonProps} placeholder="请输入应计利息" />
                    </Form.Item>
                  )
                }}
              </Form.Item>

              <Form.Item
                name="deposit"
                label="保证金"
                required
                rules={[{ required: true, message: '请输入保证金' }]}
              >
                <FormAmount {...amountCommonProps} placeholder="请输入保证金" />
              </Form.Item>

              <Form.Item noStyle dependencies={['contractLeaseType']}>
                {() => {
                  const contractLeaseType = form.getFieldValue('contractLeaseType')
                  const isOperating = contractLeaseType?.value === 'jyx_zu'
                  return (
                    <Form.Item
                      name="nextRent"
                      label="下期租金"
                      required={isOperating}
                      rules={[{ required: isOperating, message: '请输入下期租金' }]}
                    >
                      <FormAmount
                        {...amountCommonProps}
                        placeholder="请输入下期租金"
                        min={-Infinity}
                      />
                    </Form.Item>
                  )
                }}
              </Form.Item>

              <Form.Item name="remark" label="备注">
                <Input.TextArea placeholder="请输入备注" />
              </Form.Item>
            </>
          ) : (
            <div>
              <div style={{ marginBottom: 16, height: 150 }}>
                <DownloadTemplate
                  params={{
                    templateName: 'ECL_ADD_RECORD',
                    moduleType: 'Ecl',
                  }}
                />

                <Dragger
                  fileList={fileList}
                  beforeUpload={store.beforeUpload}
                  onRemove={store.onRemove}
                  maxCount={1}
                  accept=".xlsx,.xls"
                  style={{ marginTop: 8, height: 200 }}
                >
                  <p>点击选择文件或拖拽文件到此处上传</p>
                </Dragger>
              </div>
            </div>
          )}
          <Form.Item name="provisionId" hidden />
        </Form>
      </div>
    </Modal>
  )
})

export default AddModal
