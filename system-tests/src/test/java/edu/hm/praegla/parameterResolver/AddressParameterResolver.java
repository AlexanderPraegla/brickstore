package edu.hm.praegla.parameterResolver;

import edu.hm.praegla.account.dto.AddressDTO;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Random;

public class AddressParameterResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == AddressDTO.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Object ret = null;
        if (parameterContext.getParameter().getType() == AddressDTO.class) {
            ret = VALID_ADDRESSES[new Random().nextInt(VALID_ADDRESSES.length)];
        }
        return ret;
    }

    public static final AddressDTO[] VALID_ADDRESSES = {
            new AddressDTO("Freising", "85354", "Holzgartenstraße 5a"),
            new AddressDTO("Freising", "85354", "Holzgartenstraße 6a"),
            new AddressDTO("Freising", "85354", "Holzgartenstraße 7a"),
            new AddressDTO("Freising", "85354", "Holzgartenstraße 8a"),
            new AddressDTO("Freising", "85354", "Holzgartenstraße 9a"),
            new AddressDTO("Freising", "85354", "Holzgartenstraße 10a"),
            new AddressDTO("Berlin", "10117", "Unter den Linden 5"),
            new AddressDTO("Berlin", "10117", "Unter den Linden 6"),
            new AddressDTO("Berlin", "10117", "Unter den Linden 7"),
            new AddressDTO("Berlin", "10117", "Unter den Linden 8"),
            new AddressDTO("Berlin", "10117", "Unter den Linden 9"),
            new AddressDTO("Berlin", "10117", "Unter den Linden 10"),
            new AddressDTO("Berlin", "10117", "Unter den Linden 11"),
            new AddressDTO("Erding", "85435", "Lange Zeile 1a"),
            new AddressDTO("Erding", "85435", "Lange Zeile 1b"),
            new AddressDTO("Erding", "85435", "Lange Zeile 2a"),
            new AddressDTO("Erding", "85435", "Lange Zeile 17"),
            new AddressDTO("Erding", "85435", "Lange Zeile 10")
    };

}
