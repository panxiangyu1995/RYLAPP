import axios from './http';

/**
 * 获取最新APP版本信息
 * @returns {Promise<axios.AxiosResponse<any>>}
 */
export const getLatestVersion = () => {
    return axios.get('/api/app/latest-version');
}; 