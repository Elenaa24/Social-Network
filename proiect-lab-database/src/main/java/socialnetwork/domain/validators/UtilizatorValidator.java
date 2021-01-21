package socialnetwork.domain.validators;

import socialnetwork.domain.Utilizator;

import java.util.Vector;

/**
 * Valideaza un utilizator
 * Nu poate avea id-ul negativ, numele si prenumele nu trebuie sa fie nule si trebuie sa fie formate din litere de la A-Z
 */
public class UtilizatorValidator implements Validator<Utilizator> {
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        Vector<String> validationErrors = new Vector<String>();
        if(entity.getId()<0)
            validationErrors.add("ID negativ!");
        if(entity.getFirstName().equals("") || !entity.getFirstName().matches("[a-zA-Z]*"))
            validationErrors.add("Nume invalid!");
        if(entity.getLastName().equals("")|| !entity.getLastName().matches("[a-zA-Z]*"))
            validationErrors.add("Prenume invalid!");
        if(validationErrors.size()!=0)
            throw new ValidationException(validationErrors.toString());
    }
}