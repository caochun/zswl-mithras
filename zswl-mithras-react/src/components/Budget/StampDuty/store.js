import { TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/budget/stampDutyApi'
import { downFile } from '@/utils'
import { message, Modal } from 'antd'

export default class DataStore{
    constructor() {
        makeAutoObservable(this)
    }
    table = new TableStore({
        request: async ({createDate,...params}) => {
            const [start, end] = createDate || []
            const res = await Api.getList({
                startDateFrom: start?.format('yyyy-MM-DD'),
                startDateTo: end?.format('yyyy-MM-DD'),
                ...params,
                ids:[]
            })
            return res?.records || []
        }
    })
    constractList = []
    type
    updateBelongCode = async(type) => {
        if(type === this.type){
            return
        }
        this.type = type
        const res = await Api.getContractList({type})
        const _label = this.type === 'ht' ? 'contractCode' : 'financingCode'
        const _list = this.type === 'ht' ? 'contracts' : 'fundContracts'
        if(res?.[_list]?.list){
            this.constractList = res?.[_list]?.list.map(item => ({...item,label:item[_label],value:item.id}))
            console.log(this.constractList)
        }
    }
    getClientItem = async(belongId) => {
        const res = await Api.getContractList({belongId,type:this.type})
        if(res?.records?.list && res?.records?.list.length > 0 ){
            return res?.records?.list[0]
        }
        return
    }
    downloadTemplate = () => {
        Api.downloadTemplate('STAMP_DUTY_RECORD', 'FILE_TEMPLATE').then((res) => {
            downFile(res)
        })
    }
    refresh = () => {
        this.table?.search()
    }
    beforeUpload = () => {
        this.table?.search()
    }
    upload = async(data) => {
        const res = await Api.upload(
            data,
        )
        if (res.code === 200) {
            message.success('导入成功')
            this.table.search()
        } else {
            message.error(res.msg)
        }
    }
    downloadBatch = async() => {
        const rows = this.table.getSelected()
        Api.download({ids:rows.keys})
    }
    deleteRows = async () => {
        try {
            const rows = this.table.getSelected()
            if(rows?.keys.length === 0){
                message.warning('请选择要删除的行')
                return;
            }
            Modal.confirm({
                title: '是否删除选中数据？',
                onOk: async () => {
                    await Api.delete({ids:rows.keys})
                    message.success('删除成功')
                    this.table.search()
                }
            })
        }catch (error) {
            throw error
        }
    }
}
