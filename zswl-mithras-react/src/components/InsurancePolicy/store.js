import { makeAutoObservable } from '@zswl/admin'
import { ModalStore, Modal, TableStore, DrawerStore } from '@zswl/components'
import { message } from 'antd'
import moment from 'moment'
import DataUpload from '@/components/DataUpload'
import Api from '@/api/insurancePolicy/insurancePolicyApi'

function isValidDate(dateStr) {
  const date = moment(dateStr, 'YYYY-MM-DD', true)
  return date.isValid()
}

class Store {
  constructor({ businessVersion, mainId, paramsAsPolicy, baseDetailData } = {}) {
    this.businessVersion = businessVersion
    this.mainId = mainId
    this.baseDetailData = baseDetailData

    this.paramsAsPolicy = paramsAsPolicy
    this.isPolicyPage = paramsAsPolicy?.pageSource === 'policy'
    this.policyId = paramsAsPolicy?.policyRecord?.id || paramsAsPolicy?.id

    this.contractEndDate =
      paramsAsPolicy?.policyRecord?.actualFinishDate || baseDetailData?.actualFinishDate

    makeAutoObservable(this)
  }

  // 保单信息
  policyTable = new TableStore({
    request: async (params) => {
      if (this.paramsAsPolicy.type === 'policyRemind') {
        return await Api.postPolicyLedgerRenewInsurance({
          id: this.paramsAsPolicy.id,
        })
      }
      const api = this.isPolicyPage ? Api.postPolicyList : Api.postPaymentPolicyList
      const data = await api({
        ...params,
        paymentId: this.mainId,
        policyId: this.policyId,
        businessVersion: this.businessVersion,
        adventFlag: this.adventFlag,
      })
      return data || []
    },
  })

  onFileChange = async (file) => {
    const { fileList } = DataUpload.classify(file)
    const api = this.isPolicyPage ? Api.postPolicyImport : Api.postPaymentPolicyImport
    const data = await api({
      file: fileList[0],
      paymentId: this.mainId,
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
    await Api.postPaymentPolicyBatchExport({
      policyIds: keys,
      paymentId: this.mainId,
    })
    message.success('下载成功')
  }

  // 保单信息 新增/编辑/删除
  policyModal = new ModalStore({
    onOpen: (data) => {
      this.removeFileIds = []
      if (data) {
        const { insuranceEndDate, insuranceStartDate } = data
        console.log({ data })
        // return data
        return {
          ...data,
          insuranceEndDate: isValidDate(insuranceEndDate) ? moment(insuranceEndDate) : undefined,
          insuranceStartDate: isValidDate(insuranceStartDate)
            ? moment(insuranceStartDate)
            : undefined,
        }
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
            onOk: async () => {
              await this.handleSavePolicy(values, initValues)
            },
          })
        } else {
          // 若保险到期日未能覆盖合同约定结束日，是否续保选择为无需续保，则在点击确定时弹窗提示：保险到期日在合同约定结束日之前，是否确定无需续保？
          if (!policyEndDateBigOrSameContractEndDate && renewInsuranceFlag === 'NO_NEED_TO_RENEW') {
            Modal.confirm({
              title: `保险到期日在合同约定结束日（${this.contractEndDate}）之前，是否确定无需续保？`,
              onOk: async () => {
                await this.handleSavePolicy(values, initValues)
              },
            })
          } else {
            await this.handleSavePolicy(values, initValues)
          }
        }
      } else {
        await this.handleSavePolicy(values, initValues)
      }
    },
  })

  handleSavePolicy = async (values, initValues) => {
    const { files, insuranceStartDate, insuranceEndDate } = values
    const { fileList } = DataUpload.classify(files)
    const insuranceEndDateFormat = insuranceEndDate
      ? moment(insuranceEndDate).format('yyyy-MM-DD')
      : undefined
    const insuranceStartDateFormat = insuranceStartDate
      ? moment(insuranceStartDate).format('yyyy-MM-DD')
      : undefined

    if (initValues) {
      const api = this.isPolicyPage ? Api.postPolicyModify : Api.postPaymentPolicyModify
      await api({
        ...values,
        id: initValues.id,
        paymentId: this.mainId,
        files: fileList,
        insuranceEndDate: insuranceEndDateFormat,
        insuranceStartDate: insuranceStartDateFormat,
        removeFileIds: [...this.removeFileIds],
      })

      message.success('保存成功')
      this.policyTable.search()
      this.policyModal.close()
    } else {
      const api = this.isPolicyPage ? Api.postPolicyAdd : Api.postPaymentPolicyAdd
      await api({
        ...values,
        paymentId: this.mainId,
        files: fileList,
        insuranceEndDate: insuranceEndDateFormat,
        insuranceStartDate: insuranceStartDateFormat,
        policyId: this.policyId,
        contractId: this.paramsAsPolicy?.policyRecord?.contractId,
      })
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
        const api = this.isPolicyPage ? Api.postPolicyRemove : Api.postPaymentPolicyRemove
        await api({
          id: data.id,
          paymentId: this.mainId,
        })
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
  drawList
  filesManageDraw = new DrawerStore({
    onOpen: (data) => {
      this.drawList = data
    },
  })

  batchRemove = async () => {
    const { keys } = this.policyTable.getSelected()
    if (keys.length > 0) {
      await Api.postPaymentRemoveBatch({
        paymentId: this.mainId,
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
