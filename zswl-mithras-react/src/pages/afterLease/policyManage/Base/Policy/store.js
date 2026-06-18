import { makeAutoObservable } from '@zswl/admin'
import { ModalStore, TableStore, DrawerStore } from '@zswl/components'
import { Modal, message } from 'antd'
import moment from 'moment'
import DataUpload from '@/components/DataUpload'
import Api from './api'
import policyManageApi from '@/api/afterLease/policyManageApi'
import policyTemporaryStorageApi from '@/api/afterLease/policyTemporaryStorageApi'
import { saveFile } from '@/utils'
import policyLedgerApi from '@/api/afterLease/policyLedgerApi'

class Store {
  constructor({ businessVersion, mainId, paramsAsPolicy, baseDetailData } = {}) {
    this.businessVersion = businessVersion
    this.mainId = mainId
    this.baseDetailData = baseDetailData

    this.isPolicyPage = paramsAsPolicy?.pageSource === 'policy'
    this.policyId = paramsAsPolicy?.policyRecord?.id

    this.contractEndDate =
      paramsAsPolicy?.policyRecord?.actualFinishDate || baseDetailData?.actualFinishDate

    makeAutoObservable(this)
  }

  // 保单信息
  policyTable = new TableStore({
    request: async (params) => {
      const data = await policyTemporaryStorageApi.postTmpList({
        ...params,
        contractId: this.mainId,

        adventFlag: this.adventFlag,
      })
      return data || []
    },
  })

  onFileChange = async (file) => {
    const { fileList } = DataUpload.classify(file)
    const data = await policyTemporaryStorageApi.postTmpImport({
      file: fileList[0],
      contractId: this.mainId,
      policyId: this.policyId,
    })

    if (data) {
      if (data.indexOf('成功') > -1) {
        message.success(data)
      } else {
        Modal.info({
          title: '温馨提示',
          content: data,
        })
      }
    }
    this.policyTable.search()
  }

  policyCheck = 0
  setPolicyCheck = (value) => {
    this.policyCheck = value
  }

  policyBatchDown = async () => {
    const { keys } = this.policyTable.getSelected()
    if (!keys.length) {
      message.info('请选择保单')
      return
    }
    await policyTemporaryStorageApi.postTmpExport({
      ids: keys,
    })
    message.success('下载成功')
  }

  // 保单信息 新增/编辑/删除
  policyModal = new ModalStore({
    onOpen: (data = {}) => {
      this.removeFileIds = []
      const { insuranceStartDate, insuranceEndDate, ...restData } = data

      return {
        ...restData,
        insuranceStartDate: insuranceStartDate && moment(insuranceStartDate),
        insuranceEndDate: insuranceEndDate && moment(insuranceEndDate),
      }
    },
    onFinish: async (values, initValues) => {
      const { renewInsuranceFlag } = values
      if (this.contractEndDate) {
        const contractEndDate = moment(this.contractEndDate, 'yyyy-MM-DD')
        // 保单截止日期大于等于合同截止日期
        const policyEndDateBigOrSameContractEndDate = contractEndDate.isSameOrBefore(
          values.insuranceEndDate
        )
        // 若保险到期日已经覆盖合同约定结束日，是否续保选择为需要续保，则在点击确定时弹窗提示：保险到期日在合同约定结束日之后，是否仍需到期续保？
        if (
          policyEndDateBigOrSameContractEndDate &&
          renewInsuranceFlag === 'RENEWAL_UPON_EXPIRATION'
        ) {
          Modal.confirm({
            title: `保险到期日在合同约定结束日（${this.contractEndDate}）之后，是否仍需到期续保？`,
            onOk: () => {
              this.handleSavePolicy(values, initValues)
            },
          })
        } else {
          // 若保险到期日未能覆盖合同约定结束日，是否续保选择为无需续保，则在点击确定时弹窗提示：保险到期日在合同约定结束日之前，是否确定无需续保？
          if (!policyEndDateBigOrSameContractEndDate && renewInsuranceFlag === 'NO_NEED_TO_RENEW') {
            Modal.confirm({
              title: `保险到期日在合同约定结束日（${this.contractEndDate}）之前，是否确定无需续保？`,
              onOk: () => {
                this.handleSavePolicy(values, initValues)
              },
            })
          } else {
            this.handleSavePolicy(values, initValues)
          }
        }
      } else {
        this.handleSavePolicy(values, initValues)
      }
    },
  })

  handleSavePolicy = async (values, initValues) => {
    const { files } = values
    const { fileList } = DataUpload.classify(files)
    const insuranceEndDate = moment(values.insuranceEndDate).format('yyyy-MM-DD')
    const insuranceStartDate = moment(values.insuranceStartDate).format('yyyy-MM-DD')

    if (initValues?.id) {
      await policyTemporaryStorageApi.postTmpModify({
        ...values,
        id: initValues.id,
        contractId: this.mainId,
        files: fileList,
        insuranceEndDate,
        insuranceStartDate,
        removeFileIds: [...this.removeFileIds],
      })

      message.success('保存成功')
      this.policyTable.search()
      this.policyModal.close()
    } else {
      const mainId = await policyTemporaryStorageApi.postTmpAdd({
        ...values,
        contractId: this.mainId,
        insuranceEndDate,
        insuranceStartDate,
        policyId: this.policyId,
      })
      await saveFile(files, { mainId, moduleType: 'POLICY_TMP' })
      message.success('新增成功')

      this.policyTable.search()
      this.policyModal.close()
    }
  }

  policyItemEdit = (data) => {
    this.policyModal.open({
      ...data,
      file: data.file?.map((item) => {
        return {
          fileName: item.name,
          fileId: item.id,
          mainId: data.id,
        }
      }),
      insuranceEndDate: moment(data.insuranceEndDate),
      insuranceStartDate: moment(data.insuranceStartDate),
    })
  }
  policyItemDelete = async (data) => {
    Modal.confirm({
      title: '是否确认删除该条数据？',
      onOk: async () => {
        await policyTemporaryStorageApi.postTmpRemove({
          ids: [data.id],
        })
        message.success('删除成功')
        this.policyTable.search()
      },
    })
  }

  //保单弹窗删除
  removeFileIds = []
  removeFiles = async (file) => {
    this.removeFileIds = [...this.removeFileIds, file.id]
  }

  //保单信息勾选框
  onChecked = async ({ checked }) => {
    const params = {
      id: this.mainId,
      flag: checked,
    }
    await Api.postChecked(params)
  }

  //相关附件drawer
  recordId
  filesManageDraw = new DrawerStore({
    onOpen: ({ mainId }) => {
      this.recordId = mainId
    },
  })

  batchRemove = async () => {
    const { keys } = this.policyTable.getSelected()
    if (keys.length > 0) {
      await policyTemporaryStorageApi.postTmpRemove({
        contractId: this.mainId,
        ids: keys,
      })
      message.success('删除成功')
      this.policyTable.search()
    } else {
      message.info('请选中删除项')
    }
  }

  adventFlag
  onAdventChange = async (e) => {
    const value = e.target.checked
    this.adventFlag = value
    this.policyTable.search({ adventFlag: value, page: 1 })
  }
}
export default Store
