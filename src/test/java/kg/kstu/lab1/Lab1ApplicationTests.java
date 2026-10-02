package kg.kstu.lab1;

import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Lab1ApplicationTests {
    @Autowired private TestRestTemplate http;
    @Autowired private JdbcTemplate jdbc;

    @Test
    void emptyApplicationStartsAndServesHttp() {
        // В лабораторной 1 контроллеров ещё нет, поэтому ожидается HTTP 404.
        assertThat(http.getForEntity("/", String.class).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Transactional
    void taskFieldsRoundTripThroughPostgres() {
        String description = "Описание задачи на русском. ".repeat(30);
        Long id = jdbc.queryForObject(
                "INSERT INTO tasks(name, description, due_date, completed) VALUES (?, ?, ?, ?) RETURNING id",
                Long.class, "Первая лабораторная", description, LocalDate.of(2026, 10, 2), false);
        Map<String, Object> task = jdbc.queryForMap("SELECT * FROM tasks WHERE id = ?", id);
        assertThat(task).hasSize(5);
        assertThat(task.get("name")).isEqualTo("Первая лабораторная");
        assertThat(task.get("description")).isEqualTo(description);
        assertThat(task.get("due_date")).isEqualTo(java.sql.Date.valueOf("2026-10-02"));
        assertThat(task.get("completed")).isEqualTo(false);
        jdbc.update("UPDATE tasks SET completed = true WHERE id = ?", id);
        assertThat(jdbc.queryForObject("SELECT completed FROM tasks WHERE id = ?", Boolean.class, id)).isTrue();
        jdbc.update("DELETE FROM tasks WHERE id = ?", id);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM tasks WHERE id = ?", Long.class, id)).isZero();
        // Транзакция теста откатывается автоматически.
    }
}
