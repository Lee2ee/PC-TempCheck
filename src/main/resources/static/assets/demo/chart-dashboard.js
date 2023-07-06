document.addEventListener('DOMContentLoaded', function() {
// CPU 차트
var cpuChart = echarts.init(document.getElementById('CPUChart'));
var gpuChart = echarts.init(document.getElementById('GPUChart'));

// 옵션 설정
var option = {
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross',
      label: {
        backgroundColor: '#283b56',
      },
    },
    formatter: function (params) {
      const dateString = params[0].data[2].replace("T", " ");
      let tooltipText = `${dateString}<br/>`;

      params.forEach(param => {
        tooltipText += `<br>${param.marker} ${param.seriesName}: ${param.data[1]}°C`;
      });
      return tooltipText;
    },
  },
  legend: {
    data: [],
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: [],
  },
  yAxis: {
    type: 'value',
    scale: true,
    boundaryGap: [0.2, 0.2],
    axisLabel: {
      formatter: '{value}°C',
    },
  },
  series: [], // 추가 된 series
};

// CPU 차트 옵션 설정
cpuChart.setOption(option);

// GPU 차트 옵션 설정
gpuChart.setOption(option); // 수정 된 GPU 차트 옵션 설정

function getTodayDate() {
  const today = new Date();
  const dd = String(today.getDate()).padStart(2, '0');
  const mm = String(today.getMonth() + 1).padStart(2, '0');
  const yyyy = today.getFullYear();

  return yyyy + '-' + mm + '-' + dd;
}

// 데이터 가져오기 함수
async function fetchData() {
   const response = await fetch('/temps');
   let data = await response.json();

   console.log(data);

   // 오늘 날짜의 데이터만 가져오기
   const todayDate = getTodayDate();
   data = data.filter(item => item.dateTime.startsWith(todayDate));

  const tempData = {};
  let latestDataTime = new Date();

  // 데이터를 시간 순으로 정렬
  data.sort((a, b) => new Date(a.dateTime) - new Date(b.dateTime));

  // 각 IP별 가장 최근 저장된 데이터를 추적할 객체
  const latestSavedData = {};

  data.forEach((item) => {
    const ip = item.ip;

    if (!tempData[ip]) {
      tempData[ip] = {
        label: `IP: ${ip}`,
        cpuData: {},
        gpuData: {},
      };
    }

    const timeStamp = new Date(item.dateTime);
    const hhmm = ("0" + timeStamp.getHours()).slice(-2) + ":" + ("0" + (Math.floor(timeStamp.getMinutes() / 5) * 5)).slice(-2);
    const cpuValue = [item.cpuTemp, item.dateTime];
    const gpuValue = [item.gpuTemp, item.dateTime];

    latestDataTime = latestDataTime > timeStamp ? latestDataTime : timeStamp;

    if (!tempData[ip].cpuData[hhmm] || tempData[ip].cpuData[hhmm][0] < cpuValue[0]) {
      tempData[ip].cpuData[hhmm] = cpuValue;
    }

    if (!tempData[ip].gpuData[hhmm] || tempData[ip].gpuData[hhmm][0] < gpuValue[0]) {
      tempData[ip].gpuData[hhmm] = gpuValue;
    }

    // 가장 최근 저장된 데이터 추적
    if (!latestSavedData[ip] || latestSavedData[ip].dateTime < item.dateTime) {
      latestSavedData[ip] = {
        cpuTemp: item.cpuTemp,
        gpuTemp: item.gpuTemp,
        dateTime: item.dateTime,
      };
    }
  });

  // 차트 설정 업데이트
  setChartData(tempData, "cpu", cpuChart, latestDataTime);
  setChartData(tempData, "gpu", gpuChart, latestDataTime);
}

function setTimeLabels(dateTime) {
  const timeStamp = new Date(dateTime);
  const baseMinute = Math.floor(timeStamp.getMinutes() / 5) * 5;
  timeStamp.setMinutes(baseMinute);
  const timeLabels = [];
  for (let i = 0; i < 13; i++) {
    const hhmm = ("0" + timeStamp.getHours()).slice(-2) + ":" + ("0" + timeStamp.getMinutes()).slice(-2);
    timeLabels.unshift(hhmm);
    timeStamp.setMinutes(timeStamp.getMinutes() - 5);
  }
  return timeLabels;
}

function setChartData(tempData, dataType, chart, dateTime) {
  const timeLabels = setTimeLabels(dateTime);
  const datasets = Object.values(tempData).map((item) => {
    const currentData = dataType === "cpu" ? item.cpuData : item.gpuData;
    return {
      name: item.label,
      type: 'line',
      smooth: true,
      data: Object.entries(currentData)
        .map(([name, values]) => [name, values[0], values[1]])
        .filter(([name, _]) => timeLabels.includes(name))
        .sort(),
    };
  });

  option.xAxis.data = timeLabels;
  option.legend.data = datasets.map(dataset => dataset.name);
  option.series = datasets;
  chart.setOption(option);
}

// 첫 데이터 가져오기 호출
fetchData();

// 시간을 확인하고 5분 간격에 맞게 실행되도록 설정
function syncFetchData() {
  const now = new Date();
  const minutes = now.getMinutes();
  const seconds = now.getSeconds();
  const milliseconds = now.getMilliseconds();
  const interval = 5 - (minutes % 5);
  const waitTime = interval * 60 * 1000 - seconds * 1000 - milliseconds; // 수정된 부분

  setTimeout(() => {
    fetchData();
    // 이후엔 정확히 5분 간격으로 fetchData를 실행
    setInterval(fetchData, 5 * 60 * 1000 + 10000);
  }, waitTime);
}

syncFetchData(); // 함수를 호출하여 실행
});