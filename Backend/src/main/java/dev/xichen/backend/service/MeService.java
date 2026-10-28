package dev.xichen.backend.service;

import dev.xichen.backend.common.ApiException;
import dev.xichen.backend.common.CurrentUser;
import dev.xichen.backend.common.Formats;
import dev.xichen.backend.dto.MeDtos;
import dev.xichen.backend.entity.DataSource;
import dev.xichen.backend.entity.User;
import dev.xichen.backend.repository.DataSourceRepository;
import dev.xichen.backend.repository.UserRepository;
import dev.xichen.backend.view.PageViews;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class MeService {
    private final UserRepository userRepository;
    private final DataSourceRepository dataSourceRepository;
    private final JdbcTemplate jdbc;

    public MeService(UserRepository userRepository, DataSourceRepository dataSourceRepository, JdbcTemplate jdbc) {
        this.userRepository = userRepository;
        this.dataSourceRepository = dataSourceRepository;
        this.jdbc = jdbc;
    }

    public PageViews.Me profile() {
        User user = currentUser();
        long usedDays = user.getCreatedAt() == null ? 1
                : ChronoUnit.DAYS.between(user.getCreatedAt().toLocalDate(), LocalDate.now()) + 1;
        PageViews.Me.Profile profile = new PageViews.Me.Profile(user.getDisplayName(), user.getAvatarChar(),
                (int) usedDays, user.getTargetYear(), user.getSignature());

        LocalDateTime dataUpdatedAt = latestDataUpdate();
        PageViews.Me.Meta meta = new PageViews.Me.Meta("2.0.0", user.getTheme(),
                Formats.offset(user.getUpdatedAt()).toString(),
                dataUpdatedAt == null ? null : Formats.offset(dataUpdatedAt).toString(),
                "Spring Boot 4 · MySQL 8 · Vue 3");

        List<PageViews.Me.DataSourceRow> sources = new ArrayList<>();
        for (DataSource source : dataSourceRepository.findByUserIdOrderByIdAsc(CurrentUser.ID)) {
            String[] style = iconStyle(source.getSourceType());
            sources.add(new PageViews.Me.DataSourceRow(String.valueOf(source.getId()), style[0], style[1], style[2],
                    source.getName(), source.getDescription(), source.getStatus()));
        }
        return new PageViews.Me(profile, meta, sources);
    }

    public List<PageViews.Me.DataSourceRow> integrations() {
        return profile().dataSources();
    }

    @Transactional
    public PageViews.Me.Profile updateProfile(MeDtos.MePatch patch) {
        User user = currentUser();
        if (patch.displayName() != null) {
            user.setDisplayName(patch.displayName());
            user.setAvatarChar(patch.displayName().isBlank() ? "L" : patch.displayName().substring(0, 1));
        }
        if (patch.signature() != null) {
            user.setSignature(patch.signature());
        }
        if (patch.targetYear() != null) {
            user.setTargetYear(patch.targetYear());
        }
        if (patch.theme() != null) {
            user.setTheme(patch.theme());
        }
        userRepository.save(user);
        return profile().user();
    }

    @Transactional
    public PageViews.Me.DataSourceRow updateIntegration(String sourceType, MeDtos.IntegrationPatch patch) {
        DataSource source = dataSourceRepository.findByUserIdOrderByIdAsc(CurrentUser.ID).stream()
                .filter(item -> item.getSourceType().equals(sourceType))
                .findFirst()
                .orElseThrow(() -> ApiException.notFound("数据源不存在"));
        if (patch.status() != null) {
            source.setStatus(patch.status());
        }
        if (patch.vaultPath() != null) {
            source.setConfigJson("{\"vaultPath\":\"" + patch.vaultPath().replace("\"", "") + "\"}");
        }
        dataSourceRepository.save(source);
        String[] style = iconStyle(source.getSourceType());
        return new PageViews.Me.DataSourceRow(String.valueOf(source.getId()), style[0], style[1], style[2],
                source.getName(), source.getDescription(), source.getStatus());
    }

    private User currentUser() {
        return userRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> ApiException.notFound("用户不存在"));
    }

    private LocalDateTime latestDataUpdate() {
        return jdbc.queryForObject("""
                SELECT MAX(updated) FROM (
                  SELECT MAX(updated_at) AS updated FROM transactions WHERE user_id = ?
                  UNION ALL SELECT MAX(updated_at) FROM media_items WHERE user_id = ?
                  UNION ALL SELECT MAX(updated_at) FROM tasks WHERE user_id = ?
                  UNION ALL SELECT MAX(wake_at) FROM sleep_records WHERE user_id = ?
                ) t
                """, LocalDateTime.class, CurrentUser.ID, CurrentUser.ID, CurrentUser.ID, CurrentUser.ID);
    }

    private String[] iconStyle(String sourceType) {
        return switch (sourceType) {
            case "finance_api" -> new String[]{"¥", "icon-green", "green"};
            case "health_api" -> new String[]{"♥", "icon-blue", "blue"};
            case "obsidian" -> new String[]{"▤", "icon-yellow", "yellow"};
            default -> new String[]{"•", "icon-green", "green"};
        };
    }
}
