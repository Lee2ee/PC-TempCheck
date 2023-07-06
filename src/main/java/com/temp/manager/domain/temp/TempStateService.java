package com.temp.manager.domain.temp;

import com.temp.manager.domain.Temp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TempStateService {
    private static final float THRESHOLD = 60.0f;

    @Autowired
    private TempRepository tempRepository;

    @Scheduled(cron = "30 */5 * * * *")
    public void updateTemperatureAndState() {
        // 모든 IP 목록 가져오기
        List<String> allIps = tempRepository.findAllIps();

        for (String ip : allIps) {
            List<Temp> last12Datas = tempRepository.findTop12ByIpOrderByDateTimeDesc(ip);

            if (last12Datas.isEmpty()) continue;

            Temp lastData = last12Datas.get(0);
            Temp secondLastData = last12Datas.size() >= 2 ? last12Datas.get(1) : null;

            Float cpuTemp = lastData.getCpuTemp();
            Float gpuTemp = lastData.getGpuTemp();
            String state = lastData.getState();

            if (cpuTemp != null && gpuTemp != null) {
                if ((secondLastData == null || (secondLastData.getCpuTemp() < THRESHOLD && secondLastData.getGpuTemp() < THRESHOLD)) &&
                        (cpuTemp >= THRESHOLD || gpuTemp >= THRESHOLD) &&
                        ("normal".equals(state) || "stop".equals(state))) {
                    // 2번째 데이터가 없거나 온도가 기준 온도 미만이고 마지막 데이터의 온도가 임계치를 넘으면 run 상태로 변경
                    tempRepository.updateNormalStateById(lastData.getId(), "run");
                } else if (secondLastData != null &&
                        (secondLastData.getCpuTemp() >= THRESHOLD || secondLastData.getGpuTemp() >= THRESHOLD) &&
                        (cpuTemp < THRESHOLD && gpuTemp < THRESHOLD)) {
                    // 2번째 마지막 데이터의 온도가 이상이고 마지막 데이터의 온도가 기준 온도 미만일 때, 상태를 'stop'으로 변경
                    tempRepository.updateNormalStateById(lastData.getId(), "stop");
                }
            }

            // 5분이 지나면 'off'로 간주합니다. 조건을 조정하거나 설정 파일에서 관리할 수 있습니다.
            if (Duration.between(lastData.getDateTime(), LocalDateTime.now()).toMinutes() >= 5 && !"off".equals(lastData.getState())) {
                Temp newData = Temp.builder()
                        .ip(ip)
                        .dateTime(LocalDateTime.now())
                        .state("off")
                        .build();
                tempRepository.save(newData);
            }
        }
    }
}
