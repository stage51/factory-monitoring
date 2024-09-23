package centrikt.factory_monitoring.api_gateway_soap.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class XmlToJsonRequestFilter extends AbstractGatewayFilterFactory<XmlToJsonRequestFilter.Config> {

    public XmlToJsonRequestFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            return DataBufferUtils.join(request.getBody()).flatMap(dataBuffer -> {
                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                DataBufferUtils.release(dataBuffer);
                String xmlBody = new String(bytes, StandardCharsets.UTF_8);
                String jsonBody = convertXmlToJson(xmlBody); // Ваша логика преобразования

                ServerHttpRequest modifiedRequest = new ServerHttpRequestDecorator(request) {
                    @Override
                    public Flux<DataBuffer> getBody() {
                        byte[] bytes = jsonBody.getBytes(StandardCharsets.UTF_8);
                        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                        return Flux.just(buffer);
                    }
                };

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            });
        };
    }

    private String convertXmlToJson(String xml) {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            JsonNode xmlNode = xmlMapper.readTree(xml.getBytes());
            ObjectMapper jsonMapper = new ObjectMapper();
            return jsonMapper.writeValueAsString(xmlNode);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{\"error\": \"Invalid XML input\"}";
        } catch (IOException e) {
            return "{\"error\": \"IO Exception\"}";
        }
    }

    public static class Config {
    }
}
