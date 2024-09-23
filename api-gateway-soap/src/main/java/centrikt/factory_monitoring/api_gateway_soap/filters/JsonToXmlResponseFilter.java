package centrikt.factory_monitoring.api_gateway_soap.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;


@Component
public class JsonToXmlResponseFilter extends AbstractGatewayFilterFactory<JsonToXmlResponseFilter.Config> {

    public JsonToXmlResponseFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpResponse originalResponse = exchange.getResponse();
            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                public Mono<Void> writeWith(Flux<? extends DataBuffer> body) {
                    return DataBufferUtils.join(body).flatMap(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        DataBufferUtils.release(dataBuffer);
                        String jsonBody = new String(bytes, StandardCharsets.UTF_8);
                        String xmlBody = convertJsonToXml(jsonBody); // Ваша логика преобразования

                        byte[] newBytes = xmlBody.getBytes(StandardCharsets.UTF_8);
                        DataBuffer buffer = originalResponse.bufferFactory().wrap(newBytes);
                        originalResponse.getHeaders().set("Content-Type", "application/xml");
                        return originalResponse.writeWith(Flux.just(buffer));
                    });
                }
            };

            return chain.filter(exchange.mutate().response(decoratedResponse).build());
        };
    }

    private String convertJsonToXml(String json) {
        try {
            ObjectMapper jsonMapper = new ObjectMapper();
            JsonNode jsonNode = jsonMapper.readTree(json);
            XmlMapper xmlMapper = new XmlMapper();
            return "<soap-env:Envelope xmlns:soap-env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                    "<soap-env:Body>" + xmlMapper.writeValueAsString(jsonNode) + "</soap-env:Body></soap-env:Envelope>";

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "<error>Invalid JSON input</error>";
        }
    }

    public static class Config {
    }
}
