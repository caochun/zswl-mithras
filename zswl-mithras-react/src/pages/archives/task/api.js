import { http } from '@zswl/admin'

export default {
	getList: (data) => http.post('/archive/template/list', data),
	add: (data) => http.post('/archive/template/add', data),
	update: (data) => http.post('/archive/template/update', data),
	detail: (data) => http.post('/archive/template/info', data),
}
