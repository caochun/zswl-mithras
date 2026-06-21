import { makeAutoObservable, history, getQuery } from '@zswl/admin'
import { FormStore, PageStore } from '@zswl/components'
import { message } from 'antd'
import Api from '@/api/process/detail/flowDetailApi'
class Store {
	constructor() {
		makeAutoObservable(this)
	}

	templateData ={}
	treeDataTemp = []
	initLoop = (list) => {
		return list.map(item => {
			const { materialsData, projName, projKey, files, key, materialsName, fileName, fileId, ...rest } = item
			const obj = {
				projName: projName || materialsName || fileName,
				key: projKey || key || fileId,
				...rest,
			}
			if (materialsData || files) {
				obj.children = this.initLoop(materialsData || files)
			}
			return obj
		})
	}
	page = new PageStore({
		request: async (params) => {
			const detail = await Api.postFlowDownload({
				batch: params.id
			})
			this.templateData = detail
			const dataTest = this.initLoop(detail?.materials)
			this.treeDataTemp = dataTest
		}
	})
	form = new FormStore()
}
export default Store
