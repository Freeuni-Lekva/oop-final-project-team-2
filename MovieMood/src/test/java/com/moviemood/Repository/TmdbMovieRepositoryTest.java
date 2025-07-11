package com.moviemood.Repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moviemood.Enums.MovieCategory;
import com.moviemood.bean.Movie;
import com.moviemood.config.LocalDateAdapter;
import com.moviemood.repository.tmdb.TmdbMovieRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TmdbMovieRepositoryTest {

    private static MockWebServer mockWebServer;
    private static TmdbMovieRepository repository;
    private static Gson gson;

    @BeforeAll
    static void setup() throws IOException, NoSuchFieldException, IllegalAccessException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String fakeBaseUrl = mockWebServer.url("/").toString();
        String fakeApiKey = "fake-api-key";

        repository = TmdbMovieRepository.getInstance();

        // Use reflection to override private final fields
        Field baseUrlField = TmdbMovieRepository.class.getDeclaredField("BASE_URL");
        baseUrlField.setAccessible(true);
        setFinalField(baseUrlField, repository, fakeBaseUrl);

        Field apiKeyField = TmdbMovieRepository.class.getDeclaredField("apiKey");
        apiKeyField.setAccessible(true);
        setFinalField(apiKeyField, repository, fakeApiKey);

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    private static void setFinalField(Field field, Object target, Object value) throws IllegalAccessException {
        field.setAccessible(true);
        Field modifiersField;
        try {
            modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
        } catch (NoSuchFieldException ignored) {
            // for JDK 12+, this isn't needed
        }
        field.set(target, value);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testFetchAll_Success() throws IOException {
        String json = "{"
                + "\"results\": ["
                + "  {"
                + "    \"id\": 1,"
                + "    \"title\": \"Sample Movie\","
                + "    \"overview\": \"This is a test movie.\","
                + "    \"release_date\": \"2022-01-01\""
                + "  }"
                + "]"
                + "}";

        mockWebServer.enqueue(new MockResponse()
                .setBody(json)
                .addHeader("Content-Type", "application/json"));

        List<Movie> result = repository.fetchAll(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Sample Movie", result.get(0).getTitle());
    }

    @Test
    void testFetchByCategory_Success() throws IOException {
        String json = "{"
                + "\"results\": ["
                + "  {"
                + "    \"id\": 2,"
                + "    \"title\": \"Another Movie\","
                + "    \"overview\": \"Test overview.\","
                + "    \"release_date\": \"2022-01-02\""
                + "  }"
                + "]"
                + "}";

        mockWebServer.enqueue(new MockResponse()
                .setBody(json)
                .addHeader("Content-Type", "application/json"));

        List<Movie> result = repository.fetchByCategory(MovieCategory.POPULAR, 1);

        assertEquals(1, result.size());
        assertEquals("Another Movie", result.get(0).getTitle());
    }

    @Test
    void testSearch_Success() throws IOException {
        String json = "{"
                + "\"results\": ["
                + "  {"
                + "    \"id\": 3,"
                + "    \"title\": \"Search Movie\","
                + "    \"overview\": \"From search.\","
                + "    \"release_date\": \"2022-01-03\""
                + "  }"
                + "]"
                + "}";

        mockWebServer.enqueue(new MockResponse()
                .setBody(json)
                .addHeader("Content-Type", "application/json"));

        List<Movie> result = repository.search("Search", 1);

        assertEquals(1, result.size());
        assertEquals("Search Movie", result.get(0).getTitle());
    }

    @Test
    void testFetchYoutubeTrailerKey_Success() throws IOException {
        String json = "{"
                + "  \"results\": ["
                + "    {"
                + "      \"site\": \"YouTube\","
                + "      \"type\": \"Trailer\","
                + "      \"key\": \"abc123\""
                + "    },"
                + "    {"
                + "      \"site\": \"YouTube\","
                + "      \"type\": \"Teaser\","
                + "      \"key\": \"xyz456\""
                + "    }"
                + "  ]"
                + "}";

        mockWebServer.enqueue(new MockResponse()
                .setBody(json)
                .addHeader("Content-Type", "application/json"));

        Optional<String> trailerKey = repository.fetchYoutubeTrailerKey(123);

        assertTrue(trailerKey.isPresent());
        assertEquals("abc123", trailerKey.get());
    }

    @Test
    void testFetchYoutubeTrailerKey_NoTrailerFound() throws IOException {
        String json = "{"
                + "  \"results\": ["
                + "    {"
                + "      \"site\": \"Vimeo\","
                + "      \"type\": \"Clip\","
                + "      \"key\": \"nope\""
                + "    }"
                + "  ]"
                + "}";

        mockWebServer.enqueue(new MockResponse()
                .setBody(json)
                .addHeader("Content-Type", "application/json"));

        Optional<String> trailerKey = repository.fetchYoutubeTrailerKey(456);

        assertTrue(trailerKey.isEmpty());
    }

    @Test
    void testFindById_Success() throws IOException {
        String json = "{"
                + "  \"id\": 5,"
                + "  \"title\": \"Found Movie\","
                + "  \"overview\": \"By ID\","
                + "  \"release_date\": \"2022-01-05\""
                + "}";

        mockWebServer.enqueue(new MockResponse()
                .setBody(json)
                .addHeader("Content-Type", "application/json"));

        Optional<Movie> movie = repository.findById(5);

        assertTrue(movie.isPresent());
        assertEquals("Found Movie", movie.get().getTitle());
    }
}
