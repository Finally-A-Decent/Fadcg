package info.asdev.fadcr.polymart;

import com.google.gson.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PolymartAPI {
    public static final String BASE_URL = "https://api.polymart.org/v1/";
    private static final Gson gson =  new Gson();

    public static boolean checkApiStatus() {
        return Validate.notNull(getJsonResponse("status", null)).get("success").getAsBoolean();
    }

    public static JsonObject search(String query) {
        return search(query, "relevant", false, 0L, 0, 25, null);
    }
    public static JsonObject search(String query, String sorting) {
        return search(query, sorting, false, 0L, 0, 25, null);
    }
    public static JsonObject search(String query, String sorting, boolean premium) {
        return search(query, sorting, premium, 0L, 0, 25, null);
    }
    public static JsonObject search(String query, String sorting, boolean premium, long referrer) {
        return search(query, sorting, premium, referrer, 0, 25, null);
    }
    public static JsonObject search(String query, String sorting, boolean premium, long referrer, int start) {
        return search(query, sorting, premium, referrer, start, 25, null);
    }
    public static JsonObject search(String query, String sorting, boolean premium, long referrer, int start, int limit) {
        return search(query, sorting, premium, referrer, start, Math.clamp(limit, 5, 25), null);
    }
    public static JsonObject search(String query, String sorting, boolean premium, long referrer, int start, int limit, String token) {
        return getJsonResponse("search", SearchParams.builder()
                .query(query)
                .sorting(sorting)
                .premium(premium ? 1 : 0)
                .referrer(referrer)
                .start(start)
                .limit(limit)
                .token(token)
                .build().asJson()
        );
    }

    public static boolean verifyPurchase(String license, String license_number) {
        return getJsonResponse("verifyPurchase", LicenseParams.builder()
                .license(license)
                .license_number(license_number)
                .build().asJson()
        ).get("success").getAsBoolean();
    }
    public static boolean verifyPurchase(String inject_version, String resource_id, String user_id, String nonce, String download_agent, String download_time, String download_token) {
        return getJsonResponse("verifyPurchase", AlternativeLicenseParams.builder()
                .inject_version(inject_version)
                .resource_id(resource_id)
                .user_id(user_id)
                .nonce(nonce)
                .download_agent(download_agent)
                .download_time(download_time)
                .download_token(download_token)
                .build().asJson()
        ).get("success").getAsBoolean();
    }

    public static String getPremiumDownloadUrl(boolean allowRedirects, String inject_version, String resource_id, String user_id, String nonce, String download_agent, String download_time, String download_token) {
        JsonObject object = getJsonResponse("requestUpdateURL", UpdateUrlParams.builder()
                .allowRedirects(allowRedirects ? 1 : 0)
                .inject_version(inject_version)
                .resource_id(resource_id)
                .user_id(user_id)
                .nonce(nonce)
                .download_agent(download_agent)
                .download_time(download_time)
                .download_token(download_token)
                .build().asJson()
        );

        if (!object.get("success").getAsBoolean()) {
            return null;
        }

        return object.get("url").getAsString();
    }

    @Nullable
    public static JsonObject getJsonResponse(String endpoint, @Nullable String jsonParams) {
        JsonObject responseObject = null;

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest.Builder builder = HttpRequest
                    .newBuilder(URI.create(BASE_URL + endpoint))
                    .header("Content-Type", "application/json");

            if (jsonParams != null) {
                HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(jsonParams);
                builder.POST(publisher);
            } else {
                builder.GET();
            }

            HttpRequest request = builder.build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String json = response.body();
            responseObject = JsonParser.parseString(json).getAsJsonObject().getAsJsonObject("response");
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        return responseObject;
    }

    private static class Jsonable {
        public String asJson() {
            return PolymartAPI.gson.toJson(this);
        }
    }

    @AllArgsConstructor
    @Builder
    public static class SearchParams extends Jsonable {
        private String query, sorting, token;
        private int premium;
        private long referrer;
        private int start, limit;
    }
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    private static class LicenseParams extends Jsonable {
        private String license, license_number;
    }
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    private static class AlternativeLicenseParams extends Jsonable {
        private String inject_version, resource_id, user_id, nonce, download_agent, download_time, download_token;
    }
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    private static class UpdateUrlParams extends Jsonable  {
        private String inject_version, resource_id, user_id, nonce, download_agent, download_time, download_token;
        private int allowRedirects = 0;
    }

}
