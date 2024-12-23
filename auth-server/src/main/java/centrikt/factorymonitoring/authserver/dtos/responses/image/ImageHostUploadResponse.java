package centrikt.factorymonitoring.authserver.dtos.responses.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ImageHostUploadResponse {
    @JsonProperty("data")
    private ImageData data;

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("status")
    private int status;


    @Setter
    @Getter
    public class ImageData {
        @JsonProperty("link")
        private String link;

    }
}

