<template>
  <div class="p-4">
    <h1 class="text-2xl font-bold mb-4">位置服务</h1>
    <div class="space-y-4">
      <button
        @click="getLocationByGaode"
        class="w-full bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
      >
        使用高德地图定位
        </button>
      <button 
        @click="getLocationByBaidu"
        class="w-full bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded"
      >
        使用百度地图定位
      </button>
    </div>
    <div v-if="location" class="mt-4 p-4 bg-gray-100 rounded">
      <p><strong>经度:</strong> {{ location.longitude }}</p>
      <p><strong>纬度:</strong> {{ location.latitude }}</p>
      <p><strong>地址:</strong> {{ location.address }}</p>
    </div>
    <div v-if="error" class="mt-4 p-4 bg-red-100 text-red-700 rounded">
      <p>{{ error }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const location = ref(null);
const error = ref(null);

// 请在此处替换为您申请的高德地图JS API Key
const GAODE_MAP_KEY = "YOUR_GAODE_MAP_KEY"; 
// 请在此处替换为您申请的百度地图JS API Key
const BAIDU_MAP_KEY = "YOUR_BAIDU_MAP_KEY"; 

const loadScript = (url) => {
  return new Promise((resolve, reject) => {
    const script = document.createElement('script');
    script.src = url;
    script.onload = resolve;
    script.onerror = reject;
    document.body.appendChild(script);
  });
};

const getLocationByGaode = async () => {
  location.value = null;
  error.value = '正在使用高德地图获取位置...';
  try {
    if (!window.AMap) {
      await loadScript(`https://webapi.amap.com/maps?v=2.0&key=${GAODE_MAP_KEY}`);
    }
    
    await new Promise((resolve, reject) => {
        window.AMap.plugin(['AMap.Geolocation', 'AMap.Geocoder'], function() {
            const geolocation = new window.AMap.Geolocation({
                enableHighAccuracy: true,
                timeout: 10000,
            });

            geolocation.getCurrentPosition((status, result) => {
                if (status === 'complete') {
                    const geocoder = new window.AMap.Geocoder();
                    const lnglat = [result.position.lng, result.position.lat];
                    geocoder.getAddress(lnglat, (status, geoResult) => {
                        if (status === 'complete' && geoResult.info === 'OK') {
                            location.value = {
                                longitude: result.position.lng,
                                latitude: result.position.lat,
                                address: geoResult.regeocode.formattedAddress
                            };
                            error.value = null;
  } else {
                            error.value = '高德地图逆地理编码失败';
                        }
                        resolve();
                    });
    } else {
                    error.value = `高德地图定位失败: ${result.message}`;
                    reject(new Error(result.message));
                }
            });
        });
    });

  } catch (err) {
    error.value = '加载高德地图SDK失败，请检查网络或API Key。';
    console.error(err);
  }
};

const getLocationByBaidu = async () => {
  location.value = null;
  error.value = '正在使用百度地图获取位置...';
  try {
    if (!window.BMap) {
      // 百度地图的加载需要一个全局回调函数
      window.onBaiduMapLoaded = () => {
        resolveBaiduMapLoading();
      };
      await loadScript(`https://api.map.baidu.com/api?v=2.0&ak=${BAIDU_MAP_KEY}&callback=onBaiduMapLoaded`);
    } else {
       resolveBaiduMapLoading();
    }
  } catch (err) {
      error.value = '加载百度地图SDK失败，请检查网络或API Key。';
      console.error(err);
  }
};

const resolveBaiduMapLoading = () => {
    const geolocation = new window.BMap.Geolocation();
    geolocation.getCurrentPosition((r) => {
        if (geolocation.getStatus() == window.BMAP_STATUS_SUCCESS) {
            const geocoder = new window.BMap.Geocoder();
            geocoder.getLocation(r.point, (rs) => {
                location.value = {
                    longitude: r.point.lng,
                    latitude: r.point.lat,
                    address: rs.address
                };
                error.value = null;
            });
        } else {
            let errorMsg = '百度地图定位失败';
            switch(geolocation.getStatus()){
                case window.BMAP_STATUS_PERMISSION_DENIED:
                    errorMsg += ": 用户拒绝授权";
                    break;
                case window.BMAP_STATUS_SERVICE_UNAVAILABLE:
                    errorMsg += ": 定位服务不可用";
                    break;
                case window.BMAP_STATUS_TIMEOUT:
                    errorMsg += ": 定位超时";
                    break;
            }
            error.value = errorMsg;
        }
    });
}
</script>

<style scoped>
/* 可以在这里添加一些额外的样式 */
</style> 