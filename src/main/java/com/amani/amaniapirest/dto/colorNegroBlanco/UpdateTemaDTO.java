package com.amani.amaniapirest.dto.colorNegroBlanco;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTemaDTO
{    @NotNull
    @Schema(description = "Indica si el usuario prefiere el tema oscuro (true) o el tema claro (false).", example = "true")
    private Boolean tema;
}
