package dev.xichen.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class NoteDtos {
    private NoteDtos() {
    }

    public record NoteSaveRequest(
            @NotBlank @Size(max = 255) String title,
            @NotBlank String contentMarkdown,
            @Size(max = 20) String folder,
            String primaryTag) {
    }

    public record RescanResult(int indexed, String vaultPath) {
    }
}
