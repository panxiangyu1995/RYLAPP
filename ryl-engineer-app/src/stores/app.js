import { defineStore } from 'pinia';
import { getLatestVersion } from '../api/app';
import { useToast } from 'vue-toastification';

const toast = useToast();

// 当前APP版本号，需要手动维护
const CURRENT_VERSION_CODE = 1;

export const useAppStore = defineStore('app', {
    state: () => ({
        latestVersion: null,
        showUpdateDialog: false
    }),
    actions: {
        async checkUpdate(isManual = false) {
            try {
                const response = await getLatestVersion();
                if (response.success && response.data) {
                    this.latestVersion = response.data;
                    if (response.data.versionCode > CURRENT_VERSION_CODE) {
                        this.showUpdateDialog = true;
                    } else {
                        if (isManual) {
                            toast.success('当前已是最新版本');
                        }
                    }
                } else {
                    if (isManual) {
                        toast.error('检查更新失败');
                    }
                    console.error('获取最新版本信息失败:', response.message);
                }
            } catch (error) {
                if (isManual) {
                    toast.error('检查更新失败');
                }
                console.error('检查更新时出错:', error);
            }
        },
        closeUpdateDialog() {
            this.showUpdateDialog = false;
        }
    }
}); 