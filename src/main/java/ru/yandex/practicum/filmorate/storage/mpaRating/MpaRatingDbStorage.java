package ru.yandex.practicum.filmorate.storage.mpaRating;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class MpaRatingDbStorage implements MpaRatingStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaRatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<MpaRating> MPA_ROW_MAPPER = (rs, rowNum) -> {
        MpaRating mpa = new MpaRating();
        mpa.setId(rs.getInt("id"));
        mpa.setName(rs.getString("name"));
        return mpa;
    };

    @Override
    public Collection<MpaRating> findAll() {
        String sql = "SELECT * FROM mpa_ratings ORDER BY id";
        return jdbcTemplate.query(sql, MPA_ROW_MAPPER);
    }

    @Override
    public Optional<MpaRating> findById(Integer id) {
        String sql = "SELECT * FROM mpa_ratings WHERE id = ?";
        List<MpaRating> result = jdbcTemplate.query(sql, MPA_ROW_MAPPER, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }
}
