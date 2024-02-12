package cl.previred.challenge.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates a Chilean RUT (Rol Único Tributario) for correctness.
 */
public class RutValidator implements ConstraintValidator<ValidRut, String> {

    @Override
    public void initialize(ValidRut constraintAnnotation) {
        // No initialization required for this validator.
    }

    /**
     * Validates a Chilean RUT (Rol Único Tributario) for correctness.
     * @param rut The RUT to validate, expected in the format 'number-number' or 'numberK'.
     * @return true if the RUT is valid, false otherwise.
     */
    @Override
    public boolean isValid(String rut, ConstraintValidatorContext context) {
        if (rut == null || rut.isEmpty()) {
            return false;
        }

        try {

            // Check if the RUT contains exactly one hyphen and it's before the check digit.
            int hyphenIndex = rut.lastIndexOf('-');
            if (hyphenIndex == -1 || hyphenIndex != rut.length() - 2) {
                // If there's no hyphen or its position is not right before the check digit, return false.
                return false;
            }

            // Standardize the RUT string: uppercase and remove dots and hyphen.
            rut = rut.toUpperCase().replace(".", "").replace("-", "");

            // Extract the numerical part of the RUT, excluding the check digit.
            int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));

            // Extract the check digit from the RUT.
            char dv = rut.charAt(rut.length() - 1);

            // Initialize variables for the calculation.
            int m = 0;
            int s = 1;
            for (; rutAux != 0; rutAux /= 10) { // Process each digit of the RUT.
                // Calculate the sum 's' using each digit, multiplying by 2 to 7 cyclically.
                s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
            }
            // Calculate the check digit and compare it with the provided one.
            // If 's' is not 0, the check digit is calculated as 's + 47' to get the ASCII code of the numeric digit or 'K'.
            // 'K' corresponds to ASCII 75, and numeric digits start from ASCII 48 ('0').
            if (dv == (char) (s != 0 ? s + 47 : 75)) {
                return true; // The RUT is valid if the calculated check digit matches the provided one.
            }

        } catch (Exception e) {
            return false;
        }
        return false;
    }
}