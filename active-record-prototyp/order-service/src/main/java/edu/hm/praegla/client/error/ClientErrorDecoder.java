package edu.hm.praegla.client.error;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hm.praegla.error.ApiError;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.Charset;

public class ClientErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            if (response.status() == 400) {
                String responseBodyString = IOUtils.toString(response.body().asReader(Charset.defaultCharset()));
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                ApiError apiError = objectMapper.readValue(responseBodyString, ApiError.class);
                return new FeignBadRequestException(apiError.getMessage(), apiError.getResponseCode(), apiError.getStatus());
            } else {
                return new Exception(response.reason());
            }
        } catch (IOException e) {
            return new Exception("Error response could not be deserialized", e);
        }
    }
}
