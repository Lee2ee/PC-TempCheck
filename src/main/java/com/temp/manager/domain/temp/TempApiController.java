package com.temp.manager.domain.temp;

import com.temp.manager.domain.Temp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/") // API 엔드포인트의 기본 경로를 설정합니다.
public class TempApiController {

    @Autowired
    private TempService tempService;

    @GetMapping("/temps") // 엔드포인트 'GET /api/temps'를 생성합니다.
    public List<Temp> getAllTemps() {
        return tempService.getAllTemps(); // 서비스 메소드를 호출하여 결과를 반환합니다.
    }

}

