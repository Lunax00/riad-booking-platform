package ma.lunaire.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {


    @NotBlank(message = "Email ne peut pas être vide")
    @Email(message = "Email doit être valide")
    private String email;


    @Size(min = 1, max = 100, message = "Prénom doit être entre 1 et 100 caractères")
    private String firstName;


    @Size(min = 1, max = 100, message = "Nom doit être entre 1 et 100 caractères")
    private String lastName;

    @Pattern(
            regexp = "^[+]?[0-9]{10,15}$",
            message = "Numéro de téléphone invalide"
    )
    private String phoneNumber;

    @Size(max = 255, message = "Adresse trop longue")
    private String address;

    @Size(max = 100, message = "Ville trop longue")
    private String city;

    private String profilePictureUrl;

    @Size(max = 5000, message = "Bio trop longue")
    private String bio;
}