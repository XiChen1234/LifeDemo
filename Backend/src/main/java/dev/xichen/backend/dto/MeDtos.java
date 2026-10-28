package dev.xichen.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public final class MeDtos {
    private MeDtos() {
    }

    public record MePatch(
            @Size(max = 64) String displayName,
            @Size(max = 200) String signature,
            @Min(2000) @Max(2100) Integer targetYear,
            String theme) {
    }

    public record IntegrationPatch(String status, String vaultPath) {
    }
}
