(function() {
Chart.defaults.global.defaultFontFamily = '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
Chart.defaults.global.defaultFontColor = '#292b2c';

let CpuIpData = [];
let CpuDateTimeData = [];
let cpuTempData = [];

// DATE 파싱
function parseDateTime(dateString) {
  const [datePart, timePart] = dateString.split(' ');
  const [year, month, day] = datePart.split('-');
  const [hour, minute, second] = timePart.split(':');
  return new Date(year, month - 1, day, hour, minute, second);
}

// IP 주소 목록 생성
async function getIpList() {
  const response = await fetch('/temps');
  const data = await response.json();
  const ipList = [...new Set(data.map((item) => item.ip))];
  return ipList;
}

async function fetchData() {
  const response = await fetch('/temps');
  const data = await response.json();

  const ipList = [...new Set(data.map((item) => item.ip))];
  const datasets = ipList.map(ip => {
    const color = generateRandomColor();
    const ipData = data.filter(item => item.ip === ip).slice(-12);

    return {
      label: `IP: ${ip}`,
      data: ipData.map(item => ({ x: item.dateTime ? parseDateTime(item.dateTime) : null, y: item.cpuTemp })),
      lineTension: 0.3,
      backgroundColor: color,
      borderColor: color,
      pointRadius: 5,
      pointBackgroundColor: color,
      pointBorderColor: 'rgba(255,255,255,0.8)',
      pointHoverRadius: 5,
      pointHoverBackgroundColor: color,
      pointHitRadius: 50,
      pointBorderWidth: 2,
      fill: false,
    };
  });

  CpuLineChart.data.datasets = datasets;
  CpuLineChart.options.scales.xAxes[0].ticks.maxTicksLimit = 12;
  CpuLineChart.update();
}

function updateChartData() {
  CpuLineChart.data.labels = CpuDateTimeData;
  CpuLineChart.data.datasets[0].label = 'CPU 온도';
  CpuLineChart.data.datasets[0].data = cpuTempData;
  CpuLineChart.update();
}

var ctx = document.getElementById('CPUChart');
var CpuLineChart = new Chart(ctx, {
  type: 'line',
  data: {
    labels: CpuDateTimeData,
    datasets: [
//    {
//      label: 'CPU 온도',
//      lineTension: 0.3,
//      backgroundColor: 'rgba(2,117,216,0.2)',
//      borderColor: 'rgba(2,117,216,1)',
//      pointRadius: 5,
//      pointBackgroundColor: 'rgba(2,117,216,1)',
//      pointBorderColor: 'rgba(255,255,255,0.8)',
//      pointHoverRadius: 5,
//      pointHoverBackgroundColor: 'rgba(2,117,216,1)',
//      pointHitRadius: 50,
//      pointBorderWidth: 2,
//      data: cpuTempData,
//    }
    ],
  },
  options: {
    scales: {
      xAxes: [{
        type: 'time',
        time: {
          unit: 'minute',
          displayFormats: {
            minute: 'hh:mm'
          }
        },
        distribution: 'linear',
        ticks: {
          source: 'data',
          autoSkip: true,
          maxTicksLimit: 12
        },
        gridLines: {
          display: false
        },
      }],
      yAxes: [{
        ticks: {
          min: 0,
          max: 100,
          maxTicksLimit: 10
        },
        gridLines: {
          color: 'rgba(0, 0, 0, .125)',
        }
      }],
    },
    legend: {
      display: true
    }
  }

});

fetchData();
setInterval(fetchData, 10000);

function generateRandomColor() {
  return `rgba(${Math.floor(Math.random() * 256)}, ${Math.floor(Math.random() * 256)}, ${Math.floor(Math.random() * 256)}, 0.7)`;
}

})();