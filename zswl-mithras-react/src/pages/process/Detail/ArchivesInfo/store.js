import { makeAutoObservable, history, getQuery } from '@zswl/admin'
import { FormStore, PageStore } from '@zswl/components'
import { message } from 'antd'
import Api from '../api'
class Store {
	constructor() {
		makeAutoObservable(this)
	}

	// templateData = {
	// 	reason: '哈哈哈',
	// 	data: [
	// 		{
	// 			projName: "谷租赁0216测试征信",
	// 			projKey: 28,
	// 			materials: [
	// 				{
	// 					materialsName: "资料1",
	// 					materialsKey: "28_8",
	// 					files: [
	// 						{
	// 							fileName: "文档1-员工报送批量补数模版.xlsx",
	// 							fileId: 151268,
	// 						},
	// 						{
	// 							fileName: "文档1-国资委风险事件报表.xlsx",
	// 							fileId: 151278,
	// 						}
	// 					]
	// 				}
	// 			]
	// 		}
	// 	]
	// }
	// temp = {
	// 	"reason": "123123",
	// 	"materials": [
	// 		{
	// 			"projName": "王传昊测试合同模版",
	// 			"key": 2658,
	// 			"materialsData": [
	// 				{
	// 					"materialsName": "资料1",
	// 					"key": "2658_8",
	// 					"files": [
	// 						{
	// 							"fileId": 152808,
	// 							"fileName": "1.租赁合同-回租.docx"
	// 						},
	// 						{
	// 							"fileId": 152948,
	// 							"fileName": "关联方认定准则-分大类.docx"
	// 						}
	// 					]
	// 				}
	// 			]
	// 		}
	// 	]
	// }
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
