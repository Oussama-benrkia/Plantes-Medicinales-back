package ma.m3achaba.plantes.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponse {

    /**
     * Code d'erreur représentant le type d'exception.
     */
    private Integer code;

    /**
     * Message descriptif concernant l'exception.
     */
    private String message;

    /**
     * Détails supplémentaires concernant l'exception.
     */
    private String details;

    /**
     * Ensemble des messages d'erreur de validation.
     * <p>
     * Ce champ est utilisé pour capturer plusieurs erreurs de validation qui peuvent se produire lors du traitement de la requête.
     * </p>
     */
    private Set<String> validationError;

    /**
     * Carte des noms de champs aux messages d'erreur.
     * <p>
     * Ce champ est utilisé pour fournir des informations détaillées sur les erreurs spécifiques aux champs rencontrées lors de la validation.
     * </p>
     */
    private Map<String, String> erros;

    /**
     * Horodatage indiquant le moment où l'exception est survenue.
     */
    private LocalDateTime date;

    /**
     * Chemin de la requête qui a causé l'exception.
     */
    private String path;
}