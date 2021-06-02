package ohconstant.springmvc.basic.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ohconstant.springmvc.basic.HelloData;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
public class RequestBodyJsonController {
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
    * HttpServletRequest를 통해 직접 메세지를 읽어서 처리
    * */
    @PostMapping("/request-body-json-v1")
    public void requestBodyJsonV1(HttpServletRequest request, HttpServletResponse response) throws IOException{
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        log.info("messageBody={}", messageBody);

        HelloData data = objectMapper.readValue(messageBody, HelloData.class);

        log.info("username={}, age={}", data.getUsername(), data.getAge());

        response.getWriter().write("ok");
    }

    /**
     * @RequestBody
     *  HttpMessageConvert 사용 -> StringHttpMessageConverter
     * */
    @ResponseBody
    @PostMapping("/request-body-json-v2")
    public String requestBodyJsonV2(@RequestBody String messageBody) throws IOException{

        HelloData data = objectMapper.readValue(messageBody, HelloData.class);

        log.info("username={}, age={}", data.getUsername(), data.getAge());

        return "ok";
    }

    /**
     * @RequestBody에서 데이터를 뽑아서 JSON으로 만드는 과정을 단순화
     *
     * @RequestBody가 생략되면 @ModelAttribute가 적용된다.
     *
     * HttpMessageConvert 사용 -> MappingJackson2HttpMessageConverter (content-type: application/json)
     * */
    @ResponseBody
    @PostMapping("/request-body-json-v3")
    public String requestBodyJsonV3(@RequestBody HelloData data){

        log.info("username={}, age={}", data.getUsername(), data.getAge());

        return "ok";
    }

    /**
     * 물론 HttpEntity에서 getBody를 통해서도 사용이 가능하다.
     * */
    @ResponseBody
    @PostMapping("/request-body-json-v4")
    public String requestBodyJsonV4(HttpEntity<HelloData> httpEntity){
        HelloData data = httpEntity.getBody();

        log.info("username={}, age={}", data.getUsername(), data.getAge());

        return "ok";
    }

    /**
     * @RequestBody 생략 불가능(@ModelAttribute 가 적용되어 버림)
     * HttpMessageConverter 사용 -> MappingJackson2HttpMessageConverter (content-type: application/json)
     *
     * @ResponseBody 적용
     * - 메시지 바디 정보 직접 반환(view 조회X)
     * - HttpMessageConverter 사용 -> MappingJackson2HttpMessageConverter 적용(Accept: application/json)
     *
     *  return도 json객체가 된다.
     */
    @ResponseBody
    @PostMapping("/request-body-json-v5")
    public HelloData requestBodyJsonV5(@RequestBody HelloData data) {
        log.info("username={}, age={}", data.getUsername(), data.getAge());
        return data;
    }
}
