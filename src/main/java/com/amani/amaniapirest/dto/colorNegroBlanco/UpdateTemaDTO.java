package com.amani.amaniapirest.dto.colorNegroBlanco;

import com.amani.amaniapirest.enums.TemaApp;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTemaDTO
{
    @NotNull
    @Schema(
            description = "Tema visual seleccionado",
            example = "DARK",
            allowableValues = {
                    "LIGHT",
                    "DARK",
                    "SYSTEM"
            }
    )
    private TemaApp tema;
}
