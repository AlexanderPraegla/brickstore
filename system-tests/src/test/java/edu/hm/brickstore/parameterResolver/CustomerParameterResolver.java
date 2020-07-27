package edu.hm.brickstore.parameterResolver;

import edu.hm.brickstore.account.dto.CustomerDTO;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Random;

public class CustomerParameterResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == CustomerDTO.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Object ret = null;
        if (parameterContext.getParameter().getType() == CustomerDTO.class) {
            ret = VALID_CUSTOMERS[new Random().nextInt(VALID_CUSTOMERS.length)];
        }
        return ret;
    }

    public static final CustomerDTO[] VALID_CUSTOMERS = {
            new CustomerDTO("Martin", "Maier", "Martin.Maier@test.com"),
            new CustomerDTO("Michael", "Schmidt", "Michael.Schmidt@test.com"),
            new CustomerDTO("Peter", "Müller", "Peter.Müller@test.com"),
            new CustomerDTO("Anja", "Bauer", "Anja.Bauer@test.com"),
            new CustomerDTO("Claudia", "Müller", "Claudia.Müller@test.com"),
            new CustomerDTO("Osker", "Müller", "Osker.Müller@test.com"),
            new CustomerDTO("Moritz", "Bauer", "Moritz.Bauer@test.com"),
            new CustomerDTO("Felix", "Bauer", "Felix.Bauer@test.com"),
            new CustomerDTO("Tobias", "Bauer", "Tobias.Bauer@test.com"),
            new CustomerDTO("Peter", "Bauer", "Peter.Bauer@test.com"),
            new CustomerDTO("Marion", "Bauer", "Marion.Bauer@test.com"),
            new CustomerDTO("Erik", "Bauer", "Erik.Bauer@test.com"),
            new CustomerDTO("Natalie", "Bauer", "Natalie.Bauer@test.com"),
            new CustomerDTO("Hans", "Schmidt", "Hans.Schmidt@test.com"),
            new CustomerDTO("Erik", "Schmidt", "Erik.Schmidt@test.com"),
            new CustomerDTO("Birgit", "Schmidt", "Birgit.Bauer@test.com"),
            new CustomerDTO("Martin", "Schmidt", "Martin.Schmidt@test.com")
    };

}
